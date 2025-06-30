package club.xiaojiawei.hsscript.listener.log

import club.xiaojiawei.config.log
import club.xiaojiawei.enums.ModeEnum
import club.xiaojiawei.hsscript.consts.GAME_MODE_LOG_NAME
import club.xiaojiawei.hsscript.core.Core
import club.xiaojiawei.hsscript.listener.WorkTimeListener
import club.xiaojiawei.hsscript.status.Mode
import club.xiaojiawei.hsscript.status.PauseStatus
import club.xiaojiawei.hsscript.utils.GameUtil
import club.xiaojiawei.util.isFalse
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

/**
 * 游戏界面监听器
 * @author 肖嘉威
 * @date 2023/7/5 14:55
 */
object ScreenLogListener :
    AbstractLogListener(GAME_MODE_LOG_NAME, 0, 50L, TimeUnit.MILLISECONDS) {

    private const val CURR_MODE_STR = "currMode="

    private const val CURR_MODE_STR_LEN = CURR_MODE_STR.length

    private const val NEXT_MODE_STR = "nextMode="

    private const val NEXT_MODE_STR_LEN = NEXT_MODE_STR.length

    private val formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSS")

    override fun dealOldLog() {
        var line: String
        var index: Int
        var finalCurrMode: ModeEnum? = null
        var finalNextMode: ModeEnum? = null
        while ((innerLogFile!!.readLine().also { line = it }) != null) {
            if ((line.indexOf(CURR_MODE_STR).also { index = it }) != -1) {
                finalCurrMode = ModeEnum.fromString(line.substring(index + CURR_MODE_STR_LEN))
                finalNextMode = null
            } else if ((line.indexOf(NEXT_MODE_STR).also { index = it }) != -1) {
                val blankIndex = line.indexOf(" ", index)
                finalNextMode = ModeEnum.fromString(line.substring(index + NEXT_MODE_STR_LEN, blankIndex))
                finalCurrMode = null
            }
        }
        finalCurrMode?.let {
            Mode.currMode = finalCurrMode
        } ?: let {
            finalNextMode?.let {
                Mode.nextMode = it
            }
        }

    }

    private var dealing = false

    override fun dealNewLog() {
        if (dealing) return
        dealing = true
        innerLogFile?.let {
            var line: String?
            while (!PauseStatus.isPause && WorkTimeListener.working) {
                line = it.readLine()
                if (line.isNullOrEmpty()) {
                    break
                }
                resolveLog(line)
            }
        }
        dealing = false
    }

    private fun resolveLog(line: String?) {
        line?.let { l ->
            var index: Int
            if ((l.indexOf(CURR_MODE_STR).also { index = it }) != -1) {
                val logTime = LocalTime.parse(line.substring(2, 18), formatter)
                val nowTime = LocalTime.now()
                val logDiffTime =
                    Duration.between(logTime, nowTime).toMillis()
                if (logDiffTime > 1500) {
                    log.warn { "${GAME_MODE_LOG_NAME}日志实际打印时间与输出时间相差过大，diff:${logDiffTime}，log:${line}，logTime:${logTime}，nowTime:${nowTime}" }
                }
                Mode.currMode = ModeEnum.fromString(l.substring(index + CURR_MODE_STR_LEN))
            } else if ((l.indexOf(NEXT_MODE_STR).also { index = it }) != -1) {
                val blankIndex = l.indexOf(" ", index)
                Mode.nextMode = ModeEnum.fromString(l.substring(index + NEXT_MODE_STR_LEN, blankIndex))
            } else if (l.contains("OnDestroy()")) {
                Thread.sleep(2000)
                GameUtil.isAliveOfGame().isFalse {
                    log.info { "检测到游戏关闭，准备重启游戏" }
                    Core.restart()
                }
            }
        }
    }
}
