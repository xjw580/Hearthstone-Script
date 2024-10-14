package club.xiaojiawei.hsscript.strategy.mode

import club.xiaojiawei.config.log
import club.xiaojiawei.enums.ModeEnum
import club.xiaojiawei.hsscript.status.Mode.prevMode
import club.xiaojiawei.hsscript.strategy.AbstractModeStrategy
import club.xiaojiawei.hsscript.utils.GameUtil
import club.xiaojiawei.hsscript.utils.SystemUtil.delay

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
