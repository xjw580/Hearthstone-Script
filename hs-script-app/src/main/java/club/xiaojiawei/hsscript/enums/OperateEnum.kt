package club.xiaojiawei.hsscript.enums

import club.xiaojiawei.hsscriptbase.config.log
import club.xiaojiawei.hsscript.consts.GAME_CN_NAME
import club.xiaojiawei.hsscript.consts.PLATFORM_CN_NAME
import club.xiaojiawei.hsscript.dll.CSystemDll
import club.xiaojiawei.hsscript.listener.WorkTimeListener
import club.xiaojiawei.hsscript.utils.GameUtil
import club.xiaojiawei.hsscript.utils.SystemUtil

/**
 * @author 肖嘉威
 * @date 2025/4/8 15:19
 */
enum class OperateEnum(val value: String, val exec: () -> Boolean, val order: Int = 0) {

    OFF_SCREEN("关闭屏幕", {
        SystemUtil.offScreen()
        true
    }, order = 5),
    SLEEP_SYSTEM("睡眠", {
        var text = ""
        var res = true

        do {
            if (!CSystemDll.INSTANCE.checkS3Support()) {
                text = "不支持'S3睡眠'，无法设置定时唤醒"
                log.error { text }
                SystemUtil.messageError(text)
                res = false
                break
            }
            if (!CSystemDll.INSTANCE.enableWakeUpTimer()) {
                text = "启用'允许唤醒定时器'失败，无法设置定时唤醒"
                log.error { text }
                SystemUtil.messageError(text)
                res = false
                break
            }
            val nextWakeUpTime = WorkTimeListener.getSecondsUntilNextWorkPeriod()
            if (nextWakeUpTime <= 0) {
                text = "没有可唤醒时间，无法设置定时唤醒"
                log.error { text }
                SystemUtil.messageError(text)
                res = false
                break
            }
            val time = nextWakeUpTime - 60
            if (!CSystemDll.setWakeUpTimer(time.toInt())) {
                text = "设置定时唤醒失败，定时时间:[${time}]秒"
                log.error { text }
                SystemUtil.messageError(text)
                res = false
                break
            }
            CSystemDll.INSTANCE.sleepSystem()
            println("sleep")
        } while (false)

        res
    }, order = 4),
    SHUTDOWN("关机", {
        SystemUtil.shutdownSystem()
    }, order = 3),
    LOCK_SCREEN("锁屏", {
        SystemUtil.lockScreen()
    }, order = 2),
    CLOSE_PLATFORM("关闭${PLATFORM_CN_NAME}", {
        GameUtil.killPlatform()
        GameUtil.killLoginPlatform()
        true
    }, order = 1),
    CLOSE_GAME("关闭${GAME_CN_NAME}", {
        GameUtil.killGame()
        true
    }, order = 0),
    ;

}