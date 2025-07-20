package club.xiaojiawei.hsscript.bean

import club.xiaojiawei.bean.Player
import club.xiaojiawei.hsscriptbase.config.log
import club.xiaojiawei.hsscript.enums.BlockTypeEnum
import java.util.*
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * @author 肖嘉威
 * @date 2024/12/10 19:44
 */
class PlayerBehavior(val player: Player) {

    val behaviors: MutableList<Behavior> = LinkedList()

    var robotProbability: Double = 0.5

    private fun setProbability(newProbability: Double): Double {
        val value = min(max(newProbability, 0.0), 1.0)
        robotProbability = value
        return robotProbability
    }

    fun calcInterval(): Triple<MutableList<Long>, MutableList<Long>, MutableList<Long>> {
        val atcInterval = mutableListOf<Long>()
        val playInterval = mutableListOf<Long>()
        val avgInterval = mutableListOf<Long>()

        var lastBehaviorMillis: Long = -1
        var lastAtcMillis: Long = -1
        var lastPlayMillis: Long = -1
        for (behavior in behaviors) {
            if (behavior.blockType == BlockTypeEnum.ATTACK) {
                if (lastAtcMillis != -1L) {
                    atcInterval.add(behavior.millis - lastAtcMillis)
                }
                lastAtcMillis = behavior.millis
            } else if (behavior.blockType == BlockTypeEnum.PLAY) {
                if (lastPlayMillis != -1L) {
                    playInterval.add(behavior.millis - lastPlayMillis)
                }
                lastPlayMillis = behavior.millis
            }
            if (lastBehaviorMillis != -1L) {
                avgInterval.add(behavior.millis - lastBehaviorMillis)
            }
            lastBehaviorMillis = behavior.millis
        }
        return Triple(atcInterval, playInterval, avgInterval)
    }

    fun robotGameIdCheck(): Boolean {
        val gameId = player.gameId
        return gameId.contains("之") || gameId.contains("的")
    }

    fun calcVariance(clickIntervals: List<Long>): Double {
        if (clickIntervals.isEmpty()) return 0.0

        val mean = clickIntervals.average()
        val variance = clickIntervals.map { (it - mean).pow(2) }.average()
        val stdDev = sqrt(variance)
        return stdDev
    }

    fun calcProbabilityByVariance(variance: Double, threshold: Double): Double {
        return (threshold - variance) / threshold
    }

    private var isCalcGameId = false

    fun renewCalcRobotProbability(): Double {
        var newProbability = robotProbability

        if (!isCalcGameId && robotGameIdCheck()) {
            isCalcGameId = true
            newProbability += 0.5
        }
        val (atcInterval, playInterval, avgInterval) = calcInterval()
        log.debug { "atcInterval:$atcInterval" }
        log.debug { "playInterval:$playInterval" }
//        log.info { "avgInterval:$avgInterval" }
        var atcProbability = 0.0
        var playProbability = 0.0
        var avgProbability = 0.0
        val countThreshold = 5.0
        if (atcInterval.size > 1) {
            val variance = calcVariance(atcInterval)
            log.debug { "atcInterval variance:$variance" }
            atcProbability = calcProbabilityByVariance(variance, 1000.0) * min((atcInterval.size) / countThreshold, 1.0)
            log.debug { "atcProbability:$atcProbability" }
        }
        if (playInterval.size > 1) {
            val variance = calcVariance(playInterval)
            log.debug { "playInterval variance:$variance" }
            playProbability =
                calcProbabilityByVariance(variance, 1100.0) * min((playInterval.size) / countThreshold, 1.0)
            log.debug { "playProbability:$playProbability" }
        }
//        if (avgInterval.size > 5) {
//            val variance = calcVariance(avgInterval)
//            log.debug { "avgInterval variance:$variance" }
//            avgProbability = calcProbabilityByVariance(variance, 1000.0) * min((avgInterval.size) / countThreshold, 1.0)
//        }
//        newProbability += atcProbability * (3 / 7.0) + playProbability * (3 / 7.0) + avgProbability * (1 / 7.0)
        newProbability += max(min(0.2 * (atcProbability * (4 / 7.0) + playProbability * (3 / 7.0)), 0.2), -0.2)
        return setProbability(newProbability)
    }

}
