package club.xiaojiawei.hsscript.strategy.mode

import club.xiaojiawei.hsscript.bean.GameRect
import club.xiaojiawei.bean.LRunnable
import club.xiaojiawei.config.EXTRA_THREAD_POOL
import club.xiaojiawei.hsscript.strategy.AbstractModeStrategy
import java.util.concurrent.TimeUnit

/**
 * 酒馆
 * @author 肖嘉威
 * @date 2022/11/25 12:43
 */
object BaconModeStrategy : AbstractModeStrategy<Any?>() {

    val BACK_RECT: GameRect = GameRect(0.3869, 0.4429, 0.4211, 0.4507)

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
