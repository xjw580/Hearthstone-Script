package club.xiaojiawei.hsscript.starter

import club.xiaojiawei.config.log
import club.xiaojiawei.hsscript.interfaces.closer.ManuallyReleased
import club.xiaojiawei.hsscript.interfaces.closer.ScheduledCloser
import club.xiaojiawei.hsscript.status.PauseStatus
import club.xiaojiawei.hsscript.status.TaskManager
import club.xiaojiawei.util.isFalse
import java.util.concurrent.ScheduledFuture

/**
 * @author 肖嘉威
 * @date 2023/7/5 14:37
 */
abstract class AbstractStarter : ScheduledCloser, ManuallyReleased {

    constructor() {
        TaskManager.addTask(this)
    }

    private var nextStarter: AbstractStarter? = null

    private var scheduledFuture: ScheduledFuture<*>? = null

    fun start() {
        log.info { "执行【${javaClass.simpleName}】" }
        stop()
        execStart()
    }

    fun setNextStarter(nextStarter: AbstractStarter?): AbstractStarter {
        return nextStarter?.also { this.nextStarter = it } ?: this
    }

    fun getNextStarter(): AbstractStarter? {
        return nextStarter
    }

    fun stop() {
        scheduledFuture?.let {
            it.isDone.isFalse {
                it.cancel(true)
            }
        }
    }

    protected fun addTask(taskFuture: ScheduledFuture<*>) {
        stop()
        scheduledFuture = taskFuture
    }

    protected abstract fun execStart()

    protected fun startNextStarter() {
        stop()
        nextStarter?.start()
    }

    protected fun pause() {
        stop()
        PauseStatus.asyncSetPause(true)
    }

    override fun close() {
        stop()
    }

    override fun release(){
        TaskManager.removeTask(this)
    }
}
