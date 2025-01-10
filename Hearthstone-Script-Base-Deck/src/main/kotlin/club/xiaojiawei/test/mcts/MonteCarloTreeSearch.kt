package club.xiaojiawei.test.mcts

import club.xiaojiawei.bean.Player

/**
 * @author 肖嘉威
 * @date 2025/1/10 10:04
 */
class MonteCarloTreeSearch {

    // 根据当前游戏状态返回最佳行动。
    fun getBestAction(
        my: Player, rival: Player, thinkingTime: Int = 10 * 1000
    ) {
        val endTime = System.currentTimeMillis() + thinkingTime
        while (System.currentTimeMillis() < endTime) {

        }
    }

    private fun calcScore(my: Player, rival: Player) {
//        todo
    }

    fun select() {

    }

    fun expand() {
    }

    fun simulate() {
    }

    fun backPropagation() {
    }

}