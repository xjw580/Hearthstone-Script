package club.xiaojiawei.hsscript.dll

import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.WString
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinDef.POINT

/**
 * @author 肖嘉威
 * @date 2025/3/7 16:55
 */
interface User32ExDll : Library {

    fun IsIconic(hwnd: WinDef.HWND?): Boolean

    fun ClientToScreen(hWnd: WinDef.HWND?, point: POINT)

    fun FindWindowW(className: WString? = null, windowTitle: WString?): WinDef.HWND?

    companion object {

        val INSTANCE: User32ExDll by lazy {
            Native.load("user32", User32ExDll::class.java)
        }

        const val SC_MONITORPOWER: Long = 0xF170

        const val WM_NULL: Int = 0x0000

    }

}

fun main() {
    User32.INSTANCE.LockWorkStation()
}