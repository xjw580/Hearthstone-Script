package club.xiaojiawei.hsscript.appender

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.UnsynchronizedAppenderBase
import java.util.concurrent.ArrayBlockingQueue

/**
 * 额外的日志Appender
 *
 * @author 肖嘉威 xjw580@qq.com
 * @date 2022/9/28 上午10:11
 */
class ExtraLogAppender : UnsynchronizedAppenderBase<ILoggingEvent>() {

    companion object {

        val logQueue = ArrayBlockingQueue<ILoggingEvent>(100)

    }

    override fun append(event: ILoggingEvent) {
        if (event.level.levelInt >= Level.INFO_INT) {
            logQueue.add(event)
        }
    }

}