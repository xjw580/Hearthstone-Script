package club.xiaojiawei.hsscript.strategy.mode

import club.xiaojiawei.hsscript.status.DeckStrategyManager
import club.xiaojiawei.hsscript.status.Work
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
        if (Work.isDuringWorkDate()) {
            TournamentModeStrategy.selectDeck(DeckStrategyManager.CURRENT_DECK_STRATEGY.get())
            SystemUtil.delayShort()
            TournamentModeStrategy.startMatching()
        } else {
            Work.stopWork()
        }
    }
}
