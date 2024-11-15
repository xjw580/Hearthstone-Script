package club.xiaojiawei.hsscript.listener.log

import club.xiaojiawei.config.log
import club.xiaojiawei.enums.ModeEnum
import club.xiaojiawei.hsscript.core.Core
import club.xiaojiawei.hsscript.listener.WorkListener
import club.xiaojiawei.hsscript.status.Mode
import club.xiaojiawei.hsscript.status.PauseStatus
import club.xiaojiawei.hsscript.utils.GameUtil
import club.xiaojiawei.util.isFalse
import java.util.concurrent.TimeUnit

/**
 * 游戏界面监听器
 * @author 肖嘉威
 * @date 2023/7/5 14:55
 */
object ScreenLogListener :
    AbstractLogListener("LoadingScreen.log", 0, 1000L, TimeUnit.MILLISECONDS) {

    override fun dealOldLog() {
        var line: String
        var index: Int
        var finalMode: ModeEnum? = null
        while ((innerLogFile!!.readLine().also { line = it }) != null) {
            if ((line.indexOf("currMode").also { index = it }) != -1) {
                finalMode = ModeEnum.fromString(line.substring(index + 9))
            }
        }
        Mode.currMode = finalMode
    }

    private var dealing = false

    override fun dealNewLog() {
        if (dealing) return
        dealing = true
        innerLogFile?.let {
            var line: String?
            while (!PauseStatus.isPause && WorkListener.working) {
                line = it.readLine()
                if (line == null || line.isEmpty()) {
                    break
                }
                resolveLog(line)?.let {
                    Mode.currMode = it
                }
            }
        }
        dealing = false
    }

    private fun resolveLog(line: String?): ModeEnum? {
        return line?.let { l ->
            var index: Int
            if ((l.indexOf("currMode").also { index = it }) != -1) {
                return ModeEnum.valueOf(l.substring(index + 9))
            } else if (l.contains("OnDestroy()")) {
                Thread.sleep(2000)
                GameUtil.isAliveOfGame().isFalse {
                    log.info { "检测到游戏关闭，准备重启游戏" }
                    Core.restart()
                }
            }
            null
        }
    }
}
