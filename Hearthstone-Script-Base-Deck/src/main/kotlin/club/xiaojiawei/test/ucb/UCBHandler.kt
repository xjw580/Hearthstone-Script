package club.xiaojiawei.test.ucb

import kotlin.math.ln
import kotlin.math.sqrt

/**
 * @author 肖嘉威
 * @date 2025/1/9 15:56
 */
class UCBHandler(

) {

    private val rewards = mutableListOf<Double>()

    private var turn: Int = 0

    private var selectedCount: Int = 0

    var ucb: Double = 0.0

    fun addReward(reward: Double) {
        rewards.add(reward)
        selectedCount++
    }

    fun increaseTurn() {
        turn++
    }

    fun updateUCB(): Double {
        if (selectedCount == 0) {
            ucb = 0.0
            return ucb
        }
        val avgReward = rewards.sum() / selectedCount
        ucb = avgReward + sqrt(2.0) * sqrt(2.0 * ln(turn.toDouble()) / selectedCount)
        return ucb
    }

    override fun toString(): String {
        return "UCBHandler(selectedCount=$selectedCount, turn=$turn, ucb=$ucb)"
    }


}