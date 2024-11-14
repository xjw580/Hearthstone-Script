package club.xiaojiawei.util

import java.util.*

/**
 * 随机数生成工具
 * @author 肖嘉威
 * @date 2022/11/24 19:41
 */
object RandomUtil {

    private val RANDOM = Random(System.currentTimeMillis())

    fun getRandom(min: Int, max: Int): Int {
        if (min == max) return min
        return if (min > max) {
            getRandom(max, min)
        } else (RANDOM.nextDouble() * (max - min + 1) + min).toInt()
    }

    fun getRandom(min: Double, max: Double): Double {
        if (min == max) return min
        return if (min > max) {
            getRandom(max, min)
        } else RANDOM.nextDouble(min, max)
    }

    fun getHugeRandom(): Int {
        return getRandom(3000, 5000)
    }

    fun getLongRandom(): Int {
        return getRandom(2000, 3000)
    }

    fun getMediumRandom(): Int {
        return getRandom(1000, 1600)
    }

    fun getShortMediumRandom(): Int {
        return getRandom(500, 800)
    }

    fun getShortRandom(): Int {
        return getRandom(250, 450)
    }

    fun getTinyRandom(): Int {
        return getRandom(100, 200)
    }

    fun getHumanRandom(): Int {
        return getRandom(200, 2000)
    }

}
