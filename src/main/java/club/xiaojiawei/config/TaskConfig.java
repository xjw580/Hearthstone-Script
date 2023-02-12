package club.xiaojiawei.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author 肖嘉威
 * @date 2022/12/11 10:18
 */
@Configuration
public class TaskConfig implements SchedulingConfigurer {

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler executor = new ThreadPoolTaskScheduler();
        executor.setPoolSize(6);
        executor.setThreadNamePrefix("task-thread");
        //设置饱和策略
        //CallerRunsPolicy：线程池的饱和策略之一，当线程池使用饱和后，直接使用调用者所在的线程来执行任务；如果执行程序已关闭，则会丢弃该任务
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        executor.initialize();
        return executor;
    }

    //配置@Scheduled 定时器所使用的线程池
    //配置任务注册器：ScheduledTaskRegistrar 的任务调度器
    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        //可配置两种类型：TaskScheduler、ScheduledExecutorService
        //scheduledTaskRegistrar.setScheduler(taskScheduler());
        //只可配置一种类型：taskScheduler
        scheduledTaskRegistrar.setTaskScheduler(taskScheduler());
    }

}
