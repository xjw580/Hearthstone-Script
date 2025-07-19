package club.xiaojiawei.hsscript.dll

import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.WString

/**
 * @author 肖嘉威
 * @date 2023/9/16 17:34
 */
@Deprecated("")
interface ZLaunchDll : Library {
    fun ShowPage(
        bgImgPath: WString?,
        programImgPath: WString?,
        text: WString?,
        version: WString?,
        width: Int,
        height: Int
    )

    fun HidePage()

    fun SetProgress(value: Double)

    fun SetText(text: WString?)

    companion object {
        val INSTANCE: ZLaunchDll by lazy {
            Native.load("dll/zlaunch", ZLaunchDll::class.java)
        }
    }
}
