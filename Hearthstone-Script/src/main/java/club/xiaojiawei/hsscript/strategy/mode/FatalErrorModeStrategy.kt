package club.xiaojiawei.hsscript.strategy.mode

import club.xiaojiawei.config.log
import club.xiaojiawei.hsscript.core.Core
import club.xiaojiawei.hsscript.strategy.AbstractModeStrategy

/**
 * 致命错误
 * @author 肖嘉威
 * @date 2022/12/10 22:35
 */
object FatalErrorModeStrategy : AbstractModeStrategy<Any?>() {

    override fun wantEnter() {
    }

    override fun afterEnter(t: Any?) {
        log.info{"发生致命错误，准备重启游戏"}
        Core.restart()
    }
}
