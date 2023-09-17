package club.xiaojiawei.custom.dll;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef;

/**
 * @author 肖嘉威
 * @date 2023/9/16 17:34
 * @msg
 */
public interface User32Dll extends Library {

    User32Dll INSTANCE = Native.load("dll/libuser32.dll", User32Dll.class);
    void leftClick(WinDef.HWND hwnd, int x, int y);
    void rightClick(WinDef.HWND hwnd, int x, int y);
    void leftDown(WinDef.HWND hwnd, int x, int y);
    void leftUp(WinDef.HWND hwnd, int x, int y);
    void rightDown(WinDef.HWND hwnd, int x, int y);
    void rightUp(WinDef.HWND hwnd, int x, int y);
//    void move(WinDef.HWND hwnd, int startX, int startY, int endX, int endY);
}
