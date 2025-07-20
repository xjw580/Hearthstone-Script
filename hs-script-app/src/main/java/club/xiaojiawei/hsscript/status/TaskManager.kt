package club.xiaojiawei.hsscript.status

import club.xiaojiawei.hsscript.interfaces.closer.Closable
import club.xiaojiawei.hsscript.listener.WorkTimeListener

/**
 * @author 肖嘉威
 * @date 2024/10/18 22:23
 */
object TaskManager {

    private val taskList: MutableSet<Closable> = mutableSetOf()

    val launch: Unit by lazy {
        PauseStatus.addChangeListener { _, _, isPause ->
            if (isPause) {
                closeAllTasks()
            }
        }
        WorkTimeListener.addChangeListener { _, _, isWorking ->
            if (!PauseStatus.isPause && !isWorking) {
                closeAllTasks()
            }
        }
    }

    @Synchronized
    fun addTask(task: Closable) {
        taskList.add(task)
    }

    @Synchronized
    fun removeTask(task: Closable) {
        taskList.remove(task)
    }

    @Synchronized
    fun closeAllTasks() {
        taskList.toList().forEach {
            it.stopAll()
        }
    }

}