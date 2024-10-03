package club.xiaojiawei.strategy.mode

import club.xiaojiawei.config.log
import club.xiaojiawei.enums.ModeEnum
import club.xiaojiawei.status.Mode.prevMode
import club.xiaojiawei.strategy.AbstractModeStrategy
import club.xiaojiawei.utils.GameUtil

/**
 * 游戏界面
 *
 * @author 肖嘉威
 * @date 2022/11/25 12:43
 */
object GameplayModeStrategy : AbstractModeStrategy<Any?>() {

    override fun wantEnter() {
    }

    override fun afterEnter(t: Any?) {
        if (prevMode == ModeEnum.LOGIN || prevMode == null) {
            log.info { "当前对局不完整，准备投降" }
            GameUtil.surrender()
        }
    }
}
