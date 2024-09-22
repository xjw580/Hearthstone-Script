package club.xiaojiawei.dll;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef;

/**
 * 系统相关功能
 * @author 肖嘉威
 * @date 2023/9/16 17:34
 */
@SuppressWarnings("all")
public interface SystemDll extends Library {

    SystemDll INSTANCE = Native.load("dll/system", SystemDll.class);

    void normalLeftClick(int x, int y);

    void leftClick(long x, long y, WinDef.HWND hwnd);

    void rightClick(long x, long y, WinDef.HWND hwnd);

    void moveMouse(long x, long y, WinDef.HWND hwnd);

    void closeProgram(WinDef.HWND hwnd);

    void frontWindow(WinDef.HWND hwnd);

    /**
     * 点击登录战网页的登入按钮
     * @param loginPlatformRootHWND
     */
    void clickLoginPlatformLoginBtn(WinDef.HWND loginPlatformRootHWND);

    void sendText(WinDef.HWND hwnd, String text, boolean append);

    boolean topWindow(WinDef.HWND hwnd, boolean isTop);

    boolean topWindowForTitle(String title, boolean isTop);

    boolean moveWindow(WinDef.HWND hwnd, int x, int y, int w, int h, boolean ignoreSize);

    boolean moveWindowForTitle(String title, int x, int y, int w, int h, boolean ignoreSize);

    boolean IsIconicWindow(WinDef.HWND hwnd);

    void uninstallDll(WinDef.HWND hwnd);

    /**
     * 是否禁用输入
     * @param hwnd
     * @param disable
     */
    void changeInput(WinDef.HWND hwnd, boolean disable);

    /**
     * 是否禁用窗口调整
     * @param hwnd
     * @param disable
     */
    void changeWindow(WinDef.HWND hwnd, boolean disable);

}
