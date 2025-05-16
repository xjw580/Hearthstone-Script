package club.xiaojiawei.mcts

import club.xiaojiawei.bean.Action
import club.xiaojiawei.bean.MCTSArg
import club.xiaojiawei.bean.TurnOverAction
import club.xiaojiawei.bean.War
import club.xiaojiawei.enums.CardTypeEnum
import java.util.*
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.sqrt

/**
 * 蒙特卡洛树节点
 * @author 肖嘉威
 * @date 2025/1/10 16:27
 */
class MonteCarloTreeNode(
    war: War,
    action: Action,
    val arg: MCTSArg,
    var parent: MonteCarloTreeNode? = null,
) {

    /**
     * 应用的动作：父节点应用了此动作变成当前节点
     */
    val applyAction: Action = action

    /**
     * 所有子节点
     */
    val children: MutableList<MonteCarloTreeNode> by lazy { mutableListOf() }

    /**
     * 当前状态
     */
    val state: State = State(war, arg)

    /**
     * 当前所有可执行的动作
     */
    val actions: List<Action> = generateActions(war)

    /**
     * 存储action是否添加至子节点
     */
    private var actionsExpandedFlag: BitSet = BitSet(actions.size)

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
            for (card in handArea.cards) {
                if (!card.isUncertain && me.usableResource >= card.cost && (!playArea.isFull || card.cardType === CardTypeEnum.HERO || card.cardType === CardTypeEnum.SPELL || card.cardType === CardTypeEnum.WEAPON)) {
                    result.addAll(card.action.generatePlayActions(war, me))
                }
            }
            for (card in playArea.cards) {
                if (card.canAttack()) {
                    result.addAll(card.action.generateAttackActions(war, me))
                } else if (card.canPower()) {
                    result.addAll(card.action.generatePowerActions(war, me))
                }
            }
            playArea.hero?.let { myHero ->
                if (myHero.canAttack()) {
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
        arg: MCTSArg = this.arg,
        cloneWar: Boolean = true
    ): MonteCarloTreeNode {
        val newWar = if (cloneWar) state.war.clone() else state.war
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

    class State(val war: War, arg: MCTSArg) {

        val score: Double by lazy { calcScore(war, arg) }

        var winCount: Int = 0

        var visitCount: Int = 0

        var lastWin: Boolean = false

        val isEnd = war.isEnd()

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

        private fun calcScore(war: War, arg: MCTSArg): Double {
            val currentScore = arg.scoreCalculator.apply(war)
            val surplusTurn = max(arg.turnCount - 1, 0)
//        判断是否需要进行反演
            if (surplusTurn > 0) {
                val inverseArg = MCTSArg(
                    arg.endMillisTime,
                    surplusTurn,
                    arg.turnFactor * arg.turnFactor,
                    (arg.countPerTurn * arg.turnFactor).toInt(),
                    arg.scoreCalculator,
                    arg.enableMultiThread
                )
                val inverseWar = war.clone()
                inverseWar.me.apply {
                    playArea.hero?.atc = 0
//                triggerTurnEnd内部可能修改cards，使用副本遍历
                    val cardCopy = playArea.cards.toList()
                    for (card in cardCopy) {
                        if (card.isAlive()) {
                            card.action.triggerTurnEnd(war)
                        }
                    }
                }
                inverseWar.exchangePlayer()
                inverseWar.currentPlayer = inverseWar.me
                inverseWar.me.apply {
                    //            重置战场疲劳
                    for (card in playArea.cards) {
                        card.resetExhausted()
                        card.numTurnsInPlay++
                    }
                    for (card in handArea.cards) {
                        card.numTurnsInHand++
                    }
                    playArea.hero?.resetExhausted()
                    playArea.power?.resetExhausted()
                    playArea.weapon?.resetExhausted()
//                triggerTurnStart内部可能修改cards，使用副本遍历
                    val cardCopy = playArea.cards.toList()
                    for (card in cardCopy) {
                        if (card.isAlive()) {
                            card.action.triggerTurnStart(war)
                        }
                    }
                }

                val bestActions =
                    MonteCarloTreeSearch(maxDepth = MCTS_DEFAULT_DEPTH + 5).searchBestNode(inverseWar, inverseArg)
                return if (bestActions.isEmpty()) {
                    currentScore
                } else {
                    currentScore - bestActions.last().state.score * arg.turnFactor
                }
            } else {
                return currentScore
            }
        }

    }

}

