package club.xiaojiawei.hsscript.listener

import club.xiaojiawei.config.log
import club.xiaojiawei.hsscript.dll.CSystemDll
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.enums.WindowEnum
import club.xiaojiawei.hsscript.status.PauseStatus
import club.xiaojiawei.hsscript.utils.*
import club.xiaojiawei.hsscript.utils.SystemUtil.offScreen
import com.sun.jna.platform.win32.User32
import javafx.stage.Stage
import java.util.concurrent.locks.ReentrantLock

/**
 * 系统监听器
 * @author 肖嘉威
 * @date 2025/3/7 20:32
 */
object SystemSleepListener {

    val launch: Unit by lazy {
        WorkListener.addChangeListener { _, _, isWorking: Boolean ->
            go {
                check()
            }
        }
        PauseStatus.addChangeListener { _, _, isPause: Boolean ->
            go {
                check()
            }
        }
    }

    private val lock = ReentrantLock()

    private fun check() {
        if (lock.isLocked) return
        if (!WorkListener.working) {
            Thread.sleep(1000)
            if (lock.isLocked) return
            if (!WorkListener.working && !PauseStatus.isPause) {
                try {
                    lock.lock()
                    var countdown = 10
                    val countdownTime = countdown
                    val textList = mutableListOf<String>()
                    val runnable = mutableListOf<Runnable>()
                    if (ConfigUtil.getBoolean(ConfigEnum.AUTO_LOCK_SCREEN)) {
                        textList.add("锁屏")
                        runnable.add { User32.INSTANCE.LockWorkStation() }
                    }
                    if (ConfigUtil.getBoolean(ConfigEnum.AUTO_SLEEP)) {
                        textList.add("睡眠系统")
                        runnable.add { CSystemDll.INSTANCE.sleepSystem() }
                    } else if (ConfigUtil.getBoolean(ConfigEnum.AUTO_OFF_SCREEN)) {
                        textList.add("关闭显示器")
                        runnable.add { offScreen() }
                    }
                    if (runnable.isNotEmpty()) {
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
                                for (r in runnable) {
                                    r.run()
                                }
                            }
                        }
                        runUI {
                            var headText = "${countdownTime}秒后将"
                            for (s in textList) {
                                headText += s
                                headText += "，"
                            }
                            headText.removeSuffix("，")
                            alert = WindowUtil.createAlert(
                                headText,
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
                        val text: String
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
                } finally {
                    lock.unlock()
                }
            }
        } else if (ConfigUtil.getBoolean(ConfigEnum.AUTO_WAKE)) {
            CSystemDll.setWakeUpTimer(0)
        }
    }

}