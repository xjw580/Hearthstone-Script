package club.xiaojiawei.hsscript.config

import club.xiaojiawei.hsscript.listener.log.AbstractLogListener
import club.xiaojiawei.hsscript.listener.log.DeckLogListener
import club.xiaojiawei.hsscript.listener.log.PowerLogListener
import club.xiaojiawei.hsscript.listener.log.ScreenLogListener

/**
 * @author 肖嘉威
 * @date 2024/10/14 10:00
 */
object LogListenerConfig {

    val logListener: AbstractLogListener = DeckLogListener

    init {
        DeckLogListener
            .setNextLogListener(PowerLogListener)
            .setNextLogListener(ScreenLogListener)
    }

}