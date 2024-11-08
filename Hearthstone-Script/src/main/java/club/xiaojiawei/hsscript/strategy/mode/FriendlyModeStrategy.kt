package club.xiaojiawei.hsscript.strategy.mode

import club.xiaojiawei.hsscript.status.DeckStrategyManager
import club.xiaojiawei.hsscript.listener.WorkListener
import club.xiaojiawei.hsscript.listener.log.PowerLogListener
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
            TournamentModeStrategy.selectDeck(DeckStrategyManager.currentDeckStrategy)
            SystemUtil.delayShort()
            TournamentModeStrategy.startMatching()
        } else {
            WorkListener.stopWork()
        }
    }
}
