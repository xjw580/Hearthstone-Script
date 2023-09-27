package club.xiaojiawei.custom;

import java.util.concurrent.*;

/**
 * @author 肖嘉威
 * @date 2023/9/26 12:49
 */
public class LogThreadPoolExecutor extends ThreadPoolExecutor {

    public LogThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    @Override
    public void execute(Runnable command) {
        super.execute(new LogRunnable(command));
    }
}
