package club.xiaojiawei.hsscript.listener

import club.xiaojiawei.config.log
import club.xiaojiawei.hsscript.dll.SystemDll
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.enums.WindowEnum
import club.xiaojiawei.hsscript.status.PauseStatus
import club.xiaojiawei.hsscript.utils.ConfigUtil
import club.xiaojiawei.hsscript.utils.SystemUtil
import club.xiaojiawei.hsscript.utils.SystemUtil.offScreen
import club.xiaojiawei.hsscript.utils.WindowUtil
import club.xiaojiawei.hsscript.utils.runUI
import javafx.stage.Stage

/**
 * 系统监听器
 * @author 肖嘉威
 * @date 2025/3/7 20:32
 */
object SystemListener {

    val launch: Unit by lazy {
        PauseStatus.addListener { _, _, isPause: Boolean ->
            check()
        }
        WorkListener.workingProperty.addListener { _, _, isWorking: Boolean ->
            check()
        }
    }

    fun check() {
        if (!PauseStatus.isPause && !WorkListener.working) {
            var countdown = 10
            var text = ""
            var runnable: (() -> Unit)? = null
            if (ConfigUtil.getBoolean(ConfigEnum.AUTO_SLEEP)) {
                text = "${countdown}秒后将睡眠系统"
                runnable = {
                    SystemDll.INSTANCE.sleepSystem()
                }
            } else if (ConfigUtil.getBoolean(ConfigEnum.AUTO_OFF_SCREEN)) {
                text = "${countdown}秒后将关闭显示器"
                runnable = {
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
                            SystemDll.setWakeUpTimer(0)
                        },
                        null,
                        WindowUtil.getStage(WindowEnum.MAIN),
                        "阻止"
                    )
                    alert?.show()
                }
            }
            if (ConfigUtil.getBoolean(ConfigEnum.AUTO_WAKE)) {
                val time = WorkListener.getSecondsUntilNextWorkPeriod() - 120
                if (time > 0) {
                    if (!SystemDll.INSTANCE.isRunAsAdministrator()) {
                        text = "没有管理员权限，无法设置定时唤醒"
                        log.error { text }
                        SystemUtil.messageError(text)
                        return
                    }
                    if (!SystemDll.INSTANCE.checkS3Support()) {
                        text = "不支持S3睡眠，无法设置定时唤醒"
                        log.error { text }
                        SystemUtil.messageError(text)
                        return
                    }
                    if (!SystemDll.INSTANCE.enableWakeUpTimer()) {
                        text = "启用'允许唤醒定时器'失败，无法设置定时唤醒"
                        log.error { text }
                        SystemUtil.messageError(text)
                        return
                    }
                    if (!SystemDll.setWakeUpTimer(time.toInt())) {
                        text = "设置定时唤醒失败，定时时间:${time}秒"
                        log.error { text }
                        SystemUtil.messageError(text)
                        return
                    }
                }
            }
        } else {
            SystemDll.setWakeUpTimer(0)
        }
    }

}