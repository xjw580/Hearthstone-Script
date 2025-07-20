package club.xiaojiawei.hsscriptbasestrategy.test.ucb

import kotlin.random.Random


/**
 * @author 肖嘉威
 * @date 2025/1/9 15:46
 */

class Robot(val name: String, val lowBound: Double, val highBound: Double) {

    private val random = Random(System.currentTimeMillis())

    fun getValue(): Double {
        return random.nextDouble(lowBound, highBound)
    }

    override fun toString(): String {
        return "Robot(name='$name', lowBound=$lowBound, highBound=$highBound)"
    }


}