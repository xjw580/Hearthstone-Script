package club.xiaojiawei.hsscript.service

import club.xiaojiawei.hsscript.dll.CSystemDll
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.listener.WorkListener
import club.xiaojiawei.hsscript.status.ScriptStatus
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
            ScriptStatus.gameHWNDProperty().addListener(hwndListener)
            WorkListener.addChangeListener(workingListener)
        }
        return true
    }

    override fun execStop(): Boolean {
        ScriptStatus.gameHWNDProperty().removeListener(hwndListener)
        WorkListener.removeChangeListener(workingListener)
        return true
    }

    override fun execIntelligentStartStop(value: Boolean?): Boolean {
        return (value ?: ConfigUtil.getBoolean(ConfigEnum.LIMIT_MOUSE_RANGE))
    }

    override fun execValueChanged(oldValue: Boolean, newValue: Boolean) {
        CSystemDll.INSTANCE.limitMouseRange(newValue)
    }

}