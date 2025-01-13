package club.xiaojiawei.test.mcts

import club.xiaojiawei.Action
import club.xiaojiawei.InitAction
import club.xiaojiawei.TurnOverAction
import club.xiaojiawei.WarState
import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.Player
import club.xiaojiawei.enums.CardTypeEnum
import kotlin.math.ln
import kotlin.math.sqrt

/**
 * @author 肖嘉威
 * @date 2025/1/10 16:27
 */
class MonteCarloTreeNode {

    var parent: MonteCarloTreeNode?

    val applyAction: Action

    val children = mutableListOf<MonteCarloTreeNode>()

    val actions: List<Action>

    var actionsExpanded: Int = 0

    val state: State

    constructor(warState: WarState, parent: MonteCarloTreeNode? = null, action: Action) {
        this.parent = parent
        this.applyAction = action
        this.state = State(warState)
        actions = generateActions(warState).toList()
    }

    constructor(warState: WarState) : this(warState, null, InitAction)

    private fun generateActions(warState: WarState): MutableList<Action> {
        val me = warState.me
        val rival = warState.rival
        val handArea = me.handArea
        val playArea = me.playArea
        val result = mutableListOf<Action>()
        if (this.applyAction !== TurnOverAction) {
            result.add(TurnOverAction)
            handArea.cards.forEach { card ->
                if (me.usableResource >= card.cost && (!playArea.isFull || card.cardType === CardTypeEnum.HERO || card.cardType === CardTypeEnum.SPELL || card.cardType === CardTypeEnum.WEAPON)) {
                    result.addAll(
                        card.action.createPlayActions(me, rival) ?: this.generateDefaultPlayActions(
                            card,
                            me,
                            rival
                        )
                    )
                }
            }
            playArea.cards.forEach { card ->
                if (card.canAttack()) {
                    result.addAll(
                        card.action.createAttackActions(me, rival) ?: this.generateDefaultAttackActions(
                            card,
                            me,
                            rival
                        )
                    )
                }
            }
            playArea.hero?.let { myHero ->
                if (myHero.canAttack()) {
                    result.addAll(
                        myHero.action.createAttackActions(me, rival) ?: this.generateDefaultAttackActions(
                            myHero,
                            me,
                            rival
                        )
                    )
                }
            }
            playArea.power?.let { myPower ->
                if (me.usableResource >= myPower.cost) {
                    myPower.action.createPlayActions(me, rival)
                }
            }
        }
        return result
    }

    private fun generateDefaultPlayActions(card: Card, me: Player, rival: Player): List<Action> {
        return listOf(
            Action({
                card.action.power()
            }, {
                me.handArea.removeByEntityId(card.entityId)?.let {
                    rival.handArea.add(card)
                    me.resourcesUsed += card.cost
                }
            })
        )
    }

    private fun generateDefaultAttackActions(card: Card, me: Player, rival: Player): List<Action> {
        val result = mutableListOf<Action>()
        for (rivalPlayCard in rival.playArea.cards) {
//            todo 嘲讽
            if (rivalPlayCard.isSurvival() && rivalPlayCard.canBeAttacked()) {
                result.add(
                    Action({
                        card.action.attack(rivalPlayCard)
                    }, {
                        handleAttack(card, rivalPlayCard)
                    })
                )
            }
        }
        result.add(
            Action({
                card.action.attack(rival.playArea.hero)
            }, {
                rival.playArea.hero?.let { rivalHero ->
                    handleAttack(card, rivalHero)
                }
            })
        )
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
            myCard.health -= rivalCard.atc
        }

        if (rivalCard.isDivineShield) {
            rivalCard.isDivineShield = false
        } else if (myCard.isPoisonous) {
            rivalCard.damage = rivalCard.health + rivalCard.armor
        } else {
            rivalCard.health -= myCard.atc
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
        val nextNode = MonteCarloTreeNode(newWarState, this, action)
        return nextNode
    }

    fun isExpanded(index: Int): Boolean {
        if (index >= 0 && index < actions.size) {
            return actionsExpanded shr index == 1
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