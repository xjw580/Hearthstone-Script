package club.xiaojiawei.hsscript.starter

import club.xiaojiawei.config.log
import club.xiaojiawei.util.isFalse
import java.util.concurrent.ScheduledFuture

/**
 * @author 肖嘉威
 * @date 2023/7/5 14:37
 */
abstract class AbstractStarter {

    private var nextStarter: AbstractStarter? = null

    private var scheduledFuture: ScheduledFuture<*>? = null

    fun start() {
        log.info { "执行" + javaClass.simpleName }
        stop()
        execStart()
    }

    fun setNextStarter(nextStarter: AbstractStarter): AbstractStarter {
        return nextStarter.also { this.nextStarter = it }
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
}
