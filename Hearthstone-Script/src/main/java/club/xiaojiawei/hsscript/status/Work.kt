package club.xiaojiawei.hsscript.status

import club.xiaojiawei.config.log
import club.xiaojiawei.controls.TimeSelector
import club.xiaojiawei.enums.WsResultTypeEnum
import club.xiaojiawei.hsscript.controller.javafx.MainController
import club.xiaojiawei.hsscript.listener.VersionListener
import club.xiaojiawei.hsscript.utils.ConfigUtil
import club.xiaojiawei.hsscript.utils.GameUtil
import club.xiaojiawei.hsscript.utils.SystemUtil
import javafx.application.Platform
import java.time.LocalDate
import java.time.LocalTime
import java.util.*
import java.util.function.Consumer

/**
 * 工作状态
 *
 * @author 肖嘉威
 * @date 2023/9/10 22:04
 */
object Work {

    @Scheduled(fixedDelay = 1000 * 60)
    fun workSchedule() {
        checkWork()
    }

    /**
     * 是否处于工作中
     */
    @Setter
    @Getter
    @Volatile
    private var working = false

    /**
     * 工作日标记
     */
    @Getter
    private var workDayFlagArr: Array<String>

    /**
     * 工作时间标记
     */
    @Getter
    private var workTimeFlagArr: Array<String>

    /**
     * 工作时间段
     */
    @Getter
    private var workTimeArr: Array<String>
    private var propertiesUtil: PropertiesUtil? = null
    private var scriptProperties: Properties? = null
    private var core: Core? = null
    private var enableUpdate = true

    @JvmStatic
    fun storeWorkDate() {
        ConfigUtil
        scriptProperties!!.setProperty(ConfigEnum.WORK_DAY_FLAG.name, java.lang.String.join(",", *workDayFlagArr))
        scriptProperties!!.setProperty(ConfigEnum.WORK_TIME_FLAG.name, java.lang.String.join(",", *workTimeFlagArr))
        scriptProperties!!.setProperty(ConfigEnum.WORK_TIME.name, java.lang.String.join(",", *workTimeArr))
        propertiesUtil.storeScriptProperties()
        checkWork()
    }

    fun stopWork() {
        working = false
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

    private fun checkWork() {
        if (!working) {
            if (!PauseStatus.isPause && isDuringWorkDate()) {
                workLog()
                core.start()
            } else if (enableUpdate && scriptProperties!!.getProperty(ConfigEnum.AUTO_UPDATE.name) == "true" && VersionListener.isCanUpdate()) {
                MainController.downloadRelease(
                    VersionListener.getLatestRelease(), false,
                    Consumer<String> { path: String? ->
                        enableUpdate = false
                        if (path == null) {
                            log.warn(
                                java.lang.String.format(
                                    "新版本<%s>下载失败",
                                    VersionListener.getLatestRelease().getTagName()
                                )
                            )
                        } else {
                            Platform.runLater { MainController.execUpdate(path) }
                        }
                    })
            }
        }
    }

    /**
     * 验证是否在工作时间内
     *
     * @return
     */
    fun isDuringWorkDate():Boolean{
        //        天校验
        if (workDayFlagArr[0] != "true" && workDayFlagArr[LocalDate.now().getDayOfWeek().getValue()] == "false"
        ) {
            return false
        }
        //        段校验
        val localTime: LocalTime = LocalTime.now()
        for (i in workTimeFlagArr.indices) {
            if (workTimeFlagArr[i] == "true" && workTimeArr[i] != "null") {
                val time =
                    workTimeArr[i].split("-".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                val start = time[0]
                val end = time[1]
                val nowTime: String = TimeSelector.TIME_FORMATTER.format(localTime)
                if (end.compareTo(start) == 0 ||
                    (end.compareTo(start) > 0 && nowTime.compareTo(start) >= 0 && nowTime.compareTo(end) <= 0)
                    ||
                    (end.compareTo(start) < 0 && (nowTime.compareTo(start) >= 0 || nowTime.compareTo(end) <= 0))
                ) {
                    return true
                }
            }
        }
        return false
    }

}
