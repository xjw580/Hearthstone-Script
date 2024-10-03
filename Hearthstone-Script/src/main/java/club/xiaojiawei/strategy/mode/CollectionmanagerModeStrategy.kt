package club.xiaojiawei.strategy.mode

import club.xiaojiawei.bean.GameRect
import club.xiaojiawei.bean.LogRunnable
import club.xiaojiawei.config.EXTRA_THREAD_POOL
import club.xiaojiawei.interfaces.closer.ModeTaskCloser
import club.xiaojiawei.strategy.AbstractModeStrategy
import club.xiaojiawei.utils.SystemUtil
import java.util.concurrent.TimeUnit

/**
 * 我的收藏
 * @author 肖嘉威
 * @date 2022/11/25 12:40
 */
object CollectionmanagerModeStrategy : AbstractModeStrategy<Any?>() {

    val BACK_RECT: GameRect = GameRect(0.4041, 0.4604, 0.4122, 0.4489)

    override fun wantEnter() {
        SystemUtil.copyToClipboard(" ")
    }

    override fun afterEnter(t: Any?) {
        addEnteredTask(EXTRA_THREAD_POOL.scheduleWithFixedDelay(LogRunnable {
            BACK_RECT.lClick()
        }, DELAY_TIME, 500, TimeUnit.MILLISECONDS))
    }

}
