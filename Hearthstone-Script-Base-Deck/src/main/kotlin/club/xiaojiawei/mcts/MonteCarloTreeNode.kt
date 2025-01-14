package club.xiaojiawei.mcts

import club.xiaojiawei.bean.*
import club.xiaojiawei.config.log
import club.xiaojiawei.enums.CardTypeEnum
import club.xiaojiawei.status.War
import club.xiaojiawei.util.WarUtil
import club.xiaojiawei.util.isFalse
import club.xiaojiawei.util.isTrue
import java.util.*

/**
 * @author 肖嘉威
 * @date 2025/1/10 16:27
 */
class MonteCarloTreeNode(war: War, action: Action, var parent: MonteCarloTreeNode? = null) {

    val applyAction: Action = action

    val children: MutableList<MonteCarloTreeNode> = mutableListOf()

    val state: State = State(war)

    val actions: List<Action> = generateActions(war)

    private var actionsExpandedFlag: BitSet = BitSet(actions.size)

    private fun generateActions(war: War): MutableList<Action> {
        val result = mutableListOf<Action>()
        val me = war.me
        val handArea = me.handArea
        val playArea = me.playArea
        if (applyAction === TurnOverAction) return result
//        result.add(TurnOverAction)
        handArea.cards.forEach { card ->
            if (me.usableResource >= card.cost && (!playArea.isFull || card.cardType === CardTypeEnum.HERO || card.cardType === CardTypeEnum.SPELL || card.cardType === CardTypeEnum.WEAPON)) {
                result.addAll(
                    card.action.createPlayActions() ?: this.generateDefaultPlayActions(
                        card.entityId
                    )
                )
            }
        }
        playArea.cards.forEach { card ->
            if (card.canAttack() && card.isSurvival()) {
                result.addAll(
                    card.action.createAttackActions() ?: this.generateDefaultAttackActions(
                        card.entityId
                    )
                )
            }
        }
        playArea.hero?.let { myHero ->
            if (myHero.canAttack() && myHero.isSurvival()) {
                result.addAll(
                    myHero.action.createAttackActions() ?: this.generateDefaultAttackActions(
                        myHero.entityId
                    )
                )
            }
        }
        playArea.power?.let { myPower ->
            if (me.usableResource >= myPower.cost && !myPower.isExhausted) {
                myPower.action.createPlayActions()
            }
        }
        return result
    }

    private fun generateDefaultPlayActions(entityId: String): List<Action> {
        return listOf(
            PlayAction({ warState ->
                warState.me.handArea.findByEntityId(entityId)?.let { card ->
                    log.info { "打出$card" }
                    card.action.power()
                } ?: let {
                    log.warn { "查询手中卡牌失败" }
                }
            }, { warState ->
                val me = warState.me
                val rival = warState.rival
                me.handArea.removeByEntityId(entityId)?.let { card ->
                    rival.handArea.add(card).isFalse {
                        log.warn { "添加手中卡牌失败" }
                    }
                    me.resourcesUsed += card.cost
                } ?: let {
                    log.warn { "移除手中卡牌失败" }
                }
            })
        )
    }

    private fun generateDefaultAttackActions(entityId: String): List<Action> {
        val result = mutableListOf<Action>()
        for (rivalPlayCard in state.war.rival.playArea.cards) {
//            todo 嘲讽
            if (rivalPlayCard.isSurvival() && rivalPlayCard.canBeAttacked()) {
                result.add(
                    AttackAction({ warState ->
                        warState.me.playArea.findByEntityId(entityId)?.let { myCard ->
                            warState.rival.playArea.findByEntityId(rivalPlayCard.entityId)?.let { rivalCard ->
                                log.info { "${myCard}攻击${rivalCard}" }
                                myCard.action.attack(rivalCard)
                            } ?: let {
                                log.warn { "查找敌方战场卡牌失败" }
                            }
                        } ?: let {
                            log.warn { "查找我方战场卡牌失败" }
                        }
                    }, { warState ->
                        warState.me.playArea.findByEntityId(entityId)?.let { myCard ->
                            warState.rival.playArea.findByEntityId(rivalPlayCard.entityId)?.let { rivalCard ->
                                handleAttack(myCard, rivalCard)
                            }
                        } ?: let {
                            log.warn { "查找战场卡牌失败" }
                        }
                    })
                )
            }
        }
        state.war.rival.playArea.hero?.let { rivalHero ->
            rivalHero.canBeAttacked().isTrue {
                result.add(
                    AttackAction({ warState ->
                        warState.me.playArea.findByEntityId(entityId)?.let { myCard ->
                            log.info { "${myCard}攻击${warState.rival.playArea.hero}" }
                            myCard.action.attack(warState.rival.playArea.hero)
                        } ?: let {
                            log.warn { "查找战场卡牌失败" }
                        }
                    }, { warState ->
                        warState.rival.playArea.hero?.let { rivalHero ->
                            warState.me.playArea.findByEntityId(entityId)?.let { myCard ->
                                handleAttack(myCard, rivalHero)
                            } ?: let {
                                log.warn { "查找战场卡牌失败" }
                            }
                        }
                    })
                )
            }
        }
        return result
    }

    private fun handleAttack(myCard: Card, rivalCard: Card) {
        if (myCard.isImmuneWhileAttacking || myCard.isImmune) {
        } else if (myCard.isDivineShield) {
            if (rivalCard.atc > 0) {
                myCard.isDivineShield = false
            }
        } else if (rivalCard.isPoisonous) {
            myCard.damage = myCard.health + myCard.armor
        } else {
            myCard.damage += rivalCard.atc
        }

        if (rivalCard.isDivineShield) {
            if (myCard.atc > 0) {
                rivalCard.isDivineShield = false
            }
        } else if (myCard.isPoisonous) {
            rivalCard.damage = rivalCard.health + rivalCard.armor
        } else {
            rivalCard.damage += myCard.atc
        }

        myCard.attackCount++
        if (myCard.isWindFury) {
            if (myCard.attackCount >= 2) {
                myCard.isExhausted = true
            }
        } else if (myCard.isMegaWindfury) {
            if (myCard.attackCount >= 4) {
                myCard.isExhausted = true
            }
        } else {
            myCard.isExhausted = true
        }
    }

    /**
     * 在当前节点状态下构建下一个节点
     */
    fun buildNextNode(action: Action): MonteCarloTreeNode {
        val newWarState = state.war.clone()
        action.simulate.accept(newWarState)
        val nextNode = MonteCarloTreeNode(newWarState, action, this)
        return nextNode
    }

    /**
     * 扩展动作至树中
     * @return 扩展后的新节点，为null表示扩展失败
     */
    fun expand(action: Action): MonteCarloTreeNode? {
        val index = actions.indexOf(action)
        if (index >= 0 && !isExpanded(index)) {
            val nextNode = buildNextNode(action)
            this.actionsExpandedFlag[index] = true
            this.children.add(nextNode)
            return nextNode
        }
        return null
    }

    /**
     * 是否已扩展
     */
    fun isExpanded(index: Int): Boolean {
        if (index >= 0 && index < actions.size) {
            return actionsExpandedFlag[index]
        }
        return true
    }

    /**
     * 是否已完全扩展
     */
    fun isFullExpanded(): Boolean {
        return actions.size == children.size
    }

    /**
     * 获取未扩展的动作
     */
    fun getUnExpanded(): MutableList<Action> {
        val result = mutableListOf<Action>()
        for ((index, action) in actions.withIndex()) {
            if (!isExpanded(index)) {
                result.add(action)
            }
        }
        return result
    }

    /**
     * 是否为叶子节点，即没有任何动作
     */
    fun isLeaf(): Boolean {
        return actions.isEmpty()
    }

    /**
     * 是否为结束节点
     */
    fun isEnd(): Boolean {
        return isLeaf() || WarUtil.isEnd(state.war)
    }

}