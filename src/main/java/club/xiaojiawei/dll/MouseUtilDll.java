package club.xiaojiawei.dll;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;

/**
 * windows通知功能
 * @author 肖嘉威
 * @date 2023/9/16 17:34
 */
@SuppressWarnings("all")
public interface MouseUtilDll extends Library {

    MouseUtilDll INSTANCE = Native.load("dll/mouseUtil", MouseUtilDll.class);

    void leftClick(int x, int y, User32.HWND hwnd);

    void rightClick(int x, int y, User32.HWND hwnd);

    void moveMouse(int x, int y, WinDef.HWND hwnd);
}
