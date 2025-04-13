package club.xiaojiawei.hsscript.service

import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.listener.WorkTimeListener
import club.xiaojiawei.hsscript.status.ScriptStatus
import club.xiaojiawei.hsscript.utils.ConfigUtil
import club.xiaojiawei.hsscript.utils.SystemUtil
import com.sun.jna.platform.win32.WinDef.HWND
import javafx.beans.value.ChangeListener

/**
 * @author 肖嘉威
 * @date 2025/4/1 15:20
 */
object GameWindowOpacityService : Service<Int>() {
    private val windowChangeListener: ChangeListener<HWND?> by lazy {
        ChangeListener { _, _, newValue ->
            if (WorkTimeListener.working) {
                changeOpacity(ConfigUtil.getInt(ConfigEnum.GAME_WINDOW_OPACITY))
            }
        }
    }

    private val workingChangeListener: ChangeListener<Boolean> by lazy {
        ChangeListener { _, _, newValue ->
            if (newValue) {
                changeOpacity(ConfigUtil.getInt(ConfigEnum.GAME_WINDOW_OPACITY))
            } else {
                changeOpacity(255)
            }
        }
    }

    override fun execStart(): Boolean {
        if (WorkTimeListener.working) {
            changeOpacity(ConfigUtil.getInt(ConfigEnum.GAME_WINDOW_OPACITY))
        }
        ScriptStatus.gameHWNDProperty().addListener(windowChangeListener)
        WorkTimeListener.addChangeListener(workingChangeListener)
        return true
    }

    override fun execStop(): Boolean {
        ScriptStatus.gameHWNDProperty().removeListener(windowChangeListener)
        WorkTimeListener.removeChangeListener(workingChangeListener)
        changeOpacity(ConfigEnum.GAME_WINDOW_OPACITY.defaultValue.toInt())
        return true
    }

    override fun execIntelligentStartStop(value: Int?): Boolean = (value ?: ConfigUtil.getInt(ConfigEnum.GAME_WINDOW_OPACITY)) < 255

    override fun execValueChanged(
        oldValue: Int,
        newValue: Int,
    ) {
        changeOpacity(newValue)
    }

    private fun changeOpacity(opacity: Int) {
        SystemUtil.changeWindowOpacity(ScriptStatus.gameHWND, opacity)
    }
}
