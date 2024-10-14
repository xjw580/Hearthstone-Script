package club.xiaojiawei.hsscript.core

import club.xiaojiawei.config.CORE_THREAD_POOL
import club.xiaojiawei.config.log
import club.xiaojiawei.hsscript.config.StarterConfig
import club.xiaojiawei.hsscript.consts.GAME_CN_NAME
import club.xiaojiawei.hsscript.consts.GAME_HWND
import club.xiaojiawei.hsscript.consts.PLATFORM_CN_NAME
import club.xiaojiawei.hsscript.consts.setPath
import club.xiaojiawei.hsscript.dll.SystemDll
import club.xiaojiawei.hsscript.enums.WindowEnum
import club.xiaojiawei.hsscript.listener.WorkListener
import club.xiaojiawei.hsscript.status.Mode
import club.xiaojiawei.hsscript.status.PauseStatus
import club.xiaojiawei.hsscript.strategy.AbstractModeStrategy
import club.xiaojiawei.hsscript.utils.GameUtil
import club.xiaojiawei.hsscript.utils.SystemUtil
import club.xiaojiawei.hsscript.utils.WindowUtil
import club.xiaojiawei.hsscript.utils.platformRunLater
import club.xiaojiawei.util.isFalse
import club.xiaojiawei.util.isTrue

/**
 * 控制脚本的启动
 * @author 肖嘉威
 * @date 2023/7/5 13:15
 */
object Core {

    init {
        PauseStatus.addListener { _, _, newValue ->
            newValue.isTrue {
                SystemDll.INSTANCE.changeInput(GAME_HWND, false)
                SystemDll.INSTANCE.changeWindow(GAME_HWND, false)
                WorkListener.working = false
                Mode.reset()
                log.info { "当前处于【停止】状态" }
            }.isFalse {
                if (WorkListener.isDuringWorkDate()) {
                    start()
                } else {
                    WorkListener.cannotWorkLog()
                }
                log.info { "当前处于【运行】状态" }
            }
        }
    }

    /**
     * 启动脚本
     */
    fun start() {
        if (WorkListener.working) {
            log.warn { "正在工作，无法重复工作" }
            return
        }
        CORE_THREAD_POOL.execute {
            synchronized(Core.javaClass) {
                if (WorkListener.working) return@execute
                if (!setPath) {
                    SystemUtil.notice("需要配置" + GAME_CN_NAME + "和" + PLATFORM_CN_NAME + "的路径")
                    platformRunLater { WindowUtil.showStage(WindowEnum.SETTINGS) }
                    PauseStatus.isPause = true
                } else if (!PauseStatus.isPause) {
                    WorkListener.working = true
                    StarterConfig.starter.start()
                }
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
