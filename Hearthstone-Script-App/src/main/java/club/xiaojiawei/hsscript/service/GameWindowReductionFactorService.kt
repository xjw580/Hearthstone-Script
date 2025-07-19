package club.xiaojiawei.hsscript.service

import club.xiaojiawei.hsscript.consts.GameRationConst
import club.xiaojiawei.hsscript.consts.SCREEN_SCALE
import club.xiaojiawei.hsscript.dll.User32ExDll
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.enums.SCREEN_HEIGHT
import club.xiaojiawei.hsscript.listener.WorkTimeListener
import club.xiaojiawei.hsscript.status.ScriptStatus
import club.xiaojiawei.hsscript.utils.ConfigUtil
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.platform.win32.WinUser.SWP_NOMOVE
import com.sun.jna.platform.win32.WinUser.SWP_NOZORDER
import javafx.beans.value.ChangeListener

/**
 * @author 肖嘉威
 * @date 2025/4/1 15:20
 */
object GameWindowReductionFactorService : Service<Int>() {
    private val windowChangeListener: ChangeListener<HWND?> by lazy {
        ChangeListener<HWND?> { _, _, newValue ->
            if (WorkTimeListener.working) {
                changeWindowSize(newValue)
            }
        }
    }

    private val workingChangeListener: ChangeListener<Boolean> by lazy {
        ChangeListener { _, _, newValue ->
            if (newValue) {
                changeWindowSize(ScriptStatus.gameHWND)
            }
        }
    }

    override fun execStart(): Boolean {
        if (WorkTimeListener.working) {
            changeWindowSize(ScriptStatus.gameHWND)
        }
        ScriptStatus.gameHWNDProperty().addListener(windowChangeListener)
        WorkTimeListener.addChangeListener(workingChangeListener)
        return true
    }

    override fun execStop(): Boolean {
        ScriptStatus.gameHWNDProperty().removeListener(windowChangeListener)
        WorkTimeListener.removeChangeListener(workingChangeListener)
        return true
    }

    override fun execIntelligentStartStop(value: Int?): Boolean = ConfigUtil.getInt(ConfigEnum.GAME_WINDOW_REDUCTION_FACTOR) > 0

    private fun changeWindowSize(
        hwnd: HWND?,
        scale: Int = ConfigUtil.getInt(ConfigEnum.GAME_WINDOW_REDUCTION_FACTOR),
    ) {
        hwnd ?: return
        if (scale < 1) return
        val height = SCREEN_HEIGHT * SCREEN_SCALE / scale
        User32ExDll.INSTANCE.SetWindowPos(
            hwnd,
            null,
            0,
            0,
            (height * GameRationConst.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO).toInt(),
            height.toInt(),
            SWP_NOZORDER or SWP_NOMOVE,
        )
    }

    override fun execValueChanged(
        oldValue: Int,
        newValue: Int,
    ) {
        changeWindowSize(ScriptStatus.gameHWND, newValue)
    }
}
