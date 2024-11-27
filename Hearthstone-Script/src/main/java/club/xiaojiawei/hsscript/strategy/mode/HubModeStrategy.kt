package club.xiaojiawei.hsscript.strategy.mode

import club.xiaojiawei.config.EXTRA_THREAD_POOL
import club.xiaojiawei.config.log
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

    //    TODO ADD
    val CLOSE_AD1_RECT: GameRect = GameRect(-0.0296, 0.0431, 0.2502, 0.2552)

    override fun wantEnter() {
    }

    override fun afterEnter(t: Any?) {
        addEnteredTask(EXTRA_THREAD_POOL.scheduleAtFixedRate({
            if (PauseStatus.isPause) return@scheduleAtFixedRate
            log.info { "点击广告弹窗等" }
            CLOSE_AD1_RECT.lClick()
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
