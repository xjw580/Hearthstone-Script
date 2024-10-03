package club.xiaojiawei.strategy.mode

import club.xiaojiawei.bean.LogRunnable
import club.xiaojiawei.config.EXTRA_THREAD_POOL
import club.xiaojiawei.status.PauseStatus
import club.xiaojiawei.strategy.AbstractModeStrategy
import club.xiaojiawei.utils.GameUtil
import java.util.concurrent.TimeUnit

/**
 * 登录界面
 * @author 肖嘉威
 * @date 2022/11/25 12:27
 */
object LoginModeStrategy : AbstractModeStrategy<Any?>() {

    override fun wantEnter() {
    }

    override fun afterEnter(t: Any?) {
        //        去除国服登陆时恼人的点击开始和进入主界面时弹出的每日任务
        addEnteredTask(EXTRA_THREAD_POOL.scheduleWithFixedDelay(LogRunnable {
            if (PauseStatus.isPause) {
                cancelAllEnteredTasks()
            } else {
                GameUtil.lClickCenter()
            }
        }, 3000, 2000, TimeUnit.MILLISECONDS))
    }

}