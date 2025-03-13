package club.xiaojiawei.hsscript.starter

import club.xiaojiawei.bean.LRunnable
import club.xiaojiawei.config.EXTRA_THREAD_POOL
import club.xiaojiawei.config.LAUNCH_PROGRAM_THREAD_POOL
import club.xiaojiawei.config.log
import club.xiaojiawei.hsscript.config.StarterConfig
import club.xiaojiawei.hsscript.data.GAME_CN_NAME
import club.xiaojiawei.hsscript.data.GAME_HWND
import club.xiaojiawei.hsscript.dll.SystemDll
import club.xiaojiawei.hsscript.enums.MouseControlModeEnum
import club.xiaojiawei.hsscript.status.PauseStatus
import club.xiaojiawei.hsscript.utils.GameUtil
import club.xiaojiawei.hsscript.utils.MouseUtil
import club.xiaojiawei.hsscript.utils.SystemUtil
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinDef.HWND
import java.awt.Point
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * 启动游戏
 * @author 肖嘉威
 * @date 2023/7/5 14:38
 */
class GameStarter : AbstractStarter() {

    private var latestLogDir: File? = null

    public override fun execStart() {
        log.info { "开始检查$GAME_CN_NAME" }
        GameUtil.findGameHWND()?.let {
            next(it)
            return
        }
        latestLogDir = GameUtil.getLatestLogDir()
        var startTime = System.currentTimeMillis()
        var firstLogLaunch = true
        var firstLogSecondaryLaunch = true
        addTask(
            LAUNCH_PROGRAM_THREAD_POOL.scheduleAtFixedRate(LRunnable {
                val diffTime = System.currentTimeMillis() - startTime
                if (diffTime > 20_000) {
                    log.warn { "启动${GAME_CN_NAME}失败次数过多，重新执行启动器链" }
                    stopTask()
                    startTime = System.currentTimeMillis()
                    EXTRA_THREAD_POOL.schedule({
                        GameUtil.killGame(true)
                        GameUtil.killLoginPlatform()
                        GameUtil.killPlatform()
                        StarterConfig.starter.start()
                    }, 1, TimeUnit.SECONDS)
                    return@LRunnable
                } else if (diffTime > 5_000) {
                    if (firstLogSecondaryLaunch){
                        firstLogSecondaryLaunch = false
                        log.info { "更改${GAME_CN_NAME}启动方式" }
                    }
                    GameUtil.launchPlatformAndGame()
                }
                if (GameUtil.isAliveOfGame()) {
//                    游戏刚启动时可能找不到窗口句柄
                    GameUtil.findGameHWND()?.let {
                        next(it)
                    } ?: let {
                        if (diffTime > 5_000) {
                            log.info { "${GAME_CN_NAME}已在运行，但未找到对应窗口句柄" }
                            return@LRunnable
                        }
                    }
                } else {
                    if (firstLogLaunch){
                        firstLogLaunch = false
                        log.info { "正在启动$GAME_CN_NAME" }
                    }
                    launchGameBySendMessage()
                }
            }, 100, 500, TimeUnit.MILLISECONDS)
        )
    }

    private fun launchGameBySendMessage() {
        val platformHWND = GameUtil.findPlatformHWND()
        val rect = WinDef.RECT()
        SystemUtil.updateRECT(platformHWND, rect)
        MouseUtil.leftButtonClick(
            Point(145, rect.bottom - rect.top - 150),
            platformHWND,
            MouseControlModeEnum.MESSAGE.code
        )
    }

    private fun next(gameHWND: HWND) {
        log.info { GAME_CN_NAME + "正在运行" }
        if (latestLogDir != null) {
            log.info { "等待${GAME_CN_NAME}创建最新日志文件夹" }
            while (!PauseStatus.isPause) {
                val currentLatestLogDir = GameUtil.getLatestLogDir()
                if (currentLatestLogDir != null) {
                    if (currentLatestLogDir > latestLogDir){
                        log.info { "${GAME_CN_NAME}已创建最新日志文件夹：${currentLatestLogDir.absolutePath}" }
                        break
                    }
                }
                Thread.sleep(200)
            }
        }
        updateGameMsg(gameHWND)
        startNextStarter()
    }

    private fun updateGameMsg(gameHWND: HWND) {
        GAME_HWND = gameHWND
        GameUtil.updateGameRect()
        Thread.ofVirtual().name("Update GameRect VThread").start {
            Thread.sleep(3000)
            GameUtil.updateGameRect()
            SystemDll.INSTANCE.changeWindow(GAME_HWND, true)
            SystemDll.INSTANCE.changeInput(GAME_HWND, true)
        }
    }

}
