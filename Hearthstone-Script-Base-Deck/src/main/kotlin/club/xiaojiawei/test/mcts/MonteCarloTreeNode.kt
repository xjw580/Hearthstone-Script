package club.xiaojiawei.test.mcts

import club.xiaojiawei.Action
import kotlin.math.ln
import kotlin.math.sqrt

/**
 * @author 肖嘉威
 * @date 2025/1/10 16:27
 */
class MonteCarloTreeNode(var parent: MonteCarloTreeSearch? = null, val action: Action) {

    val children = mutableListOf<MonteCarloTreeNode>()

    val state: State = State()

    companion object {

        fun calcUCB(node: MonteCarloTreeNode, c: Double = 2.0): Double {
            return calcUCB(node.state, c)
        }

        fun calcUCB(state: State, c: Double = 2.0): Double {
            return state.run {
                if (visitCount == 0)
                    Int.MAX_VALUE.toDouble()
                else
                    (score / visitCount + c * sqrt(ln(totalCount.toDouble()) / visitCount.toDouble()))
            }
        }
    }

}

class State {

    var score: Double = 0.0

    var visitCount: Int = 0

    var totalCount: Int = 0

    fun addScore(score: Double) {
        this.score += score
    }
}