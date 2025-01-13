package club.xiaojiawei.test.mcts

import club.xiaojiawei.*
import club.xiaojiawei.bean.Card
import club.xiaojiawei.config.log
import club.xiaojiawei.enums.CardTypeEnum
import club.xiaojiawei.util.isFalse
import club.xiaojiawei.util.isTrue
import kotlin.math.ln
import kotlin.math.sqrt

/**
 * @author 肖嘉威
 * @date 2025/1/10 16:27
 */
class MonteCarloTreeNode(warState: WarState, action: Action, var parent: MonteCarloTreeNode? = null) {

    val applyAction: Action = action

    val children = mutableListOf<MonteCarloTreeNode>()

    val actions: List<Action>

    var actionsExpanded: Int = 0

    val state: State

    init {
        this.state = State(warState)
        actions = generateActions(warState)
    }

    private fun generateActions(warState: WarState): MutableList<Action> {
        val result = mutableListOf<Action>()
        if (this.applyAction !== TurnOverAction) {
            val me = warState.me
            val rival = warState.rival
            val handArea = me.handArea
            val playArea = me.playArea
            result.add(TurnOverAction)
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
                        log.warn { "添加卡牌失败" }
                    }
                    me.resourcesUsed += card.cost
                } ?: let {
                    log.warn { "移除卡牌失败" }
                }
            })
        )
    }

    private fun generateDefaultAttackActions(entityId: String): List<Action> {
        val result = mutableListOf<Action>()
        for (rivalPlayCard in state.warState.rival.playArea.cards) {
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
        state.warState.rival.playArea.hero?.let { rivalHero ->
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

    fun expand(index: Int): MonteCarloTreeNode? {
        if (index >= 0 && index < actions.size) {
            val action = actions[index]
            val nextNode = buildNextNode(action)
            this.actionsExpanded = this.actionsExpanded xor (1 shl index)
            this.children.add(nextNode)
            return nextNode
        }
        return null
    }

    fun expand(action: Action): MonteCarloTreeNode? {
        val index = actions.indexOf(action)
        if (index >= 0) {
            val nextNode = buildNextNode(action)
            this.actionsExpanded = this.actionsExpanded xor (1 shl index)
            this.children.add(nextNode)
            return nextNode
        }
        return null
    }

    fun buildNextNode(action: Action): MonteCarloTreeNode {
        val newWarState = state.warState.clone()
        action.simulate.accept(newWarState)
        val nextNode = MonteCarloTreeNode(newWarState, action, this)
        return nextNode
    }

    fun isExpanded(index: Int): Boolean {
        if (index >= 0 && index < actions.size) {
            return ((actionsExpanded shr index) and 1) == 1
        }
        return true
    }

    fun isFullExpand(): Boolean {
        return actionsExpanded >= (1 shl (actions.size)) - 1
    }

    fun getUnExpanded(): MutableList<Action> {
        val result = mutableListOf<Action>()
        for ((index, action) in actions.withIndex()) {
            if (!isExpanded(index)) {
                result.add(action)
            }
        }
        return result
    }


    fun isLeaf(): Boolean {
        return actions.isEmpty()
    }

    fun isEnd(): Boolean {
        return isLeaf() || state.warState.isEnd()
    }

}

class State(val warState: WarState) {

    var score: Double = 0.0

    var visitCount: Int = 0

    fun addScore(score: Double) {
        this.score += score
    }

    fun increaseVisit() {
        visitCount++
    }

    fun calcUCB(totalCount: Int, c: Double = 2.0): Double {
        return if (visitCount == 0)
            Int.MAX_VALUE.toDouble()
        else
            score / visitCount + sqrt(c * ln(totalCount.toDouble()) / visitCount.toDouble())
    }
}