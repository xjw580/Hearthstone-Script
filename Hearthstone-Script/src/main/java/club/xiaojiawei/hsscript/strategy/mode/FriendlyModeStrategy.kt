package club.xiaojiawei.hsscript.strategy.mode

import club.xiaojiawei.config.log
import club.xiaojiawei.hsscript.status.DeckStrategyManager
import club.xiaojiawei.hsscript.listener.WorkListener
import club.xiaojiawei.hsscript.listener.log.PowerLogListener
import club.xiaojiawei.hsscript.status.PauseStatus
import club.xiaojiawei.hsscript.strategy.AbstractModeStrategy
import club.xiaojiawei.hsscript.utils.SystemUtil

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/11/6 15:40
 */
object FriendlyModeStrategy : AbstractModeStrategy<Any?>() {

    override fun wantEnter() {
    }

    override fun afterEnter(t: Any?) {
        if (WorkListener.isDuringWorkDate()) {
            if (!PowerLogListener.checkPowerLogSize()) {
                return
            }
            DeckStrategyManager.currentDeckStrategy?.let {
                TournamentModeStrategy.selectDeck(it)
                SystemUtil.delayShort()
                TournamentModeStrategy.startMatching()
            } ?: let {
                SystemUtil.notice("未配置卡组策略")
                log.warn { "未配置卡组策略" }
                PauseStatus.isPause = true
            }
        } else {
            WorkListener.stopWork()
        }
    }
}
