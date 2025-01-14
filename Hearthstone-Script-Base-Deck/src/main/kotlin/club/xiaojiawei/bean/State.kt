package club.xiaojiawei.bean

import club.xiaojiawei.status.War
import club.xiaojiawei.util.WarUtil
import kotlin.math.ln
import kotlin.math.sqrt

/**
 * @author 肖嘉威
 * @date 2025/1/14 9:13
 */
class State(val war: War) {

    var winCount: Int = 0

    var score: Double = WarUtil.calcScore(war)

    var visitCount: Int = 0

    fun increaseWin() {
        this.winCount++
    }

    fun increaseVisit() {
        visitCount++
    }

    fun calcUCB(totalCount: Int, c: Double = 2.0): Double {
        return if (visitCount == 0)
            Int.MAX_VALUE.toDouble()
        else
            winCount / visitCount + sqrt(c * ln(totalCount.toDouble()) / visitCount.toDouble())
    }

}