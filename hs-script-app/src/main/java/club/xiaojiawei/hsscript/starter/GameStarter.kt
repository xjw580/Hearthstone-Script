package club.xiaojiawei.hsscript.starter

import club.xiaojiawei.hsscriptbase.config.EXTRA_THREAD_POOL
import club.xiaojiawei.hsscriptbase.config.LAUNCH_PROGRAM_THREAD_POOL
import club.xiaojiawei.hsscriptbase.config.log
import club.xiaojiawei.hsscript.config.StarterConfig
import club.xiaojiawei.hsscript.consts.GAME_CN_NAME
import club.xiaojiawei.hsscript.dll.CSystemDll
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.enums.MouseControlModeEnum
import club.xiaojiawei.hsscript.status.PauseStatus
import club.xiaojiawei.hsscript.status.ScriptStatus
import club.xiaojiawei.hsscript.utils.*
import club.xiaojiawei.hsscriptbase.util.isFalse
import com.sun.jna.platform.win32.User32
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
        val gameHWND = ScriptStatus.gameHWND
        if (gameHWND != null && User32.INSTANCE.IsWindow(gameHWND)) {
            next(gameHWND)
            return
        }
        latestLogDir = GameUtil.getLatestLogDir()
        var startTime = System.currentTimeMillis()
        var firstLogLaunch = true
        var firstLogSecondaryLaunch = true
        addTask(
            LAUNCH_PROGRAM_THREAD_POOL.scheduleWithFixedDelay(
                {
                    do {
                        val diffTime = System.currentTimeMillis() - startTime
                        if (diffTime > 25_000) {
                            log.warn { "启动${GAME_CN_NAME}失败次数过多，重新执行启动器链" }
                            stopTask()
                            startTime = System.currentTimeMillis()
                            EXTRA_THREAD_POOL.schedule({
                                GameUtil.killGame(true)
                                GameUtil.killLoginPlatform()
                                GameUtil.killPlatform()
                                StarterConfig.starter.start()
                            }, 1, TimeUnit.SECONDS)
                            break
                        }
                        if (GameUtil.isAliveOfGame()) {
//                    游戏刚启动时可能找不到窗口句柄
                            GameUtil.findGameHWND()?.let {
                                next(it)
                            } ?: let {
                                if (diffTime > 10_000) {
                                    log.info { "${GAME_CN_NAME}已在运行，但未找到对应窗口句柄" }
                                }
                            }
                        } else {
                            if (diffTime > 5_000) {
                                if (firstLogSecondaryLaunch) {
                                    firstLogSecondaryLaunch = false
                                    log.info { "更改${GAME_CN_NAME}启动方式" }
                                }
                                GameUtil.launchPlatformAndGame()
                            } else {
                                if (firstLogLaunch) {
                                    firstLogLaunch = false
                                    log.info { "正在启动$GAME_CN_NAME" }
                                }
                                launchGameBySendMessage()
                            }
                            Thread.sleep(500)
                        }
                    } while (false)
                },
                100,
                500,
                TimeUnit.MILLISECONDS,
            ),
        )
    }

    private fun launchGameBySendMessage() {
        val platformHWND = GameUtil.findPlatformHWND()
        val rect = WinDef.RECT()
        SystemUtil.updateRECT(platformHWND, rect)
        MouseUtil.leftButtonClick(
            Point(145, rect.bottom - rect.top - 150),
            platformHWND,
            MouseControlModeEnum.MESSAGE.code,
        )
        SystemUtil.delayShort()
        MouseUtil.leftButtonClick(
            Point(145, rect.bottom - rect.top - 130),
            platformHWND,
            MouseControlModeEnum.MESSAGE.code,
        )
    }

    private fun next(gameHWND: HWND) {
        log.info { GAME_CN_NAME + "正在运行" }
        if (latestLogDir != null) {
            log.info { "等待${GAME_CN_NAME}创建最新日志文件夹" }
            while (!PauseStatus.isPause) {
                val currentLatestLogDir = GameUtil.getLatestLogDir()
                if (currentLatestLogDir != null) {
                    if (currentLatestLogDir > latestLogDir) {
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
        ScriptStatus.gameHWND = gameHWND
        ScriptStatus.platformHWND = GameUtil.findPlatformHWND()
        GameUtil.updateGameRect()
        go {
            Thread.sleep(3000)
            GameUtil.updateGameRect()
            ConfigUtil.getBoolean(ConfigEnum.UPDATE_GAME_WINDOW).isFalse {
                CSystemDll.INSTANCE.limitWindowResize(gameHWND, true)
            }
        }
    }
}
