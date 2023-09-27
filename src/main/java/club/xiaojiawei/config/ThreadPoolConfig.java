package club.xiaojiawei.config;

import club.xiaojiawei.custom.LogRunnable;
import club.xiaojiawei.custom.LogScheduledThreadPoolExecutor;
import club.xiaojiawei.custom.LogThreadPoolExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 肖嘉威
 * @date 2023/7/5 13:35
 * @msg
 */
@Configuration
public class ThreadPoolConfig {

    /**
     * 使用 ${@link LogRunnable}
     * @return
     */
    @Bean
    public LogScheduledThreadPoolExecutor launchProgramThreadPool(){
        return new LogScheduledThreadPoolExecutor(1, new ThreadFactory() {
            private final AtomicInteger num = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "LaunchProgramPool Thread-" + num.getAndIncrement());
            }
        }, new ThreadPoolExecutor.AbortPolicy());
    }

    /**
     * 使用 ${@link LogRunnable}
     * @return
     */
    @Bean
    public LogScheduledThreadPoolExecutor listenFileThreadPool(){
        return new LogScheduledThreadPoolExecutor(4, new ThreadFactory() {
            private final AtomicInteger num = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "ListenFilePool Thread-" + num.getAndIncrement());
            }
        }, new ThreadPoolExecutor.AbortPolicy());
    }

    /**
     * 使用 ${@link LogRunnable}
     * @return
     */
    @Bean
    public LogScheduledThreadPoolExecutor extraThreadPool(){
        return new LogScheduledThreadPoolExecutor(5, new ThreadFactory() {
            private final AtomicInteger num = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "ExtraPool Thread-" + num.getAndIncrement());
            }
        }, new ThreadPoolExecutor.AbortPolicy());
    }

    @Bean
    public LogThreadPoolExecutor coreThreadPool(){
        return new LogThreadPoolExecutor(2, 2, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1), new ThreadFactory() {
            private final AtomicInteger num = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "CorePool Thread-" + num.getAndIncrement());
            }
        }, new ThreadPoolExecutor.AbortPolicy());
    }

}
