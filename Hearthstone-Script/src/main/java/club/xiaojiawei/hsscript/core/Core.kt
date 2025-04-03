package club.xiaojiawei.hsscript.core

import club.xiaojiawei.config.CORE_THREAD_POOL
import club.xiaojiawei.config.log
import club.xiaojiawei.hsscript.config.StarterConfig
import club.xiaojiawei.hsscript.consts.GAME_CN_NAME
import club.xiaojiawei.hsscript.consts.PLATFORM_CN_NAME
import club.xiaojiawei.hsscript.dll.CSystemDll
import club.xiaojiawei.hsscript.enums.WindowEnum
import club.xiaojiawei.hsscript.listener.WorkListener
import club.xiaojiawei.hsscript.status.Mode
import club.xiaojiawei.hsscript.status.PauseStatus
import club.xiaojiawei.hsscript.status.ScriptStatus
import club.xiaojiawei.hsscript.utils.*
import club.xiaojiawei.util.isFalse
import club.xiaojiawei.util.isTrue

/**
 * 控制脚本的启动
 * @author 肖嘉威
 * @date 2023/7/5 13:15
 */
object Core {

    @Volatile
    var lastActiveTime: Long = 0

    val launch: Unit by lazy {
        PauseStatus.addChangeListener { _, _, newValue ->
            newValue.isTrue {
                CSystemDll.INSTANCE.changeWindow(false)
                WorkListener.working = false
                Mode.reset()
                runUI { WindowUtil.getStage(WindowEnum.MAIN)?.show() }
                log.info { "当前处于【暂停】状态" }
            }.isFalse {
                if (WorkListener.isDuringWorkDate()) {
                    start()
                } else {
                    WorkListener.cannotWorkLog()
                }
                log.info { "当前处于【开始】状态" }
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
                if (ScriptStatus.isValidProgramPath) {
                    WorkListener.working = true
                    StarterConfig.starter.start()
                } else if (!PauseStatus.isPause) {
                    SystemUtil.notice("需要配置" + GAME_CN_NAME + "和" + PLATFORM_CN_NAME + "的路径")
                    WindowUtil.showStage(WindowEnum.SETTINGS, WindowUtil.getStage(WindowEnum.MAIN))
                    PauseStatus.isPause = true
                }
            }
        }
    }

    /**
     * 重启脚本
     */
    fun restart(sync: Boolean = false) {
        val exec = {
            PauseStatus.asyncSetPause(true)
            GameUtil.killGame(true)
            log.info { "${GAME_CN_NAME}重启中……" }
            PauseStatus.isPause = false
        }
        if (sync) {
            exec()
        } else {
            CORE_THREAD_POOL.execute { exec() }
        }
    }

}