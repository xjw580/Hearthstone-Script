package club.xiaojiawei.pool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author 肖嘉威
 * @date 2022/12/3 15:53
 */
public class MyThreadPool {
    public final static ThreadPoolExecutor myTurnThreadPool = new ThreadPoolExecutor(0, 1, 3, TimeUnit.MINUTES, new ArrayBlockingQueue<>(1));

}
