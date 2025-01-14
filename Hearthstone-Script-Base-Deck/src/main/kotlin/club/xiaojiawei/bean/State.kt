package club.xiaojiawei.bean

import club.xiaojiawei.status.War
import kotlin.math.ln
import kotlin.math.sqrt

/**
 * @author 肖嘉威
 * @date 2025/1/14 9:13
 */
class State(val war: War) {

    var score: Double = 0.0

    var visitCount: Int = 0

    fun addScore(score: Double) {
        this.score += score
    }

    fun increaseVisit() {
        visitCount++
    }

    fun calcUCB(totalCount: Int, c: Double = 1.0): Double {
        return if (visitCount == 0)
            Int.MAX_VALUE.toDouble()
        else
            score / visitCount + sqrt(c * ln(totalCount.toDouble()) / visitCount.toDouble())
    }

}