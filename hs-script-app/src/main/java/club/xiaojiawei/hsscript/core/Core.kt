package club.xiaojiawei.hsscript.core

import club.xiaojiawei.hsscriptbase.config.CORE_THREAD_POOL
import club.xiaojiawei.hsscriptbase.config.log
import club.xiaojiawei.hsscript.config.StarterConfig
import club.xiaojiawei.hsscript.consts.GAME_CN_NAME
import club.xiaojiawei.hsscript.consts.PLATFORM_CN_NAME
import club.xiaojiawei.hsscript.dll.CSystemDll
import club.xiaojiawei.hsscript.enums.MouseControlModeEnum
import club.xiaojiawei.hsscript.enums.OperateEnum
import club.xiaojiawei.hsscript.enums.WindowEnum
import club.xiaojiawei.hsscript.listener.WorkTimeListener
import club.xiaojiawei.hsscript.status.Mode
import club.xiaojiawei.hsscript.status.PauseStatus
import club.xiaojiawei.hsscript.status.ScriptStatus
import club.xiaojiawei.hsscript.utils.*
import club.xiaojiawei.hsscriptbase.util.isFalse
import club.xiaojiawei.hsscriptbase.util.isTrue
import java.util.concurrent.locks.ReentrantLock

/**
 * 控制脚本的启动
 * @author 肖嘉威
 * @date 2023/7/5 13:15
 */
object Core {
    @Volatile
    var lastActiveTime: Long = 0

    private val lock = ReentrantLock()

    val launch: Unit by lazy {
        PauseStatus.addChangeListener { _, _, newValue ->
            newValue
                .isTrue {
                    WorkTimeListener.working = false
                    Mode.reset()
                    runUI { WindowUtil.getStage(WindowEnum.MAIN)?.show() }
                    log.info { "当前处于【暂停】状态" }
                }.isFalse {
                    WorkTimeListener.checkWork()
                    if (WorkTimeListener.canWork()) {
                        start()
                    } else {
                        WorkTimeListener.cannotWorkLog()
                        runUI {
                            val alert =
                                WindowUtil.createAlert(
                                    "当前不在工作时间",
                                    "是否睡眠系统(下个可用时间会唤醒系统)",
                                    {
                                        OperateEnum.SLEEP_SYSTEM.exec()
                                    },
                                    {},
                                    WindowUtil.getStage(WindowEnum.MAIN),
                                )
                            go {
                                Thread.sleep(5_000)
                                runUI {
                                    alert.hide()
                                }
                            }
                            alert.show()
                        }
                    }
                    log.info { "当前处于【开始】状态" }
                }
        }
        WorkTimeListener.addChangeListener { _, _, isWorking: Boolean ->
            if (isWorking) {
                start(true)
            }
            if (ConfigExUtil.getMouseControlMode() === MouseControlModeEnum.DRIVE) {
                isWorking
                    .isTrue {
                        CSystemDll.safeRefreshDriver()
                    }.isFalse {
                        CSystemDll.safeReleaseDriver()
                    }
            }
        }
    }

    /**
     * 启动脚本
     */
    fun start(force: Boolean = false) {
        if ((!force && WorkTimeListener.working) || lock.isLocked) return

        CORE_THREAD_POOL.execute {
            try {
                if ((!force && WorkTimeListener.working) || !lock.tryLock()) return@execute

                if (ScriptStatus.isValidGameInstallPath && ScriptStatus.isValidPlatformProgramPath) {
                    WorkTimeListener.working = true
                    StarterConfig.starter.start()
                } else if (!PauseStatus.isPause) {
                    SystemUtil.notice("需要配置" + GAME_CN_NAME + "和" + PLATFORM_CN_NAME + "的路径")
                    WindowUtil.showStage(WindowEnum.SETTINGS, WindowUtil.getStage(WindowEnum.MAIN))
                    PauseStatus.isPause = true
                }
            } finally {
                lock.unlock()
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
