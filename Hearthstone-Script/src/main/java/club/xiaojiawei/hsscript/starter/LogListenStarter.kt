package club.xiaojiawei.hsscript.starter

import club.xiaojiawei.config.log
import club.xiaojiawei.hsscript.config.LogListenerConfig
import club.xiaojiawei.hsscript.status.LogListenerStatus
import club.xiaojiawei.hsscript.utils.GameUtil

/**
 * 初始化和启动日志监听器
 * @author 肖嘉威
 * @date 2023/9/20 17:22
 */
object LogListenStarter : AbstractStarter() {

    override fun execStart() {
        val latestLogDir = GameUtil.getLatestLogDir()
        latestLogDir?.let {
            LogListenerStatus.logPath = it
            log.info { "游戏日志目录读取成功：" + it.absolutePath }
            LogListenerConfig.logListener.listen()
            startNextStarter()
        } ?: let {
            log.error { "游戏日志目录读取失败" }
        }
    }

}