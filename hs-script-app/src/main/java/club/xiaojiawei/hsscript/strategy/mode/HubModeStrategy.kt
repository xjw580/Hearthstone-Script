package club.xiaojiawei.hsscript.strategy.mode

import club.xiaojiawei.hsscriptbase.config.EXTRA_THREAD_POOL
import club.xiaojiawei.hsscriptbase.config.log
import club.xiaojiawei.hsscript.bean.GameRect
import club.xiaojiawei.hsscript.status.DeckStrategyManager
import club.xiaojiawei.hsscript.status.PauseStatus
import club.xiaojiawei.hsscript.strategy.AbstractModeStrategy
import club.xiaojiawei.hsscript.utils.SystemUtil
import java.util.concurrent.TimeUnit

/**
 * 主界面
 * @author 肖嘉威
 * @date 2022/11/25 12:36
 */
object HubModeStrategy : AbstractModeStrategy<Any?>() {

    /**
     * 广告弹窗关闭按钮
     */
    val CLOSE_AD1_RECT: GameRect = GameRect(-0.0364, 0.0493, 0.2764, 0.3255)

    /**
     * 月初结算弹窗关闭按钮
     */
    val CLOSE_SETTLEMENT_RECT = GameRect(-0.0498, 0.0534, 0.2944, 0.3424);

    /**
     * 月初结算弹窗宝箱
     */
    val CHEST_RECT = GameRect(-0.0771, 0.0858, -0.0368, 0.1576);

    /**
     * 结算完成按钮
     */
    val CONFIRM_SETTLEMENT_RECT = GameRect(-0.0668, 0.0709, -0.0007, 0.0611);

    /**
     * 未领取的奖励弹窗关闭按钮
     */
    val UNCLAIMED_REWARDS_RECT = GameRect(-0.0449, 0.0406, 0.1834, 0.2342)



    override fun wantEnter() {
    }

    override fun afterEnter(t: Any?) {
        addEnteredTask(EXTRA_THREAD_POOL.scheduleWithFixedDelay({
            if (PauseStatus.isPause) return@scheduleWithFixedDelay
            log.info { "点击广告弹窗等" }
            CLOSE_AD1_RECT.lClick()
            SystemUtil.delayShortMedium()
            CLOSE_SETTLEMENT_RECT.lClick()
            SystemUtil.delayShortMedium()
            UNCLAIMED_REWARDS_RECT.lClick()
            SystemUtil.delayShortMedium()
        }, 5, 2, TimeUnit.SECONDS))

        DeckStrategyManager.currentDeckStrategy?.let {
            if (it.runModes.isEmpty()) {
                SystemUtil.notice("当前卡组策略不允许运行在任何模式中")
                log.warn { "当前卡组策略不允许运行在任何模式中" }
                PauseStatus.isPause = true
                return
            }
            log.info { "准备进入指定模式" }
            it.runModes[0].modeEnum.modeStrategy?.wantEnter()
        } ?: let {
            SystemUtil.notice("未配置卡组策略")
            log.warn { "未配置卡组策略" }
            PauseStatus.isPause = true
        }
    }

}
