package club.xiaojiawei.hsscript.listener

import club.xiaojiawei.bean.LRunnable
import club.xiaojiawei.config.EXTRA_THREAD_POOL
import club.xiaojiawei.config.log
import club.xiaojiawei.hsscript.core.Core
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.status.PauseStatus
import club.xiaojiawei.hsscript.utils.ConfigExUtil
import club.xiaojiawei.hsscript.utils.ConfigUtil
import club.xiaojiawei.hsscript.utils.GameUtil
import club.xiaojiawei.hsscript.utils.SystemUtil
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import java.time.LocalDate
import java.time.LocalTime
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * 工作状态
 *
 * @author 肖嘉威
 * @date 2023/9/10 22:04
 */
object WorkListener {

    private var checkVersionTask: ScheduledFuture<*>? = null

    fun launch() {
        checkVersionTask?.let { return }
        checkVersionTask = EXTRA_THREAD_POOL.scheduleAtFixedRate(LRunnable {
            checkWork()
        }, 0, 1000 * 60, TimeUnit.MILLISECONDS)
        log.info { "工作时段监听已启动" }
    }

    /**
     * 是否处于工作中
     */
    val workingProperty = SimpleBooleanProperty(false)

    var working: Boolean
        get() = workingProperty.get()
        set(value) = workingProperty.set(value)

    private var enableUpdate = true

    @Synchronized
    fun stopWork() {
        workingProperty.set(false)
        cannotWorkLog()
        log.info { "停止工作，准备关闭游戏" }
        GameUtil.killGame()
    }

    fun cannotWorkLog() {
        val context = "现在是下班时间 🌜"
        SystemUtil.notice(context)
        log.info { context }
    }

    fun workLog() {
        log.info { "现在是上班时间 🌞" }
    }

    fun checkWork() {
        if (working) return
        synchronized(workingProperty) {
            if (working) return
            if (!PauseStatus.isPause && isDuringWorkDate()) {
                workLog()
                Core.start()
            } else if (enableUpdate && ConfigUtil.getBoolean(ConfigEnum.AUTO_UPDATE) && VersionListener.canUpdate) {
                enableUpdate = false
                val progress = SimpleDoubleProperty()
                VersionListener.downloadLatestRelease(false, progress) { path ->
                    path?.let {
                        VersionListener.execUpdate(path)
                    } ?: let {
                        enableUpdate = true
                    }
                }
            }
        }
    }

    private val DEFAULT_TIME = LocalTime.parse("00:00")

    /**
     * 验证是否在工作时间内
     *
     * @return
     */
    fun isDuringWorkDate(): Boolean {
        //        天校验
        val workDay = ConfigExUtil.getWorkDay()
        val nowDay = LocalDate.now().getDayOfWeek().value
        if (workDay.isNotEmpty()) {
            if (!(workDay[0].enabled || workDay[nowDay].enabled)) {
                return false
            }
        } else {
            return false
        }

        //        段校验
        val workTime = ConfigExUtil.getWorkTime().toList()
        val nowTime: LocalTime = LocalTime.now()
        for (time in workTime) {
            if (time.enabled) {
                val startTime = time.parseStartTime() ?: DEFAULT_TIME
                val endTime = time.parseEndTime() ?: DEFAULT_TIME
                if (startTime == endTime) {
                    return true
                }
                if (startTime.isBefore(endTime)) {
                    // 同一天的情况：startTime < endTime
                    if (!nowTime.isBefore(startTime) && !nowTime.isAfter(endTime)){
                        return true
                    }
                } else if (!nowTime.isBefore(startTime) || !nowTime.isAfter(endTime)){
                    // 跨天的情况：startTime >= endTime
                    return true
                }
            }
        }
        return false
    }

}