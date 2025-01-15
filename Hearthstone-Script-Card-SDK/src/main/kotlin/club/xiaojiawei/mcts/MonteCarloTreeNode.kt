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
        if (surplusTurn > 0) {
            arg = MCTSArg(
                0,
                surplusTurn,
                arg.turnFactor * arg.turnFactor,
                (arg.countPerTurn * arg.turnFactor).toInt(),
                arg.scoreCalculator
            )
            val newWar = war.clone()
            newWar.exchangePlayer()
//            todo 移除对手手牌，重置战场疲劳
            val monteCarloTreeSearch = MonteCarloTreeSearch(maxDepth = 10)
            val bestActions = monteCarloTreeSearch.getBestActions(newWar, arg)
            if (bestActions.isEmpty()) {
                return currentScore
            } else {
                return currentScore - bestActions.last().state.score * arg.turnFactor
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
                    result.addAll(card.action.generatePlayActions(war))
                }
            }
            playArea.cards.forEach { card ->
                if (card.canAttack()) {
                    result.addAll(card.action.generateAttackActions(war))
                }
            }
            playArea.hero?.let { myHero ->
                if (myHero.canAttack()) {
                    result.addAll(myHero.action.generateAttackActions(war))
                }
            }
            playArea.power?.let { myPower ->
                if (me.usableResource >= myPower.cost && !myPower.isExhausted) {
                    result.addAll(myPower.action.generatePlayActions(war))
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
        val newWarState = state.war.clone()
        action.simulate.accept(newWarState)
        val nextNode = MonteCarloTreeNode(newWarState, action, arg, this)
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
        return isLeaf() || MCTSUtil.isEnd(state.war)
    }

    class State(val war: War, val score: Double) {

        var winCount: Int = 0

        var visitCount: Int = 0

        var lastWin: Boolean = false

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

