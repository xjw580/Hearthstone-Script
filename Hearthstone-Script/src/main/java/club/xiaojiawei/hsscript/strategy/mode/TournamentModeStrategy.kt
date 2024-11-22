package club.xiaojiawei.hsscript.strategy.mode

import club.xiaojiawei.DeckStrategy
import club.xiaojiawei.bean.LRunnable
import club.xiaojiawei.config.EXTRA_THREAD_POOL
import club.xiaojiawei.config.log
import club.xiaojiawei.enums.ModeEnum
import club.xiaojiawei.enums.RunModeEnum
import club.xiaojiawei.hsscript.bean.Deck
import club.xiaojiawei.hsscript.bean.GameRect
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.listener.WorkListener
import club.xiaojiawei.hsscript.listener.log.DeckLogListener.DECKS
import club.xiaojiawei.hsscript.listener.log.PowerLogListener
import club.xiaojiawei.hsscript.status.DeckStrategyManager
import club.xiaojiawei.hsscript.status.Mode
import club.xiaojiawei.hsscript.status.PauseStatus
import club.xiaojiawei.hsscript.strategy.AbstractModeStrategy
import club.xiaojiawei.hsscript.utils.ConfigUtil
import club.xiaojiawei.hsscript.utils.GameUtil.reconnect
import club.xiaojiawei.hsscript.utils.SystemUtil
import java.util.concurrent.TimeUnit

/**
 * 传统对战
 * @author 肖嘉威
 * @date 2022/11/25 12:39
 */
object TournamentModeStrategy : AbstractModeStrategy<Any?>() {

    val START_RECT: GameRect = GameRect(0.2586, 0.3459, 0.2706, 0.3794)

    val ERROR_RECT: GameRect = GameRect(-0.0397, 0.0325, 0.0856, 0.1249)


    val CHANGE_MODE_RECT: GameRect = GameRect(0.2868, 0.3256, -0.4672, -0.4279)

    val STANDARD_MODE_RECT: GameRect = GameRect(-0.2012, -0.0295, -0.2156, -0.0400)

    val WILD_MODE_RECT: GameRect = GameRect(0.0295, 0.2012, -0.2156, -0.0400)

    val CASUAL_MODE_RECT: GameRect = GameRect(0.2557, 0.4278, -0.1769, 0.0014)

    val CLASSIC_MODE_RECT: GameRect = GameRect(-0.4278, -0.2557, -0.1769, 0.0014)

    val FIRST_DECK_RECT: GameRect = GameRect(-0.4108, -0.2487, -0.2811, -0.1894)

    val TOURNAMENT_MODE_RECT: GameRect = GameRect(-0.0790, 0.0811, -0.2090, -0.1737)

    /**
     * 顶栏有限时借用套牌时使用
     */
    val FIRST_DECK_RECT_LIMIT: GameRect = GameRect(-0.4072, -0.2516, -0.0696, 0.0139)

    val PREV_DECK_PAGE: GameRect = GameRect(-0.4755, -0.4473, -0.0302, 0.0095)

    val BACK_RECT: GameRect = GameRect(0.4041, 0.4575, 0.4083, 0.4410)

    val CANCEL_RECT: GameRect = GameRect(-0.0251, 0.0530, 0.3203, 0.3802)

    override fun wantEnter() {
        addWantEnterTask(EXTRA_THREAD_POOL.scheduleWithFixedDelay(LRunnable {
            if (PauseStatus.isPause) {
                cancelAllWantEnterTasks()
            } else if (Mode.currMode == ModeEnum.HUB) {
                TOURNAMENT_MODE_RECT.lClick()
            } else if (Mode.currMode == ModeEnum.GAME_MODE) {
                cancelAllWantEnterTasks()
                BACK_RECT.lClick()
            } else {
                cancelAllWantEnterTasks()
            }
        }, DELAY_TIME.toLong(), INTERVAL_TIME.toLong(), TimeUnit.MILLISECONDS))
    }

    override fun afterEnter(t: Any?) {
        if (WorkListener.isDuringWorkDate()) {
            val deckStrategy = DeckStrategyManager.currentDeckStrategy
            if (deckStrategy == null) {
                SystemUtil.notice("未配置卡组策略")
                log.warn { "未配置卡组策略" }
                PauseStatus.isPause = true
                return
            }
            var runModeEnum: RunModeEnum
            if (((deckStrategy.runModes[0].also {
                    runModeEnum = it
                }) == RunModeEnum.CASUAL || runModeEnum === RunModeEnum.CLASSIC || runModeEnum === RunModeEnum.WILD || runModeEnum === RunModeEnum.STANDARD) && runModeEnum.isEnable) {
                if (!PowerLogListener.checkPowerLogSize()) {
                    return
                }
                SystemUtil.delayShort()
                clickModeChangeButton()
                SystemUtil.delayShort()
                changeMode(runModeEnum)
                SystemUtil.delayShort()
                selectDeck(deckStrategy)
                SystemUtil.delayShort()
                startMatching()
            } else {
                addEnteredTask(EXTRA_THREAD_POOL.scheduleWithFixedDelay(LRunnable {
                    if (PauseStatus.isPause) {
                        cancelAllEnteredTasks()
                    } else if (Mode.currMode === ModeEnum.TOURNAMENT) {
                        BACK_RECT.lClick()
                    } else {
                        cancelAllEnteredTasks()
                    }
                }, 0, 200, TimeUnit.MILLISECONDS))
            }
        } else {
            WorkListener.stopWork()
        }
    }

    private fun clickModeChangeButton() {
        log.info { "点击切换模式按钮" }
        CHANGE_MODE_RECT.lClick()
    }

    private fun changeMode(runModeEnum: RunModeEnum) {
        when (runModeEnum) {
            RunModeEnum.CLASSIC -> changeModeToClassic()
            RunModeEnum.STANDARD -> changeModeToStandard()
            RunModeEnum.WILD -> changeModeToWild()
            RunModeEnum.CASUAL -> changeModeToCasual()
            else -> throw RuntimeException("不支持此模式：" + runModeEnum.comment)
        }
    }

    fun selectDeck(deckStrategy: DeckStrategy) {
        val decks: List<Deck> = DECKS
        for (i in decks.indices.reversed()) {
            val d = decks[i]
            if (d.code == deckStrategy.deckCode() || d.name == deckStrategy.name()) {
                log.debug { "找到套牌:" + deckStrategy.name() }
                break
            }
        }
        log.info { "选择套牌" }

        PREV_DECK_PAGE.lClick()
        SystemUtil.delayTiny()
        FIRST_DECK_RECT_LIMIT.lClick()
        SystemUtil.delayTiny()
        FIRST_DECK_RECT.lClick()
        SystemUtil.delayTiny()
        FIRST_DECK_RECT.lClick()
    }

    private fun changeModeToClassic() {
        log.info { "切换至经典模式" }
        CLASSIC_MODE_RECT.lClick()
    }

    private fun changeModeToStandard() {
        log.info { "切换至标准模式" }
        STANDARD_MODE_RECT.lClick()
    }

    private fun changeModeToWild() {
        log.info { "切换至狂野模式" }
        WILD_MODE_RECT.lClick()
    }

    private fun changeModeToCasual() {
        log.info { "切换至休闲模式" }
        CASUAL_MODE_RECT.lClick()
    }

    fun startMatching() {
        log.info { "开始匹配" }
        START_RECT.lClick()
        generateTimer()
    }

    /**
     * 生成匹配失败时兜底的定时器
     */
    private fun generateTimer() {
        cancelAllEnteredTasks()
        addEnteredTask(EXTRA_THREAD_POOL.schedule(LRunnable {
            if (PauseStatus.isPause || Thread.currentThread().isInterrupted || Mode.currMode === ModeEnum.GAMEPLAY) {
                cancelAllEnteredTasks()
            } else {
                log.info { "匹配失败，再次匹配中" }
                SystemUtil.notice("匹配失败，再次匹配中")
//                点击取消匹配按钮
                CANCEL_RECT.lClick()
                SystemUtil.delayLong()
//                点击错误按钮
                ERROR_RECT.lClick()
                SystemUtil.delayShort()
                reconnect()
                afterEnter(null)
            }
        }, ConfigUtil.getLong(ConfigEnum.MATCH_MAXIMUM_TIME), TimeUnit.SECONDS))
    }

}
