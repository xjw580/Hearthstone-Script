package club.xiaojiawei.strategy.mode

import club.xiaojiawei.bean.GameRect
import club.xiaojiawei.bean.LogRunnable
import club.xiaojiawei.config.EXTRA_THREAD_POOL
import club.xiaojiawei.strategy.AbstractModeStrategy
import java.util.concurrent.TimeUnit

/**
 * 开包界面
 * @author 肖嘉威
 * @date 2022/11/25 12:40
 */
object PackopeningModeStrategy : AbstractModeStrategy<Any?>() {

    val BACK_RECT: GameRect = GameRect(0.4040, 0.4558, 0.4058, 0.4376)

    override fun wantEnter() {
    }

    override fun afterEnter(t: Any?) {
        addEnteredTask(EXTRA_THREAD_POOL.scheduleWithFixedDelay(
            LogRunnable { BACK_RECT.lClick() },
            DELAY_TIME,
            500,
            TimeUnit.MILLISECONDS
        ))
    }

}