package club.xiaojiawei.hsscript.strategy.mode

import club.xiaojiawei.config.log
import club.xiaojiawei.enums.ModeEnum
import club.xiaojiawei.hsscript.bean.GameRect
import club.xiaojiawei.hsscript.status.DeckStrategyManager
import club.xiaojiawei.hsscript.status.Mode.prevMode
import club.xiaojiawei.hsscript.status.PauseStatus
import club.xiaojiawei.hsscript.strategy.AbstractModeStrategy
import club.xiaojiawei.hsscript.utils.SystemUtil

/**
 * 主界面
 * @author 肖嘉威
 * @date 2022/11/25 12:36
 */
object HubModeStrategy : AbstractModeStrategy<Any?>() {

    val TOURNAMENT_MODE_RECT: GameRect = GameRect(-0.0790, 0.0811, -0.2090, -0.1737)

    //    TODO ADD
    val CLOSE_AD1_RECT: GameRect = GameRect(-0.0790, 0.0811, -0.2090, -0.1737)

    val CLOSE_AD2_RECT: GameRect = GameRect(-0.0296, 0.0431, 0.2502, 0.2552)

    override fun wantEnter() {
    }

    override fun afterEnter(t: Any?) {
        if (prevMode != ModeEnum.COLLECTIONMANAGER) {
            log.info { "点击弹窗（去除任务，活动等）" }
            for (i in 0..3) {
                if (PauseStatus.isPause) {
                    return
                }
                CLOSE_AD1_RECT.lClick()
                SystemUtil.delay(500)
            }
            CLOSE_AD2_RECT.lClick()
            SystemUtil.delayTiny()
        }

        DeckStrategyManager.currentDeckStrategy?.let {
            log.info { "准备进入指定模式" }
            if (it.runModes.isEmpty()){
                SystemUtil.notice("未配置卡组")
                log.error { "未配置卡组" }
                PauseStatus.isPause = false
                return
            }
            it.runModes[0].modeEnum.modeStrategy?.wantEnter()
        }
    }

}
