package club.xiaojiawei.hsscript.dll;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * windows通知功能
 * @author 肖嘉威
 * @date 2023/9/16 17:34
 */
@SuppressWarnings("all")
public interface NoticeDll extends Library {

    NoticeDll INSTANCE = Native.load("dll/libnotice", NoticeDll.class);

    void notice(
            byte[] appID, byte[] title, byte[] msg, byte[] icoPath, byte[] btnText, byte[] btnURL,
            int appIDLne, int titleLen, int msgLen, int icoPathLen, int btnTextLen, int btnURLLen
    );
}
