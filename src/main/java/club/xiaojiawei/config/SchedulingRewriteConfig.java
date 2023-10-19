package club.xiaojiawei.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * 重写Spring的定时任务线程池，规范线程命名
 * @author 肖嘉威
 * @date 2023/9/17 22:10
 */
@Configuration
public class SchedulingRewriteConfig {
    @Bean
    public TaskScheduler taskScheduler(){
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setThreadNamePrefix("SchedulePool Thread-");
        taskScheduler.setPoolSize(4);
        taskScheduler.initialize();
        return taskScheduler;
    }
}
