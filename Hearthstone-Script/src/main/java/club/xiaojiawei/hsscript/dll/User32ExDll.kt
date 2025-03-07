package club.xiaojiawei.hsscript.dll

import com.sun.jna.Native

/**
 * @author 肖嘉威
 * @date 2025/3/7 16:55
 */
interface User32ExDll {

    companion object {

        val INSTANCE: ZLaunchDll by lazy {
            Native.load("user32", ZLaunchDll::class.java)
        }

        val SC_MONITORPOWER: Long = 0xF170

    }
}