package club.xiaojiawei.mcts

import club.xiaojiawei.bean.Action
import club.xiaojiawei.bean.MCTSArg
import club.xiaojiawei.bean.TurnOverAction
import club.xiaojiawei.enums.CardTypeEnum
import club.xiaojiawei.status.War
import club.xiaojiawei.util.MCTSUtil
import java.util.*
import kotlin.math.ln
import kotlin.math.sqrt

/**
 * @author 肖嘉威
 * @date 2025/1/10 16:27
 */
class MonteCarloTreeNode(
    war: War,
    action: Action,
    var arg: MCTSArg,
    var parent: MonteCarloTreeNode? = null
) {

    /**
     * 应用的动作：父节点应用了此动作变成当前节点
     */
    val applyAction: Action = action

    /**
     * 所有子节点
     */
    val children: MutableList<MonteCarloTreeNode> = mutableListOf()

    /**
     * 当前状态
     */
    val state: State = State(war, calcScore(war))

    /**
     * 当前所有可执行的动作
     */
    val actions: List<Action> = generateActions(war)

    /**
     * 存储action是否添加至子节点
     */
    private var actionsExpandedFlag: BitSet = BitSet(actions.size)

    private fun calcScore(war: War): Double {
        val currentScore = arg.scoreCalculator.apply(war)
        val surplusTurn = arg.turnCount - 1
//        判断是否需要进行反演
        if (surplusTurn > 0) {
            val inverseArg = MCTSArg(
                (arg.thinkingSecTime * arg.turnFactor).toInt(),
                surplusTurn,
                arg.turnFactor * arg.turnFactor,
                (arg.countPerTurn * arg.turnFactor).toInt(),
                arg.scoreCalculator,
                arg.enableMultiThread
            )
            val inverseWar = war.clone()
            inverseWar.me.apply {
                playArea.hero?.atc = 0
                playArea.cards.forEach { card ->
                    card.action.triggerTurnEnd(war)
                }
            }
            inverseWar.exchangePlayer()
            inverseWar.currentPlayer = inverseWar.me
            inverseWar.me.apply {
                //            重置战场疲劳
                playArea.cards.forEach { card ->
                    card.resetExhausted()
                    card.numTurnsInPlay++
                }
                handArea.cards.forEach { card ->
                    card.numTurnsInHand++
                }
                playArea.hero?.resetExhausted()
                playArea.power?.resetExhausted()
                playArea.weapon?.resetExhausted()
                playArea.cards.forEach { card ->
                    card.action.triggerTurnStart(war)
                }
            }

//            反演时尽量调大maxDepth值，可以减少资源消耗
            val bestActions = MonteCarloTreeSearch(maxDepth = 15).getBestActions(inverseWar, inverseArg)
            return if (bestActions.isEmpty()) {
                currentScore
            } else {
                currentScore - bestActions.last().state.score * arg.turnFactor
            }
        } else {
            return currentScore
        }
    }

    /**
     * 根据指定状态生成所有可能的动作
     */
    private fun generateActions(war: War): MutableList<Action> {
        val result = mutableListOf<Action>()
        if (applyAction !== TurnOverAction) {
            val me = war.me
            val handArea = me.handArea
            val playArea = me.playArea
            result.add(TurnOverAction)
            handArea.cards.forEach { card ->
                if (me.usableResource >= card.cost && (!playArea.isFull || card.cardType === CardTypeEnum.HERO || card.cardType === CardTypeEnum.SPELL || card.cardType === CardTypeEnum.WEAPON)) {
                    result.addAll(card.action.generatePlayActions(war, me))
                }
            }
            playArea.cards.forEach { card ->
                if (card.canAttack()) {
                    result.addAll(card.action.generateAttackActions(war, me))
                } else if (card.canPower()) {
                    result.addAll(card.action.generatePowerActions(war, me))
                }
            }
            playArea.hero?.let { myHero ->
                if (myHero.canAttack() || (myHero.canAttack(ignoreAtc = true) && playArea.weapon?.canAttack(
                        ignoreExhausted = true
                    ) == true)
                ) {
                    result.addAll(myHero.action.generateAttackActions(war, me))
                }
            }
            playArea.power?.let { myPower ->
                if (me.usableResource >= myPower.cost && myPower.canPower()) {
                    result.addAll(myPower.action.generatePowerActions(war, me))
                }
            }
        }
        return result
    }

    /**
     * 在当前节点状态下构建下一个节点
     */
    fun buildNextNode(
        action: Action,
        arg: MCTSArg = this.arg
    ): MonteCarloTreeNode {
        val newWar = state.war.clone()
//        新战局应用旧动作
        action.simulate.accept(newWar)
        val nextNode = MonteCarloTreeNode(newWar, action, arg, this)
        return nextNode
    }

    /**
     * 扩展动作至树中
     * @return 扩展后的新节点，为null表示扩展失败
     */
    fun expand(action: Action, arg: MCTSArg = this.arg): MonteCarloTreeNode? {
        val index = actions.indexOf(action)
        if (index >= 0 && !isExpanded(index)) {
            val nextNode = buildNextNode(action, arg)
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
        return state.isEnd || isLeaf()
    }

    class State(val war: War, val score: Double) {

        var winCount: Int = 0

        var visitCount: Int = 0

        var lastWin: Boolean = false

        val isEnd = MCTSUtil.isEnd(war)

        fun update(win: Boolean?) {
            visitCount++
            val isWin = win?.let {
                lastWin = it
                it
            } ?: lastWin
            if (isWin) {
                winCount++
            }
        }

        fun calcUCB(totalCount: Int, c: Double = 2.0): Double {
            return if (visitCount == 0)
                Int.MAX_VALUE.toDouble()
            else
                winCount / visitCount + sqrt(c * ln(totalCount.toDouble()) / visitCount.toDouble())
        }

    }

}

