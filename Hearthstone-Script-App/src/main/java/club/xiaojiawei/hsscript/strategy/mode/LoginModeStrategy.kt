package club.xiaojiawei.hsscript.strategy.mode

import club.xiaojiawei.bean.LRunnable
import club.xiaojiawei.config.EXTRA_THREAD_POOL
import club.xiaojiawei.config.log
import club.xiaojiawei.hsscript.bean.GameRect
import club.xiaojiawei.hsscript.status.Mode
import club.xiaojiawei.hsscript.status.PauseStatus
import club.xiaojiawei.hsscript.strategy.AbstractModeStrategy
import club.xiaojiawei.hsscript.utils.GameUtil
import java.util.concurrent.TimeUnit

/**
 * 登录界面
 * @author 肖嘉威
 * @date 2022/11/25 12:27
 */
object LoginModeStrategy : AbstractModeStrategy<Any?>() {

    /**
     * 卡牌削弱增强时的弹框关闭按钮
     */
    val CARD_ADJUSTMENT_CONFIRM_RECT: GameRect = GameRect(-0.0445, 0.0501, 0.2766, 0.3453)

    private var stayTime = 0

    override fun wantEnter() {
    }

    override fun afterEnter(t: Any?) {
        stayTime = 0
//        去除国服登陆时恼人的点击开始和进入主界面时可能弹出的卡牌调整弹窗还有每日任务弹窗
        addEnteredTask(EXTRA_THREAD_POOL.scheduleWithFixedDelay(LRunnable {
            stayTime++
            if (PauseStatus.isPause) {
                cancelAllEnteredTasks()
            } else if (stayTime > 7) {
                log.info { "长时间停留在${Mode.currMode?.comment}，尝试点击其他确定按钮" }
                CARD_ADJUSTMENT_CONFIRM_RECT.lClick()
            } else {
                GameUtil.lClickRightCenter()
            }
        }, 1000, 2000, TimeUnit.MILLISECONDS))
    }

}
