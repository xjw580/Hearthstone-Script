package club.xiaojiawei.hsscript.strategy

import club.xiaojiawei.config.log
import club.xiaojiawei.hsscript.interfaces.closer.ScheduledCloser
import club.xiaojiawei.hsscript.status.Mode
import club.xiaojiawei.hsscript.status.TaskManager
import club.xiaojiawei.interfaces.ModeStrategy
import club.xiaojiawei.util.isFalse
import java.util.*
import java.util.concurrent.ScheduledFuture

/**
 * 游戏模式抽象类
 * @author 肖嘉威
 * @date 2022/11/26 17:39
 */
abstract class AbstractModeStrategy<T> : ModeStrategy<T> {

    override fun entering() {
        entering(null)
    }

    override fun entering(t: T?) {
        beforeEnter()
        log.info { "切換到【${Mode.currMode?.comment}】" }
        afterEnter(t)
    }

    override fun afterLeave() {
        cancelAllEnteredTasks()
    }

    protected abstract fun afterEnter(t: T?)

    protected fun beforeEnter() {
        cancelAllWantEnterTasks()
    }

    protected fun addWantEnterTask(task: ScheduledFuture<*>): ScheduledFuture<*> {
        wantEnterTasks.add(task)
        return task
    }

    protected fun cancelWantEnterTask(task: ScheduledFuture<*>) {
        task.isDone.isFalse {
            wantEnterTasks.remove(task)
            task.cancel(true)
        }
    }

    protected fun addEnteredTask(task: ScheduledFuture<*>): ScheduledFuture<*> {
        enteredTasks.add(task)
        return task
    }

    protected fun cancelEnteredTask(task: ScheduledFuture<*>) {
        task.isDone.isFalse {
            enteredTasks.remove(task)
            task.cancel(true)
        }
    }


    companion object:ScheduledCloser {

        init {
            TaskManager.addTask(this)
        }

        const val INTERVAL_TIME: Long = 5000
        const val DELAY_TIME: Long = 1000

        private var wantEnterTasks: MutableList<ScheduledFuture<*>> = Collections.synchronizedList(mutableListOf())
        private var enteredTasks: MutableList<ScheduledFuture<*>> = Collections.synchronizedList(mutableListOf())

        fun cancelAllEnteredTasks() {
            val listOf = enteredTasks.toList()
            enteredTasks.clear()
            listOf.forEach {
                it.isDone.isFalse {
                    it.cancel(true)
                }
            }
        }

        fun cancelAllWantEnterTasks() {
            val listOf = wantEnterTasks.toList()
            wantEnterTasks.clear()
            listOf.forEach {
                it.isDone.isFalse {
                    it.cancel(true)
                }
            }
        }

        fun cancelAllTask() {
            cancelAllEnteredTasks()
            cancelAllWantEnterTasks()
        }

        override fun close() {
            cancelAllTask()
        }

    }

}
