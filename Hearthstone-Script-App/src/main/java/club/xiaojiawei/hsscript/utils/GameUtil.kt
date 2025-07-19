package club.xiaojiawei.hsscript.utils

import club.xiaojiawei.bean.LRunnable
import club.xiaojiawei.config.EXTRA_THREAD_POOL
import club.xiaojiawei.config.log
import club.xiaojiawei.enums.ModeEnum
import club.xiaojiawei.hsscript.bean.GameRect
import club.xiaojiawei.hsscript.bean.single.WarEx
import club.xiaojiawei.hsscript.consts.*
import club.xiaojiawei.hsscript.dll.CSystemDll
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.status.Mode
import club.xiaojiawei.hsscript.status.PauseStatus
import club.xiaojiawei.hsscript.status.ScriptStatus
import club.xiaojiawei.hsscript.utils.GameUtil.CHOOSE_ONE_RECTS
import club.xiaojiawei.hsscript.utils.SystemUtil.delay
import club.xiaojiawei.status.WAR
import club.xiaojiawei.util.isFalse
import club.xiaojiawei.util.randomSelect
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

    /**
     * 游戏进度已保存，请重启游戏的按钮
     */
    private val RESTART_GAME_RECT = GameRect(-0.0365, 0.0302, 0.0878, 0.1272)

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

    /**
     * 星舰发射
     */
    val STARSHIP_LAUNCH_RECT: GameRect = GameRect(0.0180, 0.1268, 0.2723, 0.4089)

    /**
     * 星舰取消发射
     */
    val STARSHIP_CANCEL_LAUNCH_RECT: GameRect = GameRect(-0.1107, -0.0295, 0.3106, 0.4120)

    /**
     * 牌库
     */
    val DECK_RECT = GameRect(0.4376, 0.4688, 0.0346, 0.1645)

    /**
     * 抉择
     */
    private val CHOOSE_ONE_RECTS = arrayOf(
        GameRect(-0.2030, -0.0364, -0.1775, 0.1677),
        GameRect(0.0412, 0.2030, -0.1732, 0.1656)
    )

    private val FOUR_DISCOVER_RECTS =
        arrayOf(
            GameRect(-0.3332, -0.1911, -0.1702, 0.1160),
            GameRect(-0.1570, -0.0149, -0.1702, 0.1160),
            GameRect(0.0182, 0.1603, -0.1702, 0.1160),
            GameRect(0.1934, 0.3355, -0.1702, 0.1160),
        )

    private val THREE_DISCOVER_RECTS =
        arrayOf(
            GameRect(-0.3037, -0.1595, -0.1702, 0.1160),
            GameRect(-0.0666, 0.0741, -0.1702, 0.1160),
            GameRect(0.1656, 0.3106, -0.1702, 0.1160),
        )

    private val MY_HAND_DECK_RECTS =
        arrayOf(
            arrayOf(
                GameRect(-0.0693, 0.0136, 0.3675, 0.5000),
            ),
            arrayOf(
                GameRect(-0.1149, -0.0316, 0.3675, 0.5000),
                GameRect(-0.0242, 0.0590, 0.3675, 0.5000),
            ),
            arrayOf(
                GameRect(-0.1599, -0.0767, 0.3675, 0.5000),
                GameRect(-0.0693, 0.0140, 0.3675, 0.5000),
                GameRect(0.0214, 0.1047, 0.3675, 0.5000),
            ),
            arrayOf(
                GameRect(-0.1930, -0.1307, 0.3855, 0.5000),
                GameRect(-0.1092, -0.0347, 0.3742, 0.5000),
                GameRect(-0.0208, 0.0507, 0.3814, 0.4995),
                GameRect(0.0744, 0.1425, 0.4158, 0.5000),
            ),
            arrayOf(
                GameRect(-0.2034, -0.1471, 0.4116, 0.5000),
                GameRect(-0.1338, -0.0704, 0.3888, 0.5000),
                GameRect(-0.0704, -0.0071, 0.3698, 0.5000),
                GameRect(0.0077, 0.0604, 0.3935, 0.5000),
                GameRect(0.0858, 0.1456, 0.4144, 0.5000),
            ),
            arrayOf(
                GameRect(-0.2115, -0.1672, 0.4144, 0.5000),
                GameRect(-0.1514, -0.1028, 0.3964, 0.5000),
                GameRect(-0.0975, -0.0448, 0.3755, 0.5000),
                GameRect(-0.0384, 0.0087, 0.3755, 0.5000),
                GameRect(0.0270, 0.0671, 0.3812, 0.4990),
                GameRect(0.0903, 0.1579, 0.4240, 0.5000),
            ),
            arrayOf(
                GameRect(-0.2179, -0.1799, 0.4192, 0.5000),
                GameRect(-0.1640, -0.1232, 0.4040, 0.5000),
                GameRect(-0.1155, -0.0690, 0.3869, 0.5000),
                GameRect(-0.0712, -0.0233, 0.3717, 0.5000),
                GameRect(-0.0152, 0.0235, 0.3755, 0.5000),
                GameRect(0.0418, 0.0727, 0.3821, 0.5000),
                GameRect(0.0956, 0.1617, 0.4211, 0.5000),
            ),
            arrayOf(
                GameRect(-0.2210, -0.1901, 0.4259, 0.5000),
                GameRect(-0.1746, -0.1394, 0.4125, 0.5000),
                GameRect(-0.1324, -0.0916, 0.3973, 0.5000),
                GameRect(-0.0912, -0.0490, 0.3745, 0.5000),
                GameRect(-0.0469, -0.0103, 0.3688, 0.5000),
                GameRect(0.0038, 0.0326, 0.3745, 0.5000),
                GameRect(0.0534, 0.0759, 0.4040, 0.5000),
                GameRect(0.1030, 0.1536, 0.4163, 0.4990),
            ),
            arrayOf(
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
            arrayOf(
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

    private val MY_PLAY_DECK_RECTS =
        arrayOf<Array<GameRect>>(
            //            偶数
            arrayOf(
                GameRect(-0.2689, -0.2111, -0.0033, 0.1050),
                GameRect(-0.1731, -0.1153, -0.0033, 0.1050),
                GameRect(-0.0773, -0.0195, -0.0033, 0.1050),
                GameRect(0.0195, 0.0773, -0.0033, 0.1050),
                GameRect(0.1153, 0.1731, -0.0033, 0.1050),
                GameRect(0.2111, 0.2689, -0.0033, 0.1050),
            ), //            奇数
            arrayOf(
                GameRect(-0.3156, -0.2578, -0.0041, 0.1043),
                GameRect(-0.2204, -0.1626, -0.0041, 0.1043),
                GameRect(-0.1257, -0.0691, -0.0041, 0.1043),
                GameRect(-0.0299, 0.0267, -0.0041, 0.1043),
                GameRect(0.0691, 0.1257, -0.0041, 0.1043),
                GameRect(0.1626, 0.2204, -0.0041, 0.1043),
                GameRect(0.2578, 0.3156, -0.0041, 0.1043),
            ),
        )

    private val RIVAL_PLAY_DECK_RECTS =
        arrayOf<Array<GameRect>>(
            //            偶数
            arrayOf(
                GameRect(-0.2689, -0.2111, -0.1730, -0.0716),
                GameRect(-0.1731, -0.1153, -0.1730, -0.0716),
                GameRect(-0.0773, -0.0195, -0.1730, -0.0716),
                GameRect(0.0195, 0.0773, -0.1730, -0.0716),
                GameRect(0.1153, 0.1731, -0.1730, -0.0716),
                GameRect(0.2111, 0.2689, -0.1730, -0.0716),
            ),
            //            奇数
            arrayOf(
                GameRect(-0.3156, -0.2578, -0.1730, -0.0716),
                GameRect(-0.2204, -0.1626, -0.1730, -0.0716),
                GameRect(-0.1257, -0.0691, -0.1730, -0.0716),
                GameRect(-0.0299, 0.0267, -0.1730, -0.0716),
                GameRect(0.0691, 0.1257, -0.1730, -0.0716),
                GameRect(0.1626, 0.2204, -0.1730, -0.0716),
                GameRect(0.2578, 0.3156, -0.1730, -0.0716),
            ),
        )

    private val DECK_POS_RECTS =
        arrayOf(
            GameRect(-0.4108, -0.2487, -0.2782, -0.2019),
            GameRect(-0.2368, -0.0833, -0.2782, -0.2019),
            GameRect(-0.0672, 0.0863, -0.2782, -0.2019),
            GameRect(-0.4034, -0.2498, -0.0699, 0.0065),
            GameRect(-0.2368, -0.0833, -0.0699, 0.0065),
            GameRect(-0.0672, 0.0863, -0.0699, 0.0065),
            GameRect(-0.4003, -0.2468, 0.1384, 0.2148),
            GameRect(-0.2337, -0.0802, 0.1384, 0.2148),
            GameRect(-0.0672, 0.0863, 0.1384, 0.2148),
        )

    private var gameEndTasks: MutableList<ScheduledFuture<*>> = mutableListOf()

    /**
     * 谢谢表情
     */
    fun sendThankEmoji() {
        log.info { "发送谢谢表情" }
        MY_HERO_RECT.rClick()
        SystemUtil.delayMedium()
        THANK_RECT.lClick(false)
        SystemUtil.delayShortMedium()
    }

    /**
     * 问候表情
     */
    fun sendGreetEmoji() {
        log.info { "发送问候表情" }
        MY_HERO_RECT.rClick()
        SystemUtil.delayMedium()
        GREET_RECT.lClick(false)
        SystemUtil.delayShortMedium()
    }

    /**
     * 失误表情
     */
    fun sendErrorEmoji() {
        log.info { "发送失误表情" }
        MY_HERO_RECT.rClick()
        SystemUtil.delayMedium()
        ERROR_RECT.lClick(false)
        SystemUtil.delayShortMedium()
    }

    /**
     * 获取抉择位置
     * @param index 范围：0-[CHOOSE_ONE_RECTS.size]
     */
    fun getChooseOneCardRect(index: Int): GameRect {
        return CHOOSE_ONE_RECTS.getOrElse(index) { GameRect.INVALID }
    }

    fun getThreeDiscoverCardRect(index: Int): GameRect {
        return THREE_DISCOVER_RECTS.getOrElse(index) { GameRect.INVALID }
    }

    fun getFourDiscoverCardRect(index: Int): GameRect {
        return FOUR_DISCOVER_RECTS.getOrElse(index) { GameRect.INVALID }
    }

    /**
     * 左击套牌位置
     */
    fun lClickDeckPos(count: Int = 1) {
        val chooseDeckPos = ConfigExUtil.getChooseDeckPos()
        if (chooseDeckPos.isEmpty()) return
        val deckPos = chooseDeckPos.randomSelect()
        DECK_POS_RECTS.getOrNull(deckPos - 1)?.let {
            for (i in 0 until count) {
                it.lClick()
                SystemUtil.delayTiny()
            }
        }
    }

    fun getMyHandCardRect(
        index: Int,
        size: Int,
    ): GameRect {
        if (index < 0 || index > size - 1 || size > MY_HAND_DECK_RECTS.size) {
            return GameRect.INVALID
        }
        return MY_HAND_DECK_RECTS[size - 1][index]
    }

    fun getMyPlayCardRect(
        index: Int,
        size: Int,
    ): GameRect = getPlayCardRect(index, size, MY_PLAY_DECK_RECTS)

    fun getRivalPlayCardRect(
        index: Int,
        size: Int,
    ): GameRect = getPlayCardRect(index, size, RIVAL_PLAY_DECK_RECTS)

    private fun getPlayCardRect(
        index: Int,
        size: Int,
        gameRects: Array<Array<GameRect>>,
    ): GameRect {
        var i = index
        val s = max(size, 0)
        val rects: Array<GameRect> = gameRects[s and 1]
        val offset: Int = (rects.size - s) shr 1
        i = max((offset + i), 0)
        i = min(i, (rects.size - 1))
        return rects[i]
    }

    /**
     * 选择哪张发现牌
     */
    fun chooseDiscoverCard(
        index: Int,
        discoverCardSize: Int,
    ) {
        if (discoverCardSize >= 4) {
            getFourDiscoverCardRect(Math.clamp(index.toLong(), 0, 3)).lClick()
        } else {
            getThreeDiscoverCardRect(Math.clamp(index.toLong(), 0, 2)).lClick()
        }
    }

    fun leftButtonClick(point: Point) {
        MouseUtil.leftButtonClick(point, ScriptStatus.gameHWND)
    }

    fun rightButtonClick(point: Point) {
        MouseUtil.rightButtonClick(point, ScriptStatus.gameHWND)
    }

    fun moveMouse(
        startPos: Point?,
        endPos: Point,
    ) {
        MouseUtil.moveMouseByHuman(startPos, endPos, ScriptStatus.gameHWND)
    }

    fun moveMouse(endPos: Point) {
        MouseUtil.moveMouseByHuman(endPos, ScriptStatus.gameHWND)
    }

    /**
     * 如果战网不在运行则相当于启动战网，如果战网已经运行则为启动炉石
     */
    @Suppress("DEPRECATION")
    fun launchPlatformAndGame() {
        try {
//            CMDUtil.directExec(
//                arrayOf(
//                    ConfigUtil.getString(ConfigEnum.PLATFORM_PATH),
//                    """--exec="launch WTCG""""
//                )
//            )
            Runtime.getRuntime().exec(""""${ConfigUtil.getString(ConfigEnum.PLATFORM_PATH)}" --exec="launch WTCG"""")
        } catch (e: IOException) {
            log.error(e) { "启动${PLATFORM_CN_NAME}及${GAME_CN_NAME}异常" }
        }
    }

    @Suppress("DEPRECATION")
    fun launchPlatform() {
        try {
            Runtime.getRuntime().exec("\"${ConfigUtil.getString(ConfigEnum.PLATFORM_PATH)}\"")
        } catch (e: IOException) {
            log.error(e) { "启动${PLATFORM_CN_NAME}异常" }
        }
    }

    fun triggerCalcMyDeadLine() {
        WAR.me.playArea.hero?.let { myHero ->
            val rivalAllDamage = (WAR.rival.playArea.cards.sumOf {
                if (it.canAttack()) {
                    it.atc * (if (it.isMegaWindfury) 4 else if (it.isWindFury) 2 else 1)
                } else 0
            }) + (WAR.rival.playArea.hero?.let { rivalHero ->
                if (rivalHero.canAttack()) {
                    rivalHero.atc * (if (rivalHero.isMegaWindfury) 4 else if (rivalHero.isWindFury) 2 else 1)
                } else 0
            } ?: 0)
            val myTauntBlood = WAR.me.playArea.cards.sumOf { card -> if (card.isTaunt) card.blood() else 0 }
            val myHeroBlood = myHero.blood()
            if (rivalAllDamage - myHeroBlood - myTauntBlood >= 0) {
                log.info { "敌方已能斩杀我方，敌方伤害:${rivalAllDamage}，我方血量:${myHeroBlood}，我方嘲讽随从血量:${myTauntBlood}" }
                GameUtil.surrender()
            }
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
                        SystemUtil.delayTiny()
                        lClickSettings()
                        SystemUtil.delayShortMedium()
                        SURRENDER_RECT.lClick()
                        SystemUtil.delayTiny()
                        RESTART_GAME_RECT.lClick()
                    }
                },
                0,
                500,
                TimeUnit.MILLISECONDS,
            ),
        )
    }

    /**
     * 点击回合结束按钮
     */
    fun lClickTurnOver(isCancel: Boolean = true) {
        END_TURN_RECT.lClick(isCancel)
    }

    /**
     * 点击设置按钮
     */
    fun lClickSettings() {
        val width = ScriptStatus.GAME_RECT.right - ScriptStatus.GAME_RECT.left
        val height = ScriptStatus.GAME_RECT.bottom - ScriptStatus.GAME_RECT.top
        leftButtonClick(Point((width - width * 0.0072992700729927).toInt(), (height - height * 0.015625).toInt()))
    }

    fun cancelAction() {
        MouseUtil.rightButtonClick(ScriptStatus.gameHWND)
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
                    TimeUnit.MILLISECONDS,
                ),
            )
        } else {
            (0 until 3).forEach { _ ->
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

    fun isAliveOfGame(): Boolean = CSystemDll.INSTANCE.isProcessRunning(GAME_PROGRAM_NAME)

    fun isAliveOfPlatform(): Boolean = CSystemDll.INSTANCE.isProcessRunning(PLATFORM_PROGRAM_NAME)

    fun findGameHWND(): WinDef.HWND? {
        val hwnd =
            (
                    SystemUtil.findHWND("UnityWndClass", GAME_CN_NAME)
                        ?: SystemUtil.findHWND("UnityWndClass", GAME_US_NAME)
                        ?: CSystemDll.INSTANCE.findWindowsByProcessName(GAME_PROGRAM_NAME)
                    )
                ?: SystemUtil.findHWND(null, GAME_CN_NAME)
                ?: SystemUtil.findHWND(null, GAME_US_NAME)
        return hwnd
    }

    fun findPlatformHWND(): WinDef.HWND? =
        SystemUtil.findHWND("Chrome_WidgetWin_0", PLATFORM_CN_NAME)
            ?: let { SystemUtil.findHWND("Chrome_WidgetWin_0", PLATFORM_US_NAME) }

    fun findLoginPlatformHWND(): WinDef.HWND? = SystemUtil.findHWND("Qt5151QWindowIcon", PLATFORM_LOGIN_CN_NAME)

    /**
     * 更新游戏窗口信息
     */
    fun updateGameRect(gameHWND: WinDef.HWND? = ScriptStatus.gameHWND) {
        SystemUtil.updateRECT(gameHWND, ScriptStatus.GAME_RECT)
//        println("left:${ScriptStatus.GAME_RECT.left}, right:${ScriptStatus.GAME_RECT.right}, top:${ScriptStatus.GAME_RECT.top}, bottom:${ScriptStatus.GAME_RECT.bottom}")
    }

    /**
     * 通过此方式停止的游戏，screen.log监听器可能无法监测到游戏被关闭
     */
    fun killGame(sync: Boolean = false) {
//        todo del
        println("killGame")
        val exec = {
            if (isAliveOfGame()) {
                kotlin
                    .runCatching {
                        for (i in 0 until 2) {
                            CSystemDll.INSTANCE.quitWindow(ScriptStatus.gameHWND)
                            delay(2000)
                            if (!isAliveOfGame()) return@runCatching
                        }
                        for (i in 0 until 2) {
                            CSystemDll.INSTANCE.killProcessByName(GAME_PROGRAM_NAME)
                            delay(2000)
                            if (!isAliveOfGame()) return@runCatching
                        }
                    }.onSuccess {
                        if (isAliveOfGame()) {
                            log.error { "${GAME_CN_NAME}关闭失败" }
                        } else {
                            log.info { "${GAME_CN_NAME}已关闭" }
                        }
                    }.onFailure {
                        log.error(it) { "关闭${GAME_CN_NAME}异常" }
                    }
            } else {
                log.info { "${GAME_CN_NAME}不在运行" }
            }
        }
        if (sync) {
            exec()
        } else {
            exec.goWithResult()
        }
    }

    fun killPlatform() {
        val platformHWND: WinDef.HWND? = findPlatformHWND()
        val loginPlatformHWND: WinDef.HWND? = findLoginPlatformHWND()
        if (platformHWND != null || loginPlatformHWND != null) {
            CSystemDll.INSTANCE.quitWindow(platformHWND)
            CSystemDll.INSTANCE.quitWindow(loginPlatformHWND)
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
            CSystemDll.INSTANCE.quitWindow(loginPlatformHWND)
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

    /**
     * 获取游戏最新日志目录
     */
    fun getLatestLogDir(): File? {
        val files: Array<File> = getAllLogDir()
        Arrays.sort(files, Comparator.comparing { obj: File -> obj.name })
        return files.lastOrNull()
    }

    /**
     * 获取游戏所有日志目录
     */
    fun getAllLogDir(): Array<File> {
        val gameLogDir = Path.of(ConfigUtil.getString(ConfigEnum.GAME_PATH), GAME_LOG_DIR).toFile()
        return if (gameLogDir.exists() && gameLogDir.isDirectory) gameLogDir.listFiles() ?: arrayOf() else arrayOf()
    }

}
