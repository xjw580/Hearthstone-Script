package club.xiaojiawei.hsscript.status

import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.listener.log.AbstractLogListener
import club.xiaojiawei.hsscript.utils.ConfigUtil
import java.io.File
import java.nio.file.Path

/**
 * @author 肖嘉威
 * @date 2024/9/28 21:19
 */
object LogListenerStatus {

    var logPath: File? = null

    val activeListeners = mutableListOf<AbstractLogListener>()

    init {
        PauseStatus.addListener{_, _, newValue ->
            if (!newValue){
                closeAllLogListeners()
            }
        }
    }

    fun addLogListener(listener: AbstractLogListener) {
        activeListeners.add(listener)
    }

    fun removeLogListener(listener: AbstractLogListener) {
        listener.close()
        activeListeners.remove(listener)
    }

    fun closeAllLogListeners() {
        for (listener in activeListeners) {
            listener.close()
        }
        activeListeners.clear()
    }

}