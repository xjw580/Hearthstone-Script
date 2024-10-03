package club.xiaojiawei.strategy.mode

import club.xiaojiawei.status.DeckStrategyManager
import club.xiaojiawei.status.Work
import club.xiaojiawei.strategy.AbstractModeStrategy
import club.xiaojiawei.utils.SystemUtil
import jakarta.annotation.Resource
import java.util.*

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
