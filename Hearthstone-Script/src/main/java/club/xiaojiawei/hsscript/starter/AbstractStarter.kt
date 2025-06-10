package club.xiaojiawei.hsscript.starter

import club.xiaojiawei.config.EXTRA_THREAD_POOL
import club.xiaojiawei.config.log
import club.xiaojiawei.hsscript.interfaces.closer.ScheduledCloser
import club.xiaojiawei.hsscript.status.PauseStatus
import club.xiaojiawei.hsscript.status.TaskManager
import club.xiaojiawei.util.isFalse
import java.io.Closeable
import java.util.concurrent.ScheduledFuture

/**
 * @author 肖嘉威
 * @date 2023/7/5 14:37
 */
abstract class AbstractStarter : ScheduledCloser, Closeable {

    constructor() {
        TaskManager.addTask(this)
    }

    private var nextStarter: AbstractStarter? = null

    protected var scheduledFuture: ScheduledFuture<*>? = null

    fun start() {
        log.info { "执行【${javaClass.simpleName}】" }
        stopTask()
        execStart()
    }

    fun setNextStarter(nextStarter: AbstractStarter?): AbstractStarter {
        return nextStarter?.also { this.nextStarter = it } ?: this
    }

    fun getNextStarter(): AbstractStarter? {
        return nextStarter
    }

    fun stopTask() {
        scheduledFuture?.let {
            it.isDone.isFalse {
                it.cancel(true)
            }
        }
        scheduledFuture = null
    }

    protected fun addTask(taskFuture: ScheduledFuture<*>) {
        stopTask()
        scheduledFuture = taskFuture
    }

    protected abstract fun execStart()

    protected fun startNextStarter() {
        EXTRA_THREAD_POOL.execute {
            nextStarter?.start()
        }
        stopTask()
    }

    protected fun pause() {
        stopTask()
        PauseStatus.asyncSetPause(true)
    }

    override fun stopAll() {
        stopTask()
    }

    /**
     * 释放资源，不代表关闭服务
     */
    override fun close() {
        TaskManager.removeTask(this)
    }

}
