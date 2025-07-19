package club.xiaojiawei.hsscript.service

import club.xiaojiawei.hsscript.consts.INJECT_UTIL_FILE
import club.xiaojiawei.hsscript.consts.LIB_BN_FILE
import club.xiaojiawei.hsscript.consts.PLATFORM_US_NAME
import club.xiaojiawei.hsscript.consts.SCREEN_SCALE
import club.xiaojiawei.hsscript.dll.User32ExDll
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.enums.SCREEN_HEIGHT
import club.xiaojiawei.hsscript.listener.WorkTimeListener
import club.xiaojiawei.hsscript.status.ScriptStatus
import club.xiaojiawei.hsscript.utils.ConfigUtil
import club.xiaojiawei.hsscript.utils.InjectUtil
import club.xiaojiawei.hsscript.utils.SystemUtil
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.platform.win32.WinUser.SWP_NOMOVE
import com.sun.jna.platform.win32.WinUser.SWP_NOZORDER
import javafx.beans.value.ChangeListener

/**
 * @author 肖嘉威
 * @date 2025/4/1 15:20
 */
object PlatformWindowReductionFactorService : Service<Int>() {
    private val windowChangeListener: ChangeListener<HWND?> by lazy {
        ChangeListener<HWND?> { _, _, newValue ->
            inject()
            if (WorkTimeListener.working) {
                changeWindowSize(newValue)
            }
        }
    }

    private val workingChangeListener: ChangeListener<Boolean> by lazy {
        ChangeListener { _, _, newValue ->
            if (newValue) {
                changeWindowSize(ScriptStatus.platformHWND)
            }
        }
    }

    override fun execStart(): Boolean {
        inject()
        if (WorkTimeListener.working) {
            changeWindowSize(ScriptStatus.platformHWND)
        }
        ScriptStatus.platformHWNDProperty().addListener(windowChangeListener)
        WorkTimeListener.addChangeListener(workingChangeListener)
        return true
    }

    override fun execStop(): Boolean {
        ScriptStatus.platformHWNDProperty().removeListener(windowChangeListener)
        WorkTimeListener.removeChangeListener(workingChangeListener)
        return true
    }

    override fun execIntelligentStartStop(value: Int?): Boolean = ConfigUtil.getInt(ConfigEnum.PLATFORM_WINDOW_REDUCTION_FACTOR) > 0

    private fun inject(): Boolean {
        if (User32.INSTANCE.IsWindow(ScriptStatus.platformHWND)) {
            val injectFile = SystemUtil.getExeFilePath(INJECT_UTIL_FILE) ?: return false
            val dllFile = SystemUtil.getDllFilePath(LIB_BN_FILE) ?: return false
            return InjectUtil.execInject(injectFile, dllFile, "$PLATFORM_US_NAME.exe")
        }
        return false
    }

    private fun changeWindowSize(
        hwnd: HWND?,
        scale: Int = ConfigUtil.getInt(ConfigEnum.PLATFORM_WINDOW_REDUCTION_FACTOR),
    ) {
        hwnd ?: return
        if (scale < 1) return
        val height = SCREEN_HEIGHT * SCREEN_SCALE / scale
        User32ExDll.INSTANCE.SetWindowPos(
            hwnd,
            null,
            0,
            0,
            height.toInt(),
            height.toInt(),
            SWP_NOZORDER or SWP_NOMOVE,
        )
    }

    override fun execValueChanged(
        oldValue: Int,
        newValue: Int,
    ) {
        changeWindowSize(ScriptStatus.platformHWND, newValue)
    }
}
