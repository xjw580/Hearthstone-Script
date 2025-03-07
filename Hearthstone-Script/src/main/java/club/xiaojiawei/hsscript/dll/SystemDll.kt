package club.xiaojiawei.hsscript.dll

import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.WString
import com.sun.jna.platform.win32.WinDef.HWND

/**
 * 系统相关功能
 * @author 肖嘉威
 * @date 2023/9/16 17:34
 */
interface SystemDll : Library {

    fun normalLeftClick(x: Int, y: Int)

    fun leftClick(x: Long, y: Long, hwnd: HWND?, mouseMode: Int)

    fun rightClick(x: Long, y: Long, hwnd: HWND?, mouseMode: Int)

    fun moveMouse(x: Long, y: Long, hwnd: HWND?)

    fun simulateHumanMove(
        startX: Int,
        startY: Int,
        endX: Int,
        endY: Int,
        hwnd: HWND?,
        pauseStep: Int,
        mouseMode: Int,
    )

    fun closeProgram(hwnd: HWND?)

    fun frontWindow(hwnd: HWND?)

    /**
     * 点击登录战网页的登入按钮
     * @param loginPlatformRootHWND
     */
    fun clickLoginPlatformLoginBtn(loginPlatformRootHWND: HWND?)

    fun sendText(hwnd: HWND?, text: String?, append: Boolean)

    fun topWindow(hwnd: HWND?, isTop: Boolean): Boolean

    fun topWindowForTitle(title: String?, isTop: Boolean): Boolean

    fun moveWindow(hwnd: HWND?, x: Int, y: Int, w: Int, h: Int, ignoreSize: Boolean): Boolean

    fun moveWindowForTitle(title: String?, x: Int, y: Int, w: Int, h: Int, ignoreSize: Boolean): Boolean

    fun IsIconicWindow(hwnd: HWND?): Boolean

    fun uninstallDll(hwnd: HWND?)

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

    fun IsRunAsAdministrator(): Boolean

    fun MessageBox_(hwnd: HWND?, text: String?, title: String?, type: Int)

    fun FindWindowW_(className: WString?, windowName: WString?): HWND?

    fun FindWindowsByProcessName(processName: String?): HWND?

    fun FindProcessId_(processName: String?): Long

    fun GetWindowsProxy(proxyUrl: Pointer, length: Int)

    fun CheckS3Support(): Boolean

    fun EnableWakeUpTimer(): Boolean

    fun SetWakeUpTimer(seconds: Int): Boolean

    /**
     * 睡眠系统
     */
    fun SleepSystem();

    companion object {

        val INSTANCE: SystemDll = Native.load("dll/system", SystemDll::class.java)

        const val MB_ICONERROR: Int = 0x00000010

        const val MB_TOPMOST: Int = 0x00040000

        const val MB_OK: Int = 0x00000000

        const val MB_ICONINFORMATION: Int = 0x00000040
    }
}