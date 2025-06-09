package club.xiaojiawei.hsscript.listener

import club.xiaojiawei.bean.LRunnable
import club.xiaojiawei.config.EXTRA_THREAD_POOL
import club.xiaojiawei.config.log
import club.xiaojiawei.hsscript.bean.WorkTimeRule
import club.xiaojiawei.hsscript.bean.single.WarEx
import club.xiaojiawei.hsscript.enums.WindowEnum
import club.xiaojiawei.hsscript.status.PauseStatus
import club.xiaojiawei.hsscript.status.WorkTimeStatus
import club.xiaojiawei.hsscript.utils.SystemUtil
import club.xiaojiawei.hsscript.utils.WindowUtil
import club.xiaojiawei.hsscript.utils.go
import club.xiaojiawei.hsscript.utils.runUI
import club.xiaojiawei.util.isFalse
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.value.ChangeListener
import javafx.stage.Stage
import java.time.LocalDate
import java.time.LocalTime
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

/**
 * 工作状态
 *
 * @author 肖嘉威
 * @date 2023/9/10 22:04
 */
object WorkTimeListener {
    private var checkWorkTask: ScheduledFuture<*>? = null

    val launch: Unit by lazy {
        checkWorkTask =
            EXTRA_THREAD_POOL.scheduleWithFixedDelay(
                LRunnable {
                    checkWork()
                    tryWork()
                },
                0,
                30,
                TimeUnit.SECONDS,
            )
        WarEx.inWarProperty.addListener { _, _, newValue ->
            if (!newValue && PauseStatus.isStart) {
                checkWork()
                if (cannotWork()) {
                    cannotWorkLog()
                    workingProperty.set(false)
                    execOperate(prevClosestWorkTimeRule)
                }
            }
        }
        log.info { "工作时段监听已启动" }
    }

    private fun execOperate(workTimeRule: WorkTimeRule?) {
        val operates = workTimeRule?.getOperate() ?: return

        val alert: AtomicReference<Stage?> = AtomicReference<Stage?>()
        val countdownTime = 10
        val future =
            go {
                for (i in 0 until countdownTime) {
                    if (PauseStatus.isStart) {
                        Thread.sleep(1000)
                    } else {
                        break
                    }
                }
                runUI {
                    alert.get()?.hide()
                }
                for (operate in operates) {
                    if (PauseStatus.isStart) {
                        operate.exec().isFalse {
                            log.error {
                                operate.value + "执行失败"
                            }
                        }
                    } else {
                        return@go
                    }
                }
            }
        val operationName = operates.map { it.value }
        val text = "${countdownTime}秒后执行$operationName"
        println("text:"+text)
        log.info { text }
        runUI {
            alert.set(
                WindowUtil
                    .createAlert(
                        text,
                        null,
                        {
                            future.cancel(true)
                            runUI {
                                alert.get()?.hide()
                            }
                        },
                        null,
                        WindowUtil.getStage(WindowEnum.MAIN),
                        "阻止",
                    ).apply {
                        show()
                    },
            )
        }
    }

    var isDuringWorkDate = false

    /**
     * 是否处于工作中
     */
    private val workingProperty = SimpleBooleanProperty(false)

    var working: Boolean
        get() {
            return workingProperty.get()
        }
        set(value) {
            workingProperty.set(value)
        }

    fun addChangeListener(listener: ChangeListener<Boolean>) {
        workingProperty.addListener(listener)
    }

    fun removeChangeListener(listener: ChangeListener<Boolean>) {
        workingProperty.removeListener(listener)
    }

    fun canWork(): Boolean = isDuringWorkDate

    fun cannotWork(): Boolean = !isDuringWorkDate

    fun tryWork() {
        if (canWork() && PauseStatus.isStart) {
            workingProperty.set(true)
        }
    }

    @Synchronized
    fun checkWork() {
        var canWork = false
        val readOnlyWorkTimeSetting = WorkTimeStatus.readOnlyWorkTimeSetting()
        val dayIndex = LocalDate.now().dayOfWeek.value - 1
        if (dayIndex >= readOnlyWorkTimeSetting.size) return
        val id = readOnlyWorkTimeSetting[dayIndex]
        WorkTimeStatus.readOnlyWorkTimeRuleSet().toList().find { it.id == id }?.let {
            val timeRules = it.getTimeRules().toList()
            val nowTime = LocalTime.now()
            val nowSecondOfDay = nowTime.toSecondOfDay()

            var minDiffSec: Int = Int.MAX_VALUE
            var closestWorkTimeRule: WorkTimeRule? = null
            for (rule in timeRules) {
                if (!rule.isEnable()) continue
                val workTime = rule.getWorkTime()
                val startTime = workTime.parseStartTime()?.withSecond(0) ?: continue
                val endTime = workTime.parseEndTime()?.withSecond(59) ?: continue
                if (nowTime in startTime..endTime) {
                    canWork = true
                    closestWorkTimeRule = rule
                    break
                } else {
                    val diffSec = nowSecondOfDay - endTime.toSecondOfDay()
                    if (diffSec in 1 until minDiffSec) {
                        minDiffSec = diffSec
                        closestWorkTimeRule = rule
                    }
                }
            }
            prevClosestWorkTimeRule = closestWorkTimeRule
        }
        isDuringWorkDate = canWork
    }

    private var prevClosestWorkTimeRule: WorkTimeRule? = null

    fun cannotWorkLog() {
        val context = "现在是下班时间 🌜"
        SystemUtil.notice(context)
        log.info { context }
    }

    /**
     * 获取下一次可工作的时间
     */
    fun getSecondsUntilNextWorkPeriod(): Long {
        if (working) return 0L

        val readOnlyWorkTimeSetting = WorkTimeStatus.readOnlyWorkTimeSetting()
        val dayIndex = LocalDate.now().dayOfWeek.value - 1
        if (dayIndex >= readOnlyWorkTimeSetting.size) return -1L

        var sec = -1L
        for (i in dayIndex until readOnlyWorkTimeSetting.size) {
            val id = readOnlyWorkTimeSetting[i]
            sec = getSecondsUntilNextWorkPeriod(id, (i - dayIndex) * 3600 * 24L)
            if (sec > 0) break
        }
        if (sec == -1L) {
            for (i in 0 until dayIndex) {
                val id = readOnlyWorkTimeSetting[i]
                sec = getSecondsUntilNextWorkPeriod(id, (i + readOnlyWorkTimeSetting.size - dayIndex) * 3600 * 24L)
                if (sec > 0) break
            }
        }

        return sec
    }

    private fun getSecondsUntilNextWorkPeriod(
        workTimeRuleSetId: String,
        offsetSec: Long,
    ): Long {
        WorkTimeStatus.readOnlyWorkTimeRuleSet().toList().find { it.id == workTimeRuleSetId }?.let {
            val timeRules = it.getTimeRules().toList()
            val nowTime = LocalTime.now()
            val nowSecondOfDay = nowTime.toSecondOfDay() - offsetSec

            var minDiffSec: Long = Long.MAX_VALUE
            for (rule in timeRules) {
                if (!rule.isEnable()) continue
                val workTime = rule.getWorkTime()
                val startTime = workTime.parseStartTime() ?: continue
                val diffSec: Long = startTime.toSecondOfDay() - nowSecondOfDay
                if (diffSec in 1 until minDiffSec) {
                    minDiffSec = diffSec
                }
            }
            return if (minDiffSec == Long.MAX_VALUE) -1L else minDiffSec
        }
        return -1L
    }
}
