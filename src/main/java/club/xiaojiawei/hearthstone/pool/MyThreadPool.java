package club.xiaojiawei.hearthstone.pool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author 肖嘉威
 * @date 2022/12/3 15:53
 */
public class MyThreadPool {

    public static ThreadPoolExecutor myThreadPool = new ThreadPoolExecutor(0, 1, 3, TimeUnit.MINUTES, new ArrayBlockingQueue<>(1));

    public static void reset(){
        if (myThreadPool != null){
            myThreadPool.shutdownNow();
        }
        myThreadPool = new ThreadPoolExecutor(0, 1, 3, TimeUnit.MINUTES, new ArrayBlockingQueue<>(1));
    }

}
