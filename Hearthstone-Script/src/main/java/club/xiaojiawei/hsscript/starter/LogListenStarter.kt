package club.xiaojiawei.hsscript.starter

import club.xiaojiawei.hsscript.config.SpringBeanConfig
import club.xiaojiawei.config.log
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.listener.log.AbstractLogListener
import club.xiaojiawei.hsscript.status.LogListenerStatus
import club.xiaojiawei.util.isFalse
import club.xiaojiawei.util.isTrue
import club.xiaojiawei.hsscript.utils.ConfigUtil
import java.io.File
import java.nio.file.Path
import java.util.*

/**
 * 初始化和启动日志监听器
 * @author 肖嘉威
 * @date 2023/9/20 17:22
 */
object LogListenStarter : AbstractStarter() {

    //    todo refactor
    private val logListener: AbstractLogListener? = null

    override fun execStart() {

        val gameLogDir =
            Path.of(ConfigUtil.getString(ConfigEnum.GAME_PATH), SpringBeanConfig.springData.gameLogPath).toFile()

        var files: Array<File>? = null
        gameLogDir.exists().isTrue {
            files = gameLogDir.listFiles()
        }

        files.isNullOrEmpty().isTrue {
            log.error { "游戏日志目录读取失败" }
        }.isFalse {
            files?.let {
                Arrays.sort(it, Comparator.comparing { obj: File -> obj.name })
                LogListenerStatus.logDir = it[it.size - 1]
                log.info { "游戏日志目录读取成功：" + it[it.size - 1].absoluteFile }
                logListener!!.listen()
                startNextStarter()
            }
        }
    }
}
