package club.xiaojiawei.hsscript.status

import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.utils.ConfigUtil
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinDef.HWND
import javafx.beans.property.ObjectProperty
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.ReadOnlyObjectWrapper

/**
 * @author 肖嘉威
 * @date 2025/4/3 14:25
 */
object ScriptStatus {

    /**
     * 是否设置了炉石和战网的路径
     */
    var isValidProgramPath = true

    private val gameHWNDInner = ReadOnlyObjectWrapper<HWND?>(null)

    /**
     * 游戏窗口句柄
     */
    var gameHWND: HWND?
        set(value) = gameHWNDInner.set(value)
        get() = gameHWNDInner.get()

    fun gameHWNDReadOnlyProperty(): ReadOnlyObjectProperty<HWND?> = gameHWNDInner.readOnlyProperty

    fun gameHWNDProperty(): ObjectProperty<HWND?> = gameHWNDInner

    private val platformHWNDInner = ReadOnlyObjectWrapper<HWND?>(null)

    var platformHWND: HWND?
        set(value) = platformHWNDInner.set(value)
        get() = platformHWNDInner.get()

    fun platformHWNDReadOnlyProperty(): ReadOnlyObjectProperty<HWND?> = platformHWNDInner.readOnlyProperty

    fun platformHWNDProperty(): ObjectProperty<HWND?> = platformHWNDInner

    /**
     * 游戏窗口信息
     */
    val GAME_RECT: WinDef.RECT = WinDef.RECT()

    var maxLogSizeKB: Int = ConfigUtil.getInt(ConfigEnum.GAME_LOG_LIMIT)

    var maxLogSizeB: Int = maxLogSizeKB * 1024

    fun reloadLogSize() {
        maxLogSizeKB = ConfigUtil.getInt(ConfigEnum.GAME_LOG_LIMIT)
        maxLogSizeB = maxLogSizeKB * 1024
    }

}