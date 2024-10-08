package club.xiaojiawei.hsscript.strategy.mode

import club.xiaojiawei.hsscript.bean.GameRect
import club.xiaojiawei.bean.LogRunnable
import club.xiaojiawei.config.EXTRA_THREAD_POOL
import club.xiaojiawei.config.log
import club.xiaojiawei.enums.ModeEnum
import club.xiaojiawei.hsscript.status.Mode
import club.xiaojiawei.hsscript.status.PauseStatus
import club.xiaojiawei.hsscript.strategy.AbstractModeStrategy
import club.xiaojiawei.hsscript.utils.SystemUtil
import java.util.concurrent.TimeUnit

/**
 * 冒险模式
 * @author 肖嘉威
 * @date 2022/11/25 12:41
 */
object AdventureModeStrategy : AbstractModeStrategy<Any?>() {

    //    todo add
    val ADVENTURE_RECT: GameRect = GameRect.INVALID
    val CHOOSE_RECT: GameRect = GameRect.INVALID
    val START_RECT: GameRect = GameRect.INVALID
    val SELECT_DECK_RECT: GameRect = GameRect.INVALID

    override fun wantEnter() {
        addWantEnterTask(EXTRA_THREAD_POOL.scheduleWithFixedDelay(LogRunnable {
            if (PauseStatus.isPause) {
                cancelAllWantEnterTasks()
            } else if (Mode.currMode == ModeEnum.HUB) {
                cancelAllWantEnterTasks()
                ModeEnum.GAME_MODE.modeStrategy?.wantEnter()
            } else if (Mode.currMode == ModeEnum.GAME_MODE) {
//                    点击冒险模式
                ADVENTURE_RECT.lClick()
                SystemUtil.delayMedium()
//                    点击选择按钮进入冒险模式
                CHOOSE_RECT.lClick()
            } else {
                cancelAllWantEnterTasks()
            }
        }, DELAY_TIME, INTERVAL_TIME, TimeUnit.MILLISECONDS))
    }

    override fun afterEnter(t: Any?) {
        clickStart()
        SystemUtil.delayLong()
        selectDeck()
        SystemUtil.delayMedium()
        clickStart()
        SystemUtil.delayLong()
        selectHero()
        SystemUtil.delayLong()
        clickStart()
    }

    private fun clickStart() {
        log.info{"点击开始"}
        START_RECT.lClick()
    }

    private fun selectDeck() {
        log.info{"选择套牌"}
        SELECT_DECK_RECT.lClick()
    }

    private fun selectHero() {
        TODO("lazy")
    }
}
