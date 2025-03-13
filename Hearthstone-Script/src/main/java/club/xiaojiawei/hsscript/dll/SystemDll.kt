package club.xiaojiawei.hsscript.dll

import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.platform.win32.WinDef.HWND

/**
 * 系统相关功能
 * @author 肖嘉威
 * @date 2023/9/16 17:34
 */
interface SystemDll : Library {

    fun leftClick(x: Long, y: Long, hwnd: HWND?, mouseMode: Int)

    fun rightClick(x: Long, y: Long, hwnd: HWND?, mouseMode: Int)

    fun simulateHumanMoveMouse(
        startX: Int,
        startY: Int,
        endX: Int,
        endY: Int,
        hwnd: HWND?,
        pauseStep: Int,
        mouseMode: Int,
    )

    fun quitWindow(hwnd: HWND?)

    fun frontWindow(hwnd: HWND?)

    fun clickPlatformLoginBtn(loginPlatformHWND: HWND?)

    fun sendText(hwnd: HWND?, text: String?, append: Boolean)

    fun topWindow(hwnd: HWND?, isTop: Boolean): Boolean

    fun topWindowForTitle(title: String?, isTop: Boolean): Boolean

    fun moveWindow(hwnd: HWND?, x: Int, y: Int, w: Int, h: Int, ignoreSize: Boolean): Boolean

    fun moveWindowForTitle(title: String?, x: Int, y: Int, w: Int, h: Int, ignoreSize: Boolean): Boolean

     fun uninstallInjectDll(hwnd: HWND?)

    /**
     * 是否禁用输入
     * @param hwnd
     * @param disable
     */
    fun changeInput(hwnd: HWND?, disable: Boolean)

    /**
     * 是否禁用窗口调整
     * @param hwnd
     * @param disable
     */
    fun changeWindow(hwnd: HWND?, disable: Boolean)

    fun isRunAsAdministrator(): Boolean

    fun messageBox(hwnd: HWND?, text: String?, title: String?, type: Int)

    fun findWindowsByProcessName(processName: String?): HWND?

    fun findProcessId(processName: String?): Long

    fun getWindowsProxy(proxyUrl: Pointer, length: Int)

    fun checkS3Support(): Boolean

    fun enableWakeUpTimer(): Boolean

    fun setWakeUpTimer(seconds: Int): Boolean

    /**
     * 睡眠系统
     */
    fun sleepSystem()

    fun killProcessByName(processName: String)

    fun isProcessRunning(processName: String): Boolean

    companion object {

        val INSTANCE: SystemDll by lazy {
            Native.load("dll/system", SystemDll::class.java)
        }

        const val MB_ICONERROR: Int = 0x00000010

        const val MB_TOPMOST: Int = 0x00040000

        const val MB_OK: Int = 0x00000000

        const val MB_ICONINFORMATION: Int = 0x00000040
    }
}