package club.xiaojiawei.dll;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef;

/**
 * 加载自定义的dll
 * @author 肖嘉威
 * @date 2023/9/16 17:34
 */
@SuppressWarnings("all")
public interface SystemDll extends Library {

    SystemDll INSTANCE = Native.load("dll/libsystem", SystemDll.class);

    void normalLeftMouseClick(int x, int y);

    void leftMouseClick(WinDef.HWND hwnd, int x, int y);

    void rightMouseClick(WinDef.HWND hwnd, int x, int y);

    void leftMouseDown(WinDef.HWND hwnd, int x, int y);

    void leftMouseUp(WinDef.HWND hwnd, int x, int y);

    void rightMouseDown(WinDef.HWND hwnd, int x, int y);

    void rightMouseUp(WinDef.HWND hwnd, int x, int y);

    void closeProgram(WinDef.HWND hwnd);

    //void moveMouse(WinDef.HWND hwnd, int startX, int startY, int endX, int endY);

    void frontWindow(WinDef.HWND hwnd);

    /**
     * 点击登录战网页的登入按钮
     * @param loginPlatformRootHWND
     */
    void clickLoginPlatformLoginBtn(WinDef.HWND loginPlatformRootHWND);

    void sendText(WinDef.HWND hwnd, String text, boolean append);
}
