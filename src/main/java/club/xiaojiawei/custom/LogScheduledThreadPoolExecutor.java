package club.xiaojiawei.custom;

import java.util.concurrent.*;

/**
 * @author 肖嘉威
 * @date 2023/9/26 12:54
 */
public class LogScheduledThreadPoolExecutor extends ScheduledThreadPoolExecutor {

    public LogScheduledThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, threadFactory, handler);
    }

    @Override
    public void execute(Runnable command) {
        super.execute(new LogRunnable(command));
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return super.scheduleAtFixedRate(new LogRunnable(command), initialDelay, period, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        return super.scheduleWithFixedDelay(new LogRunnable(command), initialDelay, delay, unit);
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        return super.schedule(new LogRunnable(command), delay, unit);
    }

}
