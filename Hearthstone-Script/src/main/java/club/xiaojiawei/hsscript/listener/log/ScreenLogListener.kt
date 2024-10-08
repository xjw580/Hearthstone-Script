package club.xiaojiawei.hsscript.listener.log

import club.xiaojiawei.config.log
import club.xiaojiawei.hsscript.core.Core
import club.xiaojiawei.hsscript.consts.SpringData
import club.xiaojiawei.enums.ModeEnum
import club.xiaojiawei.hsscript.status.Mode
import club.xiaojiawei.hsscript.status.PauseStatus
import club.xiaojiawei.util.isFalse
import club.xiaojiawei.hsscript.utils.SystemUtil
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

/**
 * 游戏界面监听器
 * @author 肖嘉威
 * @date 2023/7/5 14:55
 */
@Slf4j
@Component
class ScreenLogListener @Autowired constructor(springData: SpringData) :
    AbstractLogListener(springData.screenLogName, 0, 1500L, TimeUnit.MILLISECONDS) {

    override fun dealOldLog() {
        var line: String
        var index: Int
        var finalMode: ModeEnum? = null
        while ((innerLogFile!!.readLine().also { line = it }) != null) {
            if ((line.indexOf("currMode").also { index = it }) != -1) {
                finalMode = ModeEnum.valueOf(line.substring(index + 9))
            }
        }
        Mode.setCurrMode(finalMode)
    }

    override fun dealNewLog() {
        while (!PauseStatus.isPause) {
            innerLogFile?.let {
                it.readLine()?.let { line ->
                    if (line.isBlank()) {
                        return
                    } else {
                        Mode.setCurrMode(resolveLog(line))
                    }
                } ?: return
            } ?: return
        }
    }

    private fun resolveLog(line: String?): ModeEnum? {
        return line?.let {l->
            var index: Int
            if ((l.indexOf("currMode").also { index = it }) != -1) {
                return ModeEnum.valueOf(l.substring(index + 9))
            } else if (l.contains("OnDestroy()")) {
                Thread.sleep(2000)
                SystemUtil.isAliveOfGame().isFalse {
                    log.info { "检测到游戏关闭，准备重启游戏" }
                    Core.restart()
                }
            }
            null
        }
    }
}
