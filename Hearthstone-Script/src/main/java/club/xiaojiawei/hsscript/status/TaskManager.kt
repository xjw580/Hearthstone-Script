package club.xiaojiawei.hsscript.status

import club.xiaojiawei.hsscript.interfaces.closer.Closable
import club.xiaojiawei.hsscript.listener.WorkListener

/**
 * @author 肖嘉威
 * @date 2024/10/18 22:23
 */
object TaskManager {

    private val taskList: MutableSet<Closable> = mutableSetOf()

    init {
        PauseStatus.addListener { _, _, isPause ->
            if (isPause) {
                closeAllTasks()
            }
        }
        WorkListener.workingProperty.addListener { _, _, isWorking ->
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
            it.close()
        }
    }

}