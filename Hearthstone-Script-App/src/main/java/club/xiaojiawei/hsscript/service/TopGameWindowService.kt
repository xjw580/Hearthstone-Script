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
object TopGameWindowService : Service<Boolean>() {
    private val changeListener: ChangeListener<HWND?> by lazy {
        ChangeListener { _, _, newValue ->
            if (WorkTimeListener.working) {
                changeTop(ConfigUtil.getBoolean(ConfigEnum.TOP_GAME_WINDOW))
            }
        }
    }

    private val workingChangeListener: ChangeListener<Boolean> by lazy {
        ChangeListener { _, _, newValue ->
            changeTop(newValue)
        }
    }

    override fun execStart(): Boolean {
        if (WorkTimeListener.working) {
            changeTop(true)
        }
        ScriptStatus.gameHWNDProperty().addListener(changeListener)
        WorkTimeListener.addChangeListener(workingChangeListener)
        return true
    }

    override fun execStop(): Boolean {
        ScriptStatus.gameHWNDProperty().removeListener(changeListener)
        WorkTimeListener.removeChangeListener(workingChangeListener)
        changeTop(false)
        return true
    }

    override fun execValueChanged(
        oldValue: Boolean,
        newValue: Boolean,
    ) {
        changeTop(newValue)
    }

    private fun changeTop(top: Boolean) {
        CSystemDll.INSTANCE.topWindow(ScriptStatus.gameHWND, top)
    }

    override fun execIntelligentStartStop(value: Boolean?): Boolean = (value ?: ConfigUtil.getBoolean(ConfigEnum.TOP_GAME_WINDOW))
}
