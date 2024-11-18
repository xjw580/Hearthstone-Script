package club.xiaojiawei.config

import club.xiaojiawei.bean.ReadableThread
import club.xiaojiawei.bean.WritableThread
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/9/18 16:47
 */
val LAUNCH_PROGRAM_THREAD_POOL: ScheduledThreadPoolExecutor by lazy {
    ScheduledThreadPoolExecutor(6, object : ThreadFactory {
        private val num = AtomicInteger(0)
        override fun newThread(r: Runnable): Thread {
            return WritableThread(r, "LaunchProgramPool Thread-" + num.getAndIncrement())
        }
    }, ThreadPoolExecutor.AbortPolicy())
}

val LISTEN_LOG_THREAD_POOL: ScheduledThreadPoolExecutor by lazy {
    ScheduledThreadPoolExecutor(4, object : ThreadFactory {
        private val num = AtomicInteger(0)
        override fun newThread(r: Runnable): Thread {
            return WritableThread(r, "ListenLogPool Thread-" + num.getAndIncrement())
        }
    }, ThreadPoolExecutor.AbortPolicy())
}

val EXTRA_THREAD_POOL: ScheduledThreadPoolExecutor by lazy {
    ScheduledThreadPoolExecutor(6, object : ThreadFactory {
        private val num = AtomicInteger(0)
        override fun newThread(r: Runnable): Thread {
            return ReadableThread(r, "ExtraPool Thread-" + num.getAndIncrement())
        }
    }, ThreadPoolExecutor.AbortPolicy())
}

val CORE_THREAD_POOL: ThreadPoolExecutor by lazy {
    ThreadPoolExecutor(2, 2, 1, TimeUnit.SECONDS, ArrayBlockingQueue(1), object : ThreadFactory {
        private val num = AtomicInteger(0)
        override fun newThread(r: Runnable): Thread {
            return WritableThread(r, "CorePool Thread-" + num.getAndIncrement())
        }
    }, ThreadPoolExecutor.AbortPolicy())
}

val CALC_THREAD_POOL: ThreadPoolExecutor by lazy {
    ThreadPoolExecutor(8, 16, 60, TimeUnit.SECONDS, ArrayBlockingQueue(8), object : ThreadFactory {
        private val num = AtomicInteger(0)
        override fun newThread(r: Runnable): Thread {
            return ReadableThread(r, "CalcPool Thread-" + num.getAndIncrement())
        }
    }, ThreadPoolExecutor.AbortPolicy())
}