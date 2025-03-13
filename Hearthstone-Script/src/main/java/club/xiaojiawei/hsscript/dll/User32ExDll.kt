package club.xiaojiawei.hsscript.dll

import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinDef.POINT

/**
 * @author 肖嘉威
 * @date 2025/3/7 16:55
 */
interface User32ExDll : Library {

    fun IsIconic(hwnd: WinDef.HWND): Boolean

    fun GetCursorPos(point: POINT)

    companion object {

        val INSTANCE: User32ExDll by lazy {
            Native.load("user32", User32ExDll::class.java)
        }

        val SC_MONITORPOWER: Long = 0xF170

    }

}