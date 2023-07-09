package club.xiaojiawei.custom;

import lombok.extern.slf4j.Slf4j;

/**
 * 为什么不直接使用lambda呢，因为ScheduledThreadPoolExecutor出错不会打印日志且影响后续调度
 * @author 肖嘉威
 * @date 2023/7/8 16:19
 */
@Slf4j
public class LogRunnable implements Runnable{

    private final Runnable task;

    public LogRunnable(Runnable task) {
        this.task = task;
    }

    @Override
    public void run() {
        try {
            task.run();
        }catch (Exception e){
            log.error("发生错误" , e);
        }
    }
}
