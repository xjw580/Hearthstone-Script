package club.xiaojiawei.config

import club.xiaojiawei.bean.LogThread
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/9/18 16:47
 */

val CALC_THREAD_POOL: ThreadPoolExecutor by lazy {
    ThreadPoolExecutor(4, 10, 60, TimeUnit.SECONDS, ArrayBlockingQueue(1), object : ThreadFactory {
        private val num = AtomicInteger(0)
        override fun newThread(r: Runnable): Thread {
            return LogThread(r, "CalcPool Thread-" + num.getAndIncrement())
        }
    }, ThreadPoolExecutor.AbortPolicy())
}