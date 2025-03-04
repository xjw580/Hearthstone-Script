package club.xiaojiawei.hsscript.appender

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.UnsynchronizedAppenderBase
import java.util.function.Consumer

/**
 * 额外的日志Appender
 *
 * @author 肖嘉威 xjw580@qq.com
 * @date 2022/9/28 上午10:11
 */
class ExtraLogAppender : UnsynchronizedAppenderBase<ILoggingEvent>() {
    override fun append(event: ILoggingEvent) {
        if (event.level.levelInt >= Level.INFO_INT) {
            ArrayList(callbacks).forEach(
                Consumer { c: Consumer<ILoggingEvent> -> c.accept(event) })
        }
    }

    companion object {
        private val callbacks: MutableList<Consumer<ILoggingEvent>> = ArrayList()

        fun addCallback(callback: Consumer<ILoggingEvent>) {
            callbacks.add(callback)
        }

        fun removeCallback(callback: Consumer<ILoggingEvent>) {
            callbacks.remove(callback)
        }

        fun clearCallbacks() {
            callbacks.clear()
        }
    }
}