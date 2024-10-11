package club.xiaojiawei.hsscript.bean

import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * @author 肖嘉威
 * @date 2024/10/11 16:30
 */

private val pattern = DateTimeFormatter.ofPattern("HH:mm")

class WorkTime {

    constructor()

    constructor(startTime: String?, endTime: String?, enabled: Boolean) {
        this.startTime = startTime
        this.endTime = endTime
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
        if (timeStr == null || timeStr.isBlank()) return null
        return LocalTime.from(pattern.parse(timeStr))
    }
}