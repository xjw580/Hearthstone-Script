package club.xiaojiawei.config;

import club.xiaojiawei.bean.LogThread;
import club.xiaojiawei.bean.WritableThread;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池配置
 * @author 肖嘉威
 * @date 2023/7/5 13:35
 */
@Configuration
public class ThreadPoolConfig {

    @Bean
    public ScheduledThreadPoolExecutor launchProgramThreadPool(){
        return new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
            private final AtomicInteger num = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                return new LogThread(r, "LaunchProgramPool Thread-" + num.getAndIncrement());
            }
        }, new ThreadPoolExecutor.AbortPolicy());
    }

    @Bean
    public ScheduledThreadPoolExecutor listenFileThreadPool(){
        return new ScheduledThreadPoolExecutor(4, new ThreadFactory() {
            private final AtomicInteger num = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                return new WritableThread(r, "ListenFilePool Thread-" + num.getAndIncrement());
            }
        }, new ThreadPoolExecutor.AbortPolicy());
    }

    @Bean
    public ScheduledThreadPoolExecutor extraThreadPool(){
        return new ScheduledThreadPoolExecutor(6, new ThreadFactory() {
            private final AtomicInteger num = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                return new LogThread(r, "ExtraPool Thread-" + num.getAndIncrement());
            }
        }, new ThreadPoolExecutor.AbortPolicy());
    }

    @Bean
    public ThreadPoolExecutor coreThreadPool(){
        return new ThreadPoolExecutor(2, 2, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1), new ThreadFactory() {
            private final AtomicInteger num = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                return new LogThread(r, "CorePool Thread-" + num.getAndIncrement());
            }
        }, new ThreadPoolExecutor.AbortPolicy());
    }

}
