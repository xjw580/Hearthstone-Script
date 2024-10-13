package club.xiaojiawei.hsscript.appender;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 额外的日志Appender
 *
 * @author 肖嘉威 xjw580@qq.com
 * @date 2022/9/28 上午10:11
 */
public class ExtraLogAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    private static final List<Consumer<ILoggingEvent>> callbacks = new ArrayList<>();

    public static void addCallback(Consumer<ILoggingEvent> callback) {
        callbacks.add(callback);
    }

    public static void removeCallback(Consumer<ILoggingEvent> callback) {
        callbacks.remove(callback);
    }

    public static void clearCallbacks() {
        callbacks.clear();
    }

    @Override
    protected void append(ILoggingEvent event) {
        if (event.getLevel().levelInt >= Level.INFO_INT) {
            new ArrayList<>(callbacks).forEach(c -> c.accept(event));
        }
    }

}