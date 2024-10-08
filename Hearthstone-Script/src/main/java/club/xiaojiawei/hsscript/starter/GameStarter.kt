package club.xiaojiawei.hsscript.starter

import club.xiaojiawei.bean.LogRunnable
import club.xiaojiawei.config.EXTRA_THREAD_POOL
import club.xiaojiawei.config.LAUNCH_PROGRAM_THREAD_POOL
import club.xiaojiawei.hsscript.config.StarterConfig
import club.xiaojiawei.config.log
import club.xiaojiawei.hsscript.consts.ScriptStaticData
import club.xiaojiawei.hsscript.status.PauseStatus
import club.xiaojiawei.hsscript.utils.GameUtil
import club.xiaojiawei.hsscript.utils.MouseUtil
import club.xiaojiawei.hsscript.utils.SystemUtil
import com.sun.jna.platform.win32.WinDef.HWND
import java.awt.Point
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * 启动游戏
 * @author 肖嘉威
 * @date 2023/7/5 14:38
 */
object GameStarter : AbstractStarter() {

    public override fun execStart() {
        log.info { "开始检查" + ScriptStaticData.GAME_CN_NAME }
        SystemUtil.findGameHWND()?.let {
            updateGameMsg(it)
            startNextStarter()
            return
        }
        val launchCount = AtomicInteger()

        addTask(
            LAUNCH_PROGRAM_THREAD_POOL.scheduleAtFixedRate(LogRunnable {
                if (PauseStatus.isPause) {
                    stop()
                } else {
                    if (launchCount.incrementAndGet() > 3) {
                        launchGameBySendMessage()
                    } else if (launchCount.incrementAndGet() > 4) {
                        log.warn { "打开炉石失败次数过多，重新执行启动器链" }
                        stop()
                        EXTRA_THREAD_POOL.schedule({
                            SystemUtil.killLoginPlatform()
                            SystemUtil.killPlatform()
                            launchCount.set(0)
                            StarterConfig.starter.start()
                        }, 1, TimeUnit.SECONDS)
                        return@LogRunnable
                    }
                    if (SystemUtil.isAliveOfGame()) {
//                    游戏刚启动时可能找不到窗口句柄
                        SystemUtil.findGameHWND()?.let {
                            updateGameMsg(it)
                            startNextStarter()
                        } ?: let {
                            log.info { "炉石传说已在运行，但未找到对应窗口句柄" }
                            return@LogRunnable
                        }
                    } else {
                        GameUtil.cmdLaunchPlatformAndGame()
                    }
                }
            }, 5, 20, TimeUnit.SECONDS)
        )
    }

    private fun launchGameBySendMessage() {
        log.info { "正在通过SendMessage打开" + ScriptStaticData.GAME_CN_NAME }
        MouseUtil.leftButtonClick(
            Point(145, 120),
            SystemUtil.findPlatformHWND()
        )
    }

    private fun updateGameMsg(gameHWND: HWND) {
        ScriptStaticData.setGameHWND(gameHWND)
        GameUtil.hidePlatformWindow()
        SystemUtil.updateGameRect()
        Thread.ofVirtual().name("Update GameRect VThread").start {
            Thread.sleep(3000)
            SystemUtil.updateGameRect()
        }
    }

}
