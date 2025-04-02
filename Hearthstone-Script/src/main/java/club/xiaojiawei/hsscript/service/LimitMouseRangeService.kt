package club.xiaojiawei.hsscript.service

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
object LimitMouseRangeService : Service<Boolean>() {

    private val changeListener: ChangeListener<HWND?> by lazy {
        ChangeListener<HWND?> { _, _, newValue ->
            limitMouse(ConfigUtil.getBoolean(ConfigEnum.LIMIT_MOUSE_RANGE), newValue)
        }
    }

    override fun execStart(): Boolean {
        if (ConfigUtil.getBoolean(ConfigEnum.LIMIT_MOUSE_RANGE)) {
            gameHWNDProperty().addListener(changeListener)
        }
        return true
    }

    override fun execStop(): Boolean {
        gameHWNDProperty().removeListener(changeListener)
        return true
    }

    override fun execValueChanged(oldValue: Boolean, newValue: Boolean) {
        limitMouse(newValue)
    }

    private fun limitMouse(limit: Boolean, gameHWND: HWND? = null) {
        (gameHWND ?: GameUtil.findGameHWND())?.let {
            CSystemDll.INSTANCE.limitMouseRange(limit)
        }
    }


}