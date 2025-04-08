package club.xiaojiawei.hsscript.bean

import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * @author 肖嘉威
 * @date 2024/10/11 16:30
 */

class WorkTime {

    constructor()

    constructor(startTime: String?, endTime: String?, enabled: Boolean) {
        this.startTime = startTime
        this.endTime = endTime
        this.enabled = enabled
    }

    constructor(startTime: LocalTime?, endTime: LocalTime?, enabled: Boolean) {
        this.startTime = pattern.format(startTime)
        this.endTime = pattern.format(endTime)
        this.enabled = enabled
    }

    var startTime: String? = null
    var endTime: String? = null
    var enabled: Boolean = false

    fun parseStartTime(): LocalTime? {
        return parseTime(startTime)
    }

    fun parseEndTime(): LocalTime? {
        return parseTime(endTime)
    }

    private fun parseTime(timeStr: String?):LocalTime? {
        if (timeStr.isNullOrBlank()) return null
        return LocalTime.from(pattern.parse(timeStr))
    }


    companion object {
        val pattern = DateTimeFormatter.ofPattern("HH:mm")
    }
}