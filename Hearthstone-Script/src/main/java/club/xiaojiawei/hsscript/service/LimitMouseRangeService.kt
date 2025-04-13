package club.xiaojiawei.hsscript.service

import club.xiaojiawei.hsscript.dll.CSystemDll
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.listener.WorkTimeListener
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
            if (WorkTimeListener.working) {
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
        if (WorkTimeListener.working) {
            CSystemDll.INSTANCE.limitMouseRange(true)
        }
        ScriptStatus.gameHWNDProperty().addListener(hwndListener)
        WorkTimeListener.addChangeListener(workingListener)
        return true
    }

    override fun execStop(): Boolean {
        ScriptStatus.gameHWNDProperty().removeListener(hwndListener)
        WorkTimeListener.removeChangeListener(workingListener)
        CSystemDll.INSTANCE.limitMouseRange(false)
        return true
    }

    override fun execIntelligentStartStop(value: Boolean?): Boolean = (value ?: ConfigUtil.getBoolean(ConfigEnum.LIMIT_MOUSE_RANGE))

    override fun execValueChanged(
        oldValue: Boolean,
        newValue: Boolean,
    ) {
        CSystemDll.INSTANCE.limitMouseRange(newValue)
    }
}
