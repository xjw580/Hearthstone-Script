package club.xiaojiawei.hsscript.starter

import club.xiaojiawei.bean.LogRunnable
import club.xiaojiawei.config.LAUNCH_PROGRAM_THREAD_POOL
import club.xiaojiawei.config.log
import club.xiaojiawei.hsscript.consts.ScriptStaticData
import club.xiaojiawei.hsscript.status.PauseStatus
import club.xiaojiawei.hsscript.utils.GameUtil
import club.xiaojiawei.hsscript.utils.SystemUtil
import java.util.concurrent.TimeUnit

/**
 * 启动战网
 * @author 肖嘉威
 * @date 2023/7/5 14:39
 */
object PlatformStarter : AbstractStarter() {

    public override fun execStart() {
        if (SystemUtil.isAliveOfGame()) {
            startNextStarter()
            return
        }
        log.info { "正在进入" + ScriptStaticData.PLATFORM_CN_NAME + ScriptStaticData.GAME_CN_NAME + "启动页" }
        GameUtil.cmdLaunchPlatformAndGame()
        GameUtil.hidePlatformWindow()
        addTask(
            LAUNCH_PROGRAM_THREAD_POOL.scheduleAtFixedRate(LogRunnable {
                if (PauseStatus.isPause) {
                    stop()
                } else if (SystemUtil.findPlatformHWND() != null || SystemUtil.findLoginPlatformHWND() != null) {
                    startNextStarter()
                }
            }, 1, 1, TimeUnit.SECONDS)
        )
    }
}
