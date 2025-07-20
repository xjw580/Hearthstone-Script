package club.xiaojiawei.hsscript.dll

import com.sun.jna.Library
import com.sun.jna.Native

/**
 * windows通知功能
 * @author 肖嘉威
 * @date 2023/9/16 17:34
 */
interface GSystemDll : Library {

    fun notice(
        appID: ByteArray?,
        title: ByteArray?,
        msg: ByteArray?,
        icoPath: ByteArray?,
        btnText: ByteArray?,
        btnURL: ByteArray?,
        appIDLne: Int,
        titleLen: Int,
        msgLen: Int,
        icoPathLen: Int,
        btnTextLen: Int,
        btnURLLen: Int
    )

    companion object {
        val INSTANCE: GSystemDll by lazy {
            Native.load("dll/gsystem", GSystemDll::class.java)
        }
    }
}
