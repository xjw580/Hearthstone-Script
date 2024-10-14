package club.xiaojiawei.hsscript.starter

import club.xiaojiawei.bean.LogRunnable
import club.xiaojiawei.config.EXTRA_THREAD_POOL
import club.xiaojiawei.config.LAUNCH_PROGRAM_THREAD_POOL
import club.xiaojiawei.config.log
import club.xiaojiawei.hsscript.config.StarterConfig
import club.xiaojiawei.hsscript.consts.GAME_CN_NAME
import club.xiaojiawei.hsscript.consts.GAME_HWND
import club.xiaojiawei.hsscript.dll.SystemDll
import club.xiaojiawei.hsscript.status.PauseStatus
import club.xiaojiawei.hsscript.utils.GameUtil
import club.xiaojiawei.hsscript.utils.MouseUtil
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
        log.info { "开始检查$GAME_CN_NAME" }
        GameUtil.findGameHWND()?.let {
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
                    if (launchCount.incrementAndGet() > 50) {
                        log.info { "更改${GAME_CN_NAME}启动方式" }
                        GameUtil.launchPlatformAndGame()
                    } else if (launchCount.incrementAndGet() > 100) {
                        log.warn { "打开${GAME_CN_NAME}失败次数过多，重新执行启动器链" }
                        stop()
                        EXTRA_THREAD_POOL.schedule({
                            GameUtil.killLoginPlatform()
                            GameUtil.killPlatform()
                            launchCount.set(0)
                            StarterConfig.starter.start()
                        }, 1, TimeUnit.SECONDS)
                        return@LogRunnable
                    }
                    if (GameUtil.isAliveOfGame()) {
//                    游戏刚启动时可能找不到窗口句柄
                        GameUtil.findGameHWND()?.let {
                            updateGameMsg(it)
                            startNextStarter()
                        } ?: let {
                            log.info { "${GAME_CN_NAME}已在运行，但未找到对应窗口句柄" }
                            return@LogRunnable
                        }
                    } else {
                        launchGameBySendMessage()
                    }
                }
            }, 100, 200, TimeUnit.MILLISECONDS)
        )
    }

    private fun launchGameBySendMessage() {
        log.info { "正在打开$GAME_CN_NAME" }
        MouseUtil.leftButtonClick(
            Point(145, 120),
            GameUtil.findPlatformHWND()
        )
    }

    private fun updateGameMsg(gameHWND: HWND) {
        log.info { GAME_CN_NAME + "正在运行" }
        GAME_HWND = gameHWND
        GameUtil.hidePlatformWindow()
        GameUtil.updateGameRect()
        Thread.ofVirtual().name("Update GameRect VThread").start {
            Thread.sleep(3000)
            GameUtil.updateGameRect()
            SystemDll.INSTANCE.changeWindow(GAME_HWND, true)
            SystemDll.INSTANCE.changeInput(GAME_HWND, true)
        }
    }

}
