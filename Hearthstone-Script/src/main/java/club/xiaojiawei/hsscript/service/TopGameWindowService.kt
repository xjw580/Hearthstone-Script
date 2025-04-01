package club.xiaojiawei.hsscript.service

import club.xiaojiawei.hsscript.data.GAME_HWND
import club.xiaojiawei.hsscript.data.gameHWNDProperty
import club.xiaojiawei.hsscript.dll.CSystemDll
import club.xiaojiawei.hsscript.enums.ConfigEnum
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
        ChangeListener<HWND?> { _, _, newValue ->
            changeTop(ConfigUtil.getBoolean(ConfigEnum.TOP_GAME_WINDOW), newValue)
        }
    }

    override fun execStart(): Boolean {
        if (ConfigUtil.getBoolean(ConfigEnum.TOP_GAME_WINDOW)) {
            gameHWNDProperty().addListener(changeListener)
            return true
        }
        return false
    }

    override fun execStop(): Boolean {
        gameHWNDProperty().removeListener(changeListener)
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
}