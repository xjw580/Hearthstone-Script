package club.xiaojiawei.hsscript.core

import club.xiaojiawei.config.CORE_THREAD_POOL
import club.xiaojiawei.hsscript.config.StarterConfig
import club.xiaojiawei.config.log
import club.xiaojiawei.hsscript.controller.javafx.MainController
import club.xiaojiawei.hsscript.consts.ScriptStaticData
import club.xiaojiawei.hsscript.enums.WindowEnum
import club.xiaojiawei.hsscript.status.PauseStatus
import club.xiaojiawei.hsscript.status.Work
import club.xiaojiawei.hsscript.utils.GameUtil
import club.xiaojiawei.hsscript.utils.SystemUtil
import club.xiaojiawei.hsscript.utils.WindowUtil
import club.xiaojiawei.hsscript.utils.platformRunLater

/**
 * 控制脚本的启动
 * @author 肖嘉威
 * @date 2023/7/5 13:15
 */
object Core {

    /**
     * 启动脚本
     */
    @Synchronized
    fun start() {
        if (Work.isWorking()) {
            log.warn { "正在工作，无法重复工作" }
            return
        }
        Work.setWorking(true)
        CORE_THREAD_POOL.execute {
            if (!ScriptStaticData.isSetPath()) {
                SystemUtil.notice("需要配置" + ScriptStaticData.GAME_CN_NAME + "和" + ScriptStaticData.PLATFORM_CN_NAME + "的路径")
                platformRunLater { WindowUtil.showStage(WindowEnum.SETTINGS) }
                PauseStatus.isPause = true
            } else if (!PauseStatus.isPause) {
                val controller = WindowUtil.getController(WindowEnum.MAIN)
                if (controller is MainController){
                    controller.expandedLogPane()
                }
//                todo refactor
                log.info { "热键：Ctrl+P 开始/停止程序,Alt+P 关闭程序" }
                StarterConfig.starter.start()
            }
        }
    }

    /**
     * 重启脚本
     */
    fun restart() {
        CORE_THREAD_POOL.execute {
            PauseStatus.isPause = true
            GameUtil.killGame()
            log.info { "游戏重启中……" }
            PauseStatus.isPause = false
        }
    }

}
