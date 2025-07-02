package club.xiaojiawei.hsscript.status

import club.xiaojiawei.config.log
import club.xiaojiawei.hsscript.consts.GAME_CN_NAME
import club.xiaojiawei.hsscript.consts.PLATFORM_CN_NAME
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.utils.ConfigUtil
import club.xiaojiawei.hsscript.utils.GameUtil
import club.xiaojiawei.hsscript.utils.go
import com.sun.jna.platform.win32.User32
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
     * 仅供测试使用
     */
    var testMode = false

    /**
     * 炉石安装路径是否有效
     */
    var isValidGameInstallPath = true

    /**
     * 战网程序路径是否有效
     */
    var isValidPlatformProgramPath = true

    private val gameHWNDInner = ReadOnlyObjectWrapper<HWND?>(null)

    /**
     * 游戏窗口句柄
     */
    var gameHWND: HWND?
        set(value) = gameHWNDInner.set(value)
        get() {
            var hWND = gameHWNDInner.get()
            if (!User32.INSTANCE.IsWindow(hWND)) {
                if (hWND != null) {
                    log.info { "${GAME_CN_NAME}窗口句柄已经失效，尝试更新句柄" }
                }
                hWND = GameUtil.findGameHWND()
                go {
                    gameHWNDInner.set(hWND)
                }
            }
            return hWND
        }

    fun gameHWNDReadOnlyProperty(): ReadOnlyObjectProperty<HWND?> = gameHWNDInner.readOnlyProperty

    fun gameHWNDProperty(): ObjectProperty<HWND?> = gameHWNDInner

    private val platformHWNDInner = ReadOnlyObjectWrapper<HWND?>(null)

    /**
     * 战网窗口句柄
     */
    var platformHWND: HWND?
        set(value) = platformHWNDInner.set(value)
        get() {
            var hWND = platformHWNDInner.get()
            if (!User32.INSTANCE.IsWindow(hWND)) {
                if (hWND != null) {
                    log.info { "${PLATFORM_CN_NAME}窗口句柄已经失效，尝试更新句柄" }
                }
                hWND = GameUtil.findPlatformHWND()
                go {
                    platformHWNDInner.set(hWND)
                }
            }
            return hWND
        }

    fun platformHWNDReadOnlyProperty(): ReadOnlyObjectProperty<HWND?> = platformHWNDInner.readOnlyProperty

    fun platformHWNDProperty(): ObjectProperty<HWND?> = platformHWNDInner

    /**
     * 游戏窗口信息
     */
    val GAME_RECT: WinDef.RECT = WinDef.RECT()

    var maxLogSizeKB: Int = ConfigUtil.getInt(ConfigEnum.GAME_LOG_LIMIT)

    var maxLogSizeB: Int = maxLogSizeKB * 1024

    fun reloadLogSize(newMaxLogSizeKB: Int = ConfigUtil.getInt(ConfigEnum.GAME_LOG_LIMIT)) {
        maxLogSizeKB = newMaxLogSizeKB
        maxLogSizeB = maxLogSizeKB * 1024
    }
}
