package club.xiaojiawei.hsscript.service

import club.xiaojiawei.hsscript.data.gameHWNDProperty
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.utils.ConfigUtil
import club.xiaojiawei.hsscript.utils.GameUtil
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.platform.win32.WinUser.*
import javafx.beans.value.ChangeListener

/**
 * @author 肖嘉威
 * @date 2025/4/1 15:20
 */
object GameWindowOpacityService : Service<Int>() {

    private val changeListener: ChangeListener<HWND?> = ChangeListener<HWND?> { _, _, newValue ->
        changeOpacity(ConfigUtil.getInt(ConfigEnum.GAME_WINDOW_OPACITY), newValue)
    }

    override fun execStart(): Boolean {
        if (ConfigUtil.getInt(ConfigEnum.GAME_WINDOW_OPACITY) < 255) {
            gameHWNDProperty().addListener(changeListener)

        }
        return true
    }

    override fun execStop(): Boolean {
        gameHWNDProperty().removeListener(changeListener)
        return true
    }

    override fun execValueChanged(oldValue: Int, newValue: Int) {
        changeOpacity(newValue)
    }

    private fun changeOpacity(opacity: Int, gameHWND: HWND? = null) {
        (gameHWND ?: GameUtil.findGameHWND())?.let {
            val windowLong = User32.INSTANCE.GetWindowLong(it, GWL_EXSTYLE)
            if ((windowLong and WS_EX_LAYERED) == 0){
                User32.INSTANCE.SetWindowLong(it, GWL_EXSTYLE, windowLong xor WS_EX_LAYERED)
            }

            User32.INSTANCE.SetLayeredWindowAttributes(
                it,
                0,
                Math.clamp(opacity.toDouble(), 0.0, 255.0).toInt().toByte(),
                LWA_ALPHA
            )
        }
    }
}