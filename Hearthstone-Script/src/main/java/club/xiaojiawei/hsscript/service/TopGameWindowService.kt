package club.xiaojiawei.hsscript.service

import club.xiaojiawei.hsscript.dll.CSystemDll
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.status.PauseStatus
import club.xiaojiawei.hsscript.status.ScriptStatus
import club.xiaojiawei.hsscript.utils.ConfigUtil
import club.xiaojiawei.hsscript.utils.GameUtil
import com.sun.jna.platform.win32.WinDef.HWND
import javafx.beans.value.ChangeListener

/**
 * @author 肖嘉威
 * @date 2025/4/1 15:20
 */
object TopGameWindowService : Service<Boolean>() {

    private val changeListener: ChangeListener<HWND?> by lazy {
        ChangeListener { _, _, newValue ->
            changeTop(ConfigUtil.getBoolean(ConfigEnum.TOP_GAME_WINDOW), newValue)
        }
    }

    private val pauseChangeListener: ChangeListener<Boolean> by lazy {
        ChangeListener { _, _, newValue ->
            changeTop(!newValue)
        }
    }

    override fun execStart(): Boolean {
        if (ConfigUtil.getBoolean(ConfigEnum.TOP_GAME_WINDOW)) {
            ScriptStatus.gameHWNDProperty().addListener(changeListener)
            PauseStatus.addChangeListener(pauseChangeListener)
            return true
        }
        return false
    }

    override fun execStop(): Boolean {
        ScriptStatus.gameHWNDProperty().removeListener(changeListener)
        PauseStatus.removeChangeListener(pauseChangeListener)
        return true
    }

    override fun execValueChanged(oldValue: Boolean, newValue: Boolean) {
        changeTop(newValue)
    }

    private fun changeTop(top: Boolean, hwnd: HWND? = null) {
        (hwnd ?: GameUtil.findGameHWND())?.let {
            CSystemDll.INSTANCE.topWindow(it, top)
        }
    }

    override fun execIntelligentStartStop(value: Boolean?): Boolean {
        return (value ?: ConfigUtil.getBoolean(ConfigEnum.TOP_GAME_WINDOW))
    }
}