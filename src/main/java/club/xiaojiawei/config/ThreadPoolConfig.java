package club.xiaojiawei.config;

import club.xiaojiawei.custom.LogRunnable;
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

    @Bean
    public ScheduledThreadPoolExecutor launchProgramThreadPool(){
        return new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
            private final AtomicInteger num = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(new LogRunnable(r), "LaunchProgramPool Thread-" + num.getAndIncrement());
            }
        }, new ThreadPoolExecutor.AbortPolicy());
    }

    @Bean
    public ScheduledThreadPoolExecutor listenFileThreadPool(){
        return new ScheduledThreadPoolExecutor(4, new ThreadFactory() {
            private final AtomicInteger num = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(new LogRunnable(r), "ListenFilePool Thread-" + num.getAndIncrement());
            }
        }, new ThreadPoolExecutor.AbortPolicy());
    }

    @Bean
    public ScheduledThreadPoolExecutor extraThreadPool(){
        return new ScheduledThreadPoolExecutor(6, new ThreadFactory() {
            private final AtomicInteger num = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(new LogRunnable(r), "ExtraPool Thread-" + num.getAndIncrement());
            }
        }, new ThreadPoolExecutor.AbortPolicy());
    }

    @Bean
    public ThreadPoolExecutor coreThreadPool(){
        return new ThreadPoolExecutor(2, 2, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1), new ThreadFactory() {
            private final AtomicInteger num = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(new LogRunnable(r), "CorePool Thread-" + num.getAndIncrement());
            }
        }, new ThreadPoolExecutor.AbortPolicy());
    }

}
