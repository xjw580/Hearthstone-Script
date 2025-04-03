package club.xiaojiawei.hsscript.listener

import club.xiaojiawei.config.log
import club.xiaojiawei.hsscript.dll.CSystemDll
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.enums.MouseControlModeEnum
import club.xiaojiawei.hsscript.enums.WindowEnum
import club.xiaojiawei.hsscript.status.PauseStatus
import club.xiaojiawei.hsscript.utils.*
import club.xiaojiawei.hsscript.utils.SystemUtil.offScreen
import club.xiaojiawei.util.isFalse
import club.xiaojiawei.util.isTrue
import com.sun.jna.platform.win32.User32
import javafx.stage.Stage

/**
 * 系统监听器
 * @author 肖嘉威
 * @date 2025/3/7 20:32
 */
object SystemSleepListener {

    val launch: Unit by lazy {
        WorkListener.addChangeListener { _, _, isWorking: Boolean ->
            if (ConfigExUtil.getMouseControlMode() === MouseControlModeEnum.DRIVE) {
                isWorking.isTrue {
                    CSystemDll.safeRefreshDriver()
                }.isFalse {
                    CSystemDll.safeReleaseDriver()
                }
            }
            go {
                check()
            }
        }
    }

    fun check() {
        if (!PauseStatus.isPause && !WorkListener.working) {
            Thread.sleep(2000)
            if (!PauseStatus.isPause && !WorkListener.working) {
                var countdown = 10
                var text = ""
                var runnable: (() -> Unit)? = null
                if (ConfigUtil.getBoolean(ConfigEnum.AUTO_SLEEP)) {
                    text = "${countdown}秒后将睡眠系统"
                    runnable = {
                        if (ConfigUtil.getBoolean(ConfigEnum.AUTO_LOCK_SCREEN)) {
                            User32.INSTANCE.LockWorkStation()
                        }
                        CSystemDll.INSTANCE.sleepSystem()
                    }
                } else if (ConfigUtil.getBoolean(ConfigEnum.AUTO_OFF_SCREEN)) {
                    text = "${countdown}秒后将关闭显示器"
                    runnable = {
                        if (ConfigUtil.getBoolean(ConfigEnum.AUTO_LOCK_SCREEN)) {
                            User32.INSTANCE.LockWorkStation()
                        }
                        offScreen()
                    }
                }
                runnable?.let {
                    var alert: Stage? = null
                    val thread = Thread.ofVirtual().name("CountDownVThread").start {
                        runCatching {
                            while (--countdown >= 0) {
                                Thread.sleep(1000)
                            }
                        }.onSuccess {
                            runUI {
                                alert?.hide()
                            }
                            runnable()
                        }
                    }
                    runUI {
                        alert = WindowUtil.createAlert(
                            text,
                            null,
                            {
                                thread.interrupt()
                                if (ConfigUtil.getBoolean(ConfigEnum.AUTO_WAKE)) {
                                    CSystemDll.setWakeUpTimer(0)
                                }
                            },
                            null,
                            WindowUtil.getStage(WindowEnum.MAIN),
                            "阻止"
                        )
                        alert?.show()
                    }
                }
                if (ConfigUtil.getBoolean(ConfigEnum.AUTO_WAKE)) {
                    val time = WorkListener.getSecondsUntilNextWorkPeriod() - 60
                    if (time > 0) {
                        if (!CSystemDll.INSTANCE.isRunAsAdministrator()) {
                            text = "没有管理员权限，无法设置定时唤醒"
                            log.error { text }
                            SystemUtil.messageError(text)
                            return
                        }
                        if (!CSystemDll.INSTANCE.checkS3Support()) {
                            text = "不支持S3睡眠，无法设置定时唤醒"
                            log.error { text }
                            SystemUtil.messageError(text)
                            return
                        }
                        if (!CSystemDll.INSTANCE.enableWakeUpTimer()) {
                            text = "启用'允许唤醒定时器'失败，无法设置定时唤醒"
                            log.error { text }
                            SystemUtil.messageError(text)
                            return
                        }
                        if (!CSystemDll.setWakeUpTimer(time.toInt())) {
                            text = "设置定时唤醒失败，定时时间:${time}秒"
                            log.error { text }
                            SystemUtil.messageError(text)
                            return
                        }
                    }
                }
            }
        } else if (ConfigUtil.getBoolean(ConfigEnum.AUTO_WAKE)) {
            CSystemDll.setWakeUpTimer(0)
        }
    }

}