package club.xiaojiawei.hsscript.bean

import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * @author 肖嘉威
 * @date 2024/10/11 16:30
 */

class WorkTime : Cloneable {

    constructor()

    constructor(startTime: String?, endTime: String?) {
        this.startTime = startTime
        this.endTime = endTime
    }

    constructor(startTime: LocalTime?, endTime: LocalTime?) {
        this.startTime = pattern.format(startTime)
        this.endTime = pattern.format(endTime)
    }

    var startTime: String? = null
    var endTime: String? = null

    fun parseStartTime(): LocalTime? {
        return parseTime(startTime)
    }

    fun parseEndTime(): LocalTime? {
        return parseTime(endTime)
    }

    private fun parseTime(timeStr: String?): LocalTime? {
        if (timeStr.isNullOrBlank()) return null
        return runCatching { LocalTime.from(pattern.parse(timeStr)) }.getOrNull()
    }

    companion object {
        val pattern = DateTimeFormatter.ofPattern("HH:mm")
    }

    public override fun clone(): WorkTime {
        val clone = WorkTime()
        clone.startTime = this.startTime
        clone.endTime = this.endTime
        return clone
    }
}