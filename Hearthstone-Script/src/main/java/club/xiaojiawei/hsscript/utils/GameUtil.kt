package club.xiaojiawei.hsscript.utils

import club.xiaojiawei.bean.LRunnable
import club.xiaojiawei.config.EXTRA_THREAD_POOL
import club.xiaojiawei.config.log
import club.xiaojiawei.enums.ModeEnum
import club.xiaojiawei.hsscript.bean.GameRect
import club.xiaojiawei.hsscript.bean.single.WarEx
import club.xiaojiawei.hsscript.data.*
import club.xiaojiawei.hsscript.dll.SystemDll
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.status.Mode
import club.xiaojiawei.hsscript.status.PauseStatus
import club.xiaojiawei.hsscript.utils.SystemUtil.delay
import club.xiaojiawei.util.isFalse
import com.sun.jna.WString
import com.sun.jna.platform.win32.Kernel32
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinUser
import java.awt.Point
import java.io.File
import java.io.IOException
import java.nio.file.Path
import java.util.*
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.min

/**
 * 游戏工具类
 * @author 肖嘉威
 * @date 2022/11/27 1:42
 */
object GameUtil {

    private val GAME_CLASS_NAME_W = WString("UnityWndClass")

    val CENTER_RECT: GameRect = GameRect(-0.1, 0.1, 0.1, -0.1)

    val RIGHT_CENTER_RECT: GameRect = GameRect(0.4, 0.5, 0.1, -0.1)

    val CONFIRM_RECT: GameRect = GameRect(-0.0546, 0.0601, 0.2709, 0.3222)

    val END_TURN_RECT: GameRect = GameRect(0.3550, 0.4533, -0.0636, -0.0196)

    val RECONNECT_RECT: GameRect = GameRect(-0.1845, -0.0396, 0.2282, 0.2904)

    val CANCEL_CONNECT_RECT: GameRect = GameRect(0.0266, 0.1714, 0.2282, 0.2904)

    val SURRENDER_RECT: GameRect = GameRect(-0.0629, 0.0607, -0.1677, -0.1279)

    //    表情
    val THANK_RECT: GameRect = GameRect(-0.1604, -0.0404, 0.1153, 0.1502)
    val PRAISE_RECT: GameRect = GameRect(-0.1930, -0.0730, 0.1971, 0.2320)
    val GREET_RECT: GameRect = GameRect(-0.1907, -0.0707, 0.2799, 0.3148)
    val THREATEN_RECT: GameRect = GameRect(0.0754, 0.1954, 0.2830, 0.3180)
    val ERROR_RECT: GameRect = GameRect(0.0786, 0.1986, 0.1981, 0.2331)
    val WONDER_RECT: GameRect = GameRect(0.0444, 0.1644, 0.1174, 0.1523)

    val RIVAL_HERO_RECT: GameRect = GameRect(-0.0453, 0.0488, -0.3620, -0.2355)
    val MY_HERO_RECT: GameRect = GameRect(-0.0453, 0.0488, 0.2229, 0.3494)

    val RIVAL_POWER_RECT: GameRect = GameRect(0.0840, 0.1554, -0.3260, -0.2338)
    val MY_POWER_RECT: GameRect = GameRect(0.0855, 0.1569, 0.2254, 0.3176)

    private val FOUR_DISCOVER_RECTS = arrayOf<GameRect>(
        GameRect(-0.3332, -0.1911, -0.1702, 0.1160),
        GameRect(-0.1570, -0.0149, -0.1702, 0.1160),
        GameRect(0.0182, 0.1603, -0.1702, 0.1160),
        GameRect(0.1934, 0.3355, -0.1702, 0.1160),
    )

    private val THREE_DISCOVER_RECTS = arrayOf<GameRect>(
        GameRect(-0.3037, -0.1595, -0.1702, 0.1160),
        GameRect(-0.0666, 0.0741, -0.1702, 0.1160),
        GameRect(0.1656, 0.3106, -0.1702, 0.1160),
    )

    private val MY_HAND_DECK_RECTS = arrayOf<Array<GameRect>>(
        arrayOf<GameRect>(
            GameRect(-0.0693, 0.0136, 0.3675, 0.5000),
        ),
        arrayOf<GameRect>(
            GameRect(-0.1149, -0.0316, 0.3675, 0.5000),
            GameRect(-0.0242, 0.0590, 0.3675, 0.5000),
        ),
        arrayOf<GameRect>(
            GameRect(-0.1599, -0.0767, 0.3675, 0.5000),
            GameRect(-0.0693, 0.0140, 0.3675, 0.5000),
            GameRect(0.0214, 0.1047, 0.3675, 0.5000),
        ),
        arrayOf<GameRect>(
            GameRect(-0.1930, -0.1307, 0.3855, 0.5000),
            GameRect(-0.1092, -0.0347, 0.3742, 0.5000),
            GameRect(-0.0208, 0.0507, 0.3814, 0.4995),
            GameRect(0.0744, 0.1425, 0.4158, 0.5000),
        ),
        arrayOf<GameRect>(
            GameRect(-0.2034, -0.1471, 0.4116, 0.5000),
            GameRect(-0.1338, -0.0704, 0.3888, 0.5000),
            GameRect(-0.0704, -0.0071, 0.3698, 0.5000),
            GameRect(0.0077, 0.0604, 0.3935, 0.5000),
            GameRect(0.0858, 0.1456, 0.4144, 0.5000),
        ),
        arrayOf<GameRect>(
            GameRect(-0.2115, -0.1672, 0.4144, 0.5000),
            GameRect(-0.1514, -0.1028, 0.3964, 0.5000),
            GameRect(-0.0975, -0.0448, 0.3755, 0.5000),
            GameRect(-0.0384, 0.0087, 0.3755, 0.5000),
            GameRect(0.0270, 0.0671, 0.3812, 0.4990),
            GameRect(0.0903, 0.1579, 0.4240, 0.5000),
        ),
        arrayOf<GameRect>(
            GameRect(-0.2179, -0.1799, 0.4192, 0.5000),
            GameRect(-0.1640, -0.1232, 0.4040, 0.5000),
            GameRect(-0.1155, -0.0690, 0.3869, 0.5000),
            GameRect(-0.0712, -0.0233, 0.3717, 0.5000),
            GameRect(-0.0152, 0.0235, 0.3755, 0.5000),
            GameRect(0.0418, 0.0727, 0.3821, 0.5000),
            GameRect(0.0956, 0.1617, 0.4211, 0.5000),
        ),
        arrayOf<GameRect>(
            GameRect(-0.2210, -0.1901, 0.4259, 0.5000),
            GameRect(-0.1746, -0.1394, 0.4125, 0.5000),
            GameRect(-0.1324, -0.0916, 0.3973, 0.5000),
            GameRect(-0.0912, -0.0490, 0.3745, 0.5000),
            GameRect(-0.0469, -0.0103, 0.3688, 0.5000),
            GameRect(0.0038, 0.0326, 0.3745, 0.5000),
            GameRect(0.0534, 0.0759, 0.4040, 0.5000),
            GameRect(0.1030, 0.1536, 0.4163, 0.4990),
        ),
        arrayOf<GameRect>(
            GameRect(-0.2274, -0.1964, 0.4335, 0.5000),
            GameRect(-0.1820, -0.1496, 0.4335, 0.5000),
            GameRect(-0.1429, -0.1099, 0.4059, 0.5000),
            GameRect(-0.1060, -0.0687, 0.3888, 0.5000),
            GameRect(-0.0712, -0.0346, 0.3698, 0.5000),
            GameRect(-0.0268, 0.0034, 0.3745, 0.5000),
            GameRect(0.0186, 0.0502, 0.3764, 0.4563),
            GameRect(0.0639, 0.0942, 0.3878, 0.4610),
            GameRect(0.1083, 0.1653, 0.4125, 0.5000),
        ),
        arrayOf<GameRect>(
            GameRect(-0.2305, -0.2024, 0.4401, 0.5000),
            GameRect(-0.1894, -0.1598, 0.4401, 0.5000),
            GameRect(-0.1524, -0.1250, 0.4097, 0.5000),
            GameRect(-0.1176, -0.0859, 0.3964, 0.5000),
            GameRect(-0.0859, -0.0522, 0.3726, 0.5000),
            GameRect(-0.0511, -0.0208, 0.3726, 0.5000),
            GameRect(-0.0089, 0.0207, 0.3740, 0.4501),
            GameRect(0.0302, 0.0583, 0.3783, 0.4515),
            GameRect(0.0692, 0.0974, 0.3926, 0.4610),
            GameRect(0.1093, 0.1677, 0.4163, 0.5000),
        ),
    )

    private val MY_PLAY_DECK_RECTS = arrayOf<Array<GameRect>>(
        //            偶数
        arrayOf<GameRect>(
            GameRect(-0.2689, -0.2111, -0.0033, 0.1050),
            GameRect(-0.1731, -0.1153, -0.0033, 0.1050),
            GameRect(-0.0773, -0.0195, -0.0033, 0.1050),
            GameRect(0.0195, 0.0773, -0.0033, 0.1050),
            GameRect(0.1153, 0.1731, -0.0033, 0.1050),
            GameRect(0.2111, 0.2689, -0.0033, 0.1050),
        ),  //            奇数
        arrayOf<GameRect>(
            GameRect(-0.3156, -0.2578, -0.0041, 0.1043),
            GameRect(-0.2204, -0.1626, -0.0041, 0.1043),
            GameRect(-0.1257, -0.0691, -0.0041, 0.1043),
            GameRect(-0.0299, 0.0267, -0.0041, 0.1043),
            GameRect(0.0691, 0.1257, -0.0041, 0.1043),
            GameRect(0.1626, 0.2204, -0.0041, 0.1043),
            GameRect(0.2578, 0.3156, -0.0041, 0.1043),
        ),
    )

    private val RIVAL_PLAY_DECK_RECTS = arrayOf<Array<GameRect>>(
        //            偶数
        arrayOf<GameRect>(
            GameRect(-0.2689, -0.2111, -0.1730, -0.0716),
            GameRect(-0.1731, -0.1153, -0.1730, -0.0716),
            GameRect(-0.0773, -0.0195, -0.1730, -0.0716),
            GameRect(0.0195, 0.0773, -0.1730, -0.0716),
            GameRect(0.1153, 0.1731, -0.1730, -0.0716),
            GameRect(0.2111, 0.2689, -0.1730, -0.0716),
        ),
        //            奇数
        arrayOf<GameRect>(
            GameRect(-0.3156, -0.2578, -0.1730, -0.0716),
            GameRect(-0.2204, -0.1626, -0.1730, -0.0716),
            GameRect(-0.1257, -0.0691, -0.1730, -0.0716),
            GameRect(-0.0299, 0.0267, -0.1730, -0.0716),
            GameRect(0.0691, 0.1257, -0.1730, -0.0716),
            GameRect(0.1626, 0.2204, -0.1730, -0.0716),
            GameRect(0.2578, 0.3156, -0.1730, -0.0716),
        ),
    )

    private var gameEndTasks: MutableList<ScheduledFuture<*>> = mutableListOf()

    fun getThreeDiscoverCardRect(index: Int): GameRect {
        if (index < 0 || index > THREE_DISCOVER_RECTS.size - 1) {
            return GameRect.INVALID
        }
        return THREE_DISCOVER_RECTS[index]
    }

    fun getFourDiscoverCardRect(index: Int): GameRect {
        if (index < 0 || index > FOUR_DISCOVER_RECTS.size - 1) {
            return GameRect.INVALID
        }
        return FOUR_DISCOVER_RECTS[index]
    }

    fun getMyHandCardRect(index: Int, size: Int): GameRect {
        if (index < 0 || index > size - 1 || size > MY_HAND_DECK_RECTS.size) {
            return GameRect.INVALID
        }
        return MY_HAND_DECK_RECTS[size - 1][index]
    }

    fun getMyPlayCardRect(index: Int, size: Int): GameRect {
        return getPlayCardRect(index, size, MY_PLAY_DECK_RECTS)
    }

    fun getRivalPlayCardRect(index: Int, size: Int): GameRect {
        return getPlayCardRect(index, size, RIVAL_PLAY_DECK_RECTS)
    }

    private fun getPlayCardRect(index: Int, size: Int, gameRects: Array<Array<GameRect>>): GameRect {
        var i = index
        var s = size
        s = max(s, 0)
        val rects: Array<GameRect> = gameRects[s and 1]
        val offset: Int = (rects.size - s) shr 1
        i = max((offset + i), 0)
        i = min(i, (rects.size - 1))
        return rects[i]
    }

    fun clickDiscover(index: Int, discoverSize: Int) {
        if (discoverSize == 3) {
            getThreeDiscoverCardRect(index).lClick()
        } else {
            getFourDiscoverCardRect(index).lClick()
        }
    }

    fun leftButtonClick(point: Point) {
        MouseUtil.leftButtonClick(point, GAME_HWND)
    }

    fun rightButtonClick(point: Point) {
        MouseUtil.leftButtonClick(point, GAME_HWND)
    }

    fun moveMouse(startPos: Point?, endPos: Point) {
        MouseUtil.moveMouseByHuman(startPos, endPos, GAME_HWND)
    }

    fun moveMouse(endPos: Point) {
        MouseUtil.moveMouseByHuman(endPos, GAME_HWND)
    }


    /**
     * 如果战网不在运行则相当于启动战网，如果战网已经运行则为启动炉石
     */
    fun launchPlatformAndGame() {
        try {
            Runtime.getRuntime().exec("\"${ConfigUtil.getString(ConfigEnum.PLATFORM_PATH)}\" --exec=\"launch WTCG\"")
        } catch (e: IOException) {
            log.error(e) { "启动${PLATFORM_CN_NAME}及${GAME_CN_NAME}异常" }
        }
    }

    fun launchPlatform() {
        try {
            Runtime.getRuntime().exec("\"${ConfigUtil.getString(ConfigEnum.PLATFORM_PATH)}\"")
        } catch (e: IOException) {
            log.error(e) { "启动${PLATFORM_CN_NAME}异常" }
        }
    }

    /**
     * 游戏里投降
     */
    fun surrender() {
//        SystemUtil.frontWindow(ScriptStaticData.getGameHWND());
//        按ESC键弹出投降界面
//        ScriptStaticData.ROBOT.keyPress(27);
//        ScriptStaticData.ROBOT.keyRelease(27);
        if (gameEndTasks.isNotEmpty()) return
        log.info { "触发投降" }
        val warCount = WarEx.warCount
        delay(1000)
        val isGamePlay = Mode.currMode === ModeEnum.GAMEPLAY
        gameEndTasks.add(
            EXTRA_THREAD_POOL.scheduleWithFixedDelay(
                LRunnable {
                    if (PauseStatus.isPause) {
                        cancelGameEndTask()
                    } else if (WarEx.warCount > warCount || (isGamePlay && Mode.currMode !== ModeEnum.GAMEPLAY)) {
                        cancelGameEndTask()
                    } else {
                        END_TURN_RECT.lClick()
                        delay(100)
                        lClickSettings()
                        delay(500)
                        SURRENDER_RECT.lClick()
                    }
                },
                0,
                500,
                TimeUnit.MILLISECONDS
            )
        )
    }

    /**
     * 点击设置按钮
     */
    fun lClickSettings() {
        val width = GAME_RECT.right - GAME_RECT.left
        val height = GAME_RECT.bottom - GAME_RECT.top
        leftButtonClick(Point((width - width * 0.0072992700729927).toInt(), (height - height * 0.015625).toInt()))
    }

    fun cancelAction() {
        MouseUtil.rightButtonClick(GAME_HWND)
    }

    fun lClickCenter() {
        CENTER_RECT.lClick()
    }

    fun lClickRightCenter() {
        RIGHT_CENTER_RECT.lClick()
    }

    fun rClickCenter() {
        CENTER_RECT.rClick()
    }

    fun reconnect() {
        RECONNECT_RECT.lClick()
    }

    /**
     * 点掉游戏结束结算页面
     */
    fun addGameEndTask() {
        cancelGameEndTask()
        log.info { "点掉${GAME_CN_NAME}结束结算页面" }
        if (Mode.currMode === ModeEnum.GAMEPLAY) {
            gameEndTasks.add(
                EXTRA_THREAD_POOL.scheduleWithFixedDelay(
                    LRunnable {
                        if (PauseStatus.isPause) {
                            cancelGameEndTask()
                        } else if (Mode.currMode !== ModeEnum.GAMEPLAY) {
                            cancelGameEndTask()
                        } else {
                            END_TURN_RECT.lClick()
                        }
                    },
                    1000,
                    1000,
                    TimeUnit.MILLISECONDS
                )
            )
        } else {
            (0 until 3).forEach {
                END_TURN_RECT.lClick()
                SystemUtil.delayShort()
            }
        }
    }

    fun hidePlatformWindow() {
        val platformHWND = findPlatformHWND()
        if (platformHWND != null && !User32.INSTANCE.ShowWindow(platformHWND, WinUser.SW_MINIMIZE)) {
            log.warn { "最小化${PLATFORM_CN_NAME}窗口异常，错误代码：" + Kernel32.INSTANCE.GetLastError() }
        }
    }

    fun isAliveOfGame(): Boolean {
        return SystemUtil.isAliveOfProgram(GAME_PROGRAM_NAME)
    }

    fun isAliveOfPlatform(): Boolean {
        return SystemUtil.isAliveOfProgram(PLATFORM_PROGRAM_NAME)
    }

    fun findGameHWND(): WinDef.HWND? {
        return SystemUtil.findHWND("UnityWndClass", GAME_CN_NAME)
            ?: SystemUtil.findHWND("UnityWndClass", GAME_US_NAME)
            ?: SystemDll.INSTANCE.FindWindowsByProcessName(GAME_PROGRAM_NAME)
    }

    fun findPlatformHWND(): WinDef.HWND? {
        return SystemUtil.findHWND("Chrome_WidgetWin_0", PLATFORM_CN_NAME)
            ?: let { SystemUtil.findHWND("Chrome_WidgetWin_0", PLATFORM_US_NAME) }
    }

    fun findLoginPlatformHWND(): WinDef.HWND? {
        return SystemUtil.findHWND("Qt5151QWindowIcon", PLATFORM_LOGIN_CN_NAME)
    }

    /**
     * 更新游戏窗口信息
     */
    fun updateGameRect(updateHWNDCache: Boolean = false) {
        if (GAME_HWND == null || updateHWNDCache) {
            GAME_HWND = findGameHWND()
        }
        SystemUtil.updateRECT(GAME_HWND, GAME_RECT)
    }

    /**
     * 通过此方式停止的游戏，screen.log监听器可能无法监测到游戏被关闭
     */
    fun killGame() {
        if (findGameHWND() != null) {
            try {
                Runtime.getRuntime().exec("cmd /c taskkill /f /t /im $GAME_PROGRAM_NAME")
                    .waitFor()
                delay(1000)
                log.info { "${GAME_CN_NAME}已关闭" }
            } catch (e: IOException) {
                log.error(e) { "关闭${GAME_CN_NAME}异常" }
            } catch (e: InterruptedException) {
                log.warn(e) { "关闭${GAME_CN_NAME}可能异常" }
            }
        } else {
            log.info { "${GAME_CN_NAME}不在运行" }
        }
    }

    fun killPlatform() {
        val platformHWND: WinDef.HWND? = findPlatformHWND()
        val loginPlatformHWND: WinDef.HWND? = findLoginPlatformHWND()
        if (platformHWND != null || loginPlatformHWND != null) {
            SystemDll.INSTANCE.closeProgram(platformHWND)
            SystemDll.INSTANCE.closeProgram(loginPlatformHWND)
            log.info { "${PLATFORM_CN_NAME}已关闭" }
        } else {
            log.info { "${PLATFORM_CN_NAME}不在运行" }
        }
    }

    fun killLoginPlatform() {
        val loginPlatformHWND: WinDef.HWND? = findLoginPlatformHWND()
        if (loginPlatformHWND == null) {
            log.info { "${PLATFORM_LOGIN_CN_NAME}不在运行" }
        } else {
            SystemDll.INSTANCE.closeProgram(loginPlatformHWND)
            log.info { "${PLATFORM_LOGIN_CN_NAME}已关闭" }
        }
    }

    private fun cancelGameEndTask() {
        for (future in gameEndTasks.toList()) {
            future.isDone.isFalse {
                future.cancel(true)
            }
            gameEndTasks.remove(future)
        }
    }

    fun getLatestLogDir(): File? {
        val gameLogDir = Path.of(ConfigUtil.getString(ConfigEnum.GAME_PATH), "Logs").toFile()

        var files: Array<File>? = null
        if (gameLogDir.exists() && gameLogDir.isDirectory) {
            files = gameLogDir.listFiles()
            Arrays.sort(files, Comparator.comparing { obj: File -> obj.name })
            if (files.isNotEmpty()) return files[files.size - 1]
        }
        return null
    }

}
