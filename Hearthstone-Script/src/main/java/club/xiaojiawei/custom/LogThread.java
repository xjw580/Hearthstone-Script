package club.xiaojiawei.custom;

import club.xiaojiawei.bean.LogRunnable;

/**
 * @author 肖嘉威
 * @date 2024/9/8 11:14
 */
public class LogThread extends Thread{

    public LogThread(Runnable task) {
        super(new club.xiaojiawei.bean.LogRunnable(task));
    }

    public LogThread(Runnable task, String name) {
        super(new LogRunnable(task), name);
    }
}
