package club.xiaojiawei.hsscript.starter

import club.xiaojiawei.bean.LogRunnable
import club.xiaojiawei.config.LAUNCH_PROGRAM_THREAD_POOL
import club.xiaojiawei.config.log
import club.xiaojiawei.hsscript.consts.GAME_CN_NAME
import club.xiaojiawei.hsscript.consts.PLATFORM_CN_NAME
import club.xiaojiawei.hsscript.status.PauseStatus
import club.xiaojiawei.hsscript.utils.GameUtil
import java.util.concurrent.TimeUnit

/**
 * 启动战网
 * @author 肖嘉威
 * @date 2023/7/5 14:39
 */
object PlatformStarter : AbstractStarter() {

    public override fun execStart() {
        if (GameUtil.isAliveOfGame()) {
            startNextStarter()
            return
        }
        if (GameUtil.isAliveOfPlatform()) {
            log.info { PLATFORM_CN_NAME + "正在运行" }
//            GameUtil.hidePlatformWindow()
        } else {
            log.info { "启动$PLATFORM_CN_NAME" }
            GameUtil.launchPlatform()
        }

        addTask(
            LAUNCH_PROGRAM_THREAD_POOL.scheduleAtFixedRate(LogRunnable {
                if (PauseStatus.isPause) {
                    stop()
                } else if (GameUtil.findPlatformHWND() != null || GameUtil.findLoginPlatformHWND() != null) {
                    startNextStarter()
                }
            }, 1, 100, TimeUnit.SECONDS)
        )
    }
}
