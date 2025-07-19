package club.xiaojiawei.hsscript.strategy.mode

import club.xiaojiawei.hsscript.bean.GameRect
import club.xiaojiawei.bean.LRunnable
import club.xiaojiawei.config.EXTRA_THREAD_POOL
import club.xiaojiawei.hsscript.strategy.AbstractModeStrategy
import java.util.concurrent.TimeUnit

/**
 * 乱斗
 * @author 肖嘉威
 * @date 2023/7/8 15:29
 */
object TavernBrawlModeStrategy : AbstractModeStrategy<Any?>(){

    val BACK_RECT: GameRect = GameRect(0.4040, 0.4591, 0.4146, 0.4474)

    override fun wantEnter() {
    }

    override fun afterEnter(t: Any?) {
        addEnteredTask(EXTRA_THREAD_POOL.scheduleWithFixedDelay(
            LRunnable { BACK_RECT.lClick() },
            DELAY_TIME,
            500,
            TimeUnit.MILLISECONDS
        ))
    }

}
