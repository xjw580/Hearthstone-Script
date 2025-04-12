package club.xiaojiawei.hsscript.service

import club.xiaojiawei.hsscript.controller.javafx.GameWindowModalController
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.enums.WindowEnum
import club.xiaojiawei.hsscript.status.ScriptStatus
import club.xiaojiawei.hsscript.utils.ConfigUtil
import club.xiaojiawei.hsscript.utils.WindowUtil
import com.sun.jna.platform.win32.WinDef.HWND
import javafx.beans.value.ChangeListener

/**
 * @author 肖嘉威
 * @date 2025/3/24 17:21
 */
object DisplayGameRectPosService : Service<Boolean>() {
    private val hwndListener: ChangeListener<HWND?> by lazy {
        ChangeListener<HWND?> { _, _, newValue ->
            show()
        }
    }

    private fun show() {
        WindowUtil.showStage(WindowEnum.GAME_WINDOW_CONTROL_MODAL)
        val controller = WindowUtil.getController(WindowEnum.GAME_WINDOW_CONTROL_MODAL)
        if (controller is GameWindowModalController) {
            controller.setOpacity(0.0)
        }
    }

    override fun execStart(): Boolean {
        ScriptStatus.gameHWNDProperty().addListener(hwndListener)
        return true
    }

    override fun execStop(): Boolean {
        WindowUtil.hideStage(WindowEnum.GAME_WINDOW_CONTROL_MODAL)
        return true
    }

    override fun execIntelligentStartStop(value: Boolean?): Boolean = ConfigUtil.getBoolean(ConfigEnum.DISPLAY_GAME_RECT_POS)
}
