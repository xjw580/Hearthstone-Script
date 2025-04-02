package club.xiaojiawei.hsscript.service

import club.xiaojiawei.hsscript.data.gameHWNDProperty
import club.xiaojiawei.hsscript.dll.CSystemDll
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.listener.WorkListener
import club.xiaojiawei.hsscript.utils.ConfigUtil
import com.sun.jna.platform.win32.WinDef.HWND
import javafx.beans.value.ChangeListener

/**
 * @author 肖嘉威
 * @date 2025/4/1 15:20
 */
object LimitMouseRangeService : Service<Boolean>() {

    private val hwndListener: ChangeListener<HWND?> by lazy {
        ChangeListener<HWND?> { _, _, newValue ->
            if (WorkListener.working) {
                CSystemDll.INSTANCE.limitMouseRange(ConfigUtil.getBoolean(ConfigEnum.LIMIT_MOUSE_RANGE))
            }
        }
    }
    private val workingListener: ChangeListener<Boolean> by lazy {
        ChangeListener<Boolean> { _, _, newValue ->
            CSystemDll.INSTANCE.limitMouseRange(newValue)
        }
    }

    override fun execStart(): Boolean {
        if (ConfigUtil.getBoolean(ConfigEnum.LIMIT_MOUSE_RANGE)) {
            gameHWNDProperty().addListener(hwndListener)
            WorkListener.workingProperty.addListener(workingListener)
        }
        return true
    }

    override fun execStop(): Boolean {
        gameHWNDProperty().removeListener(hwndListener)
        WorkListener.workingProperty.removeListener(workingListener)
        return true
    }

    override fun execValueChanged(oldValue: Boolean, newValue: Boolean) {
        CSystemDll.INSTANCE.limitMouseRange(newValue)
    }

}

fun main() {
    CSystemDll.INSTANCE.limitMouseRange(false)
}