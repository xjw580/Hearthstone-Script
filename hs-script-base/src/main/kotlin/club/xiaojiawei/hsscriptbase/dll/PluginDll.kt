package club.xiaojiawei.hsscriptbase.dll

import com.sun.jna.Library
import com.sun.jna.Native

/**
 * @author 肖嘉威
 * @date 2025/3/7 16:55
 */
@Suppress("ktlint:standard:function-naming")
interface PluginDll : Library {

    fun load(type: String)

    companion object {
        val INSTANCE: PluginDll by lazy {
            Native.load(
                "dll/plugin",
                PluginDll::class.java
            )
        }
    }
}