package club.xiaojiawei.hsscript.status

import club.xiaojiawei.config.EXTRA_THREAD_POOL
import club.xiaojiawei.hsscript.utils.onlyWriteRun
import javafx.beans.property.ReadOnlyBooleanWrapper
import javafx.beans.value.ChangeListener

/**
 * 脚本暂停状态
 * @author 肖嘉威
 * @date 2023/7/5 15:04
 */
object PauseStatus {

    private val isPauseProperty: ReadOnlyBooleanWrapper = ReadOnlyBooleanWrapper(true)

    var isPause: Boolean
        get() {
            return isPauseProperty.get()
        }
        set(value) {
            isPauseProperty.set(value)
        }

    fun asyncSetPause(isPaused: Boolean) {
        EXTRA_THREAD_POOL.submit {
            isPauseProperty.set(isPaused)
        }
    }

    fun addListener(listener: ChangeListener<Boolean>) {
        isPauseProperty.addListener(listener)
    }

    fun removeListener(listener: ChangeListener<Boolean>) {
        isPauseProperty.removeListener(listener)
    }

}
