package club.xiaojiawei.status

import club.xiaojiawei.listener.log.AbstractLogListener
import java.io.File

/**
 * @author 肖嘉威
 * @date 2024/9/28 21:19
 */
object LogListenerStatus {

    var logDir: File? = null

    val activeListeners = mutableListOf<AbstractLogListener>()

    fun closeAllLogListeners() {
        for (listener in activeListeners) {
            listener.close()
        }
        activeListeners.clear()
    }

}