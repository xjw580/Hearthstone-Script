package club.xiaojiawei.hearthstone.utils;

import com.sun.jna.platform.win32.BaseTSD;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;

/**
 * @author 肖嘉威
 * @date 2022/11/24 17:23
 */
public class KeyBoardUtil {

    private final static WinUser.INPUT INPUT = new WinUser.INPUT();

    /**
     * 输入
     * @param ch
     */
    public static void  sendChar(char ch){

        INPUT.type = new WinDef.DWORD( WinUser.INPUT.INPUT_KEYBOARD );
        INPUT.input.setType("ki"); // Because setting INPUT_INPUT_KEYBOARD is not enough: https://groups.google.com/d/msg/jna-users/NDBGwC1VZbU/cjYCQ1CjBwAJ
        INPUT.input.ki.wScan = new WinDef.WORD( 0 );
        INPUT.input.ki.time = new WinDef.DWORD( 0 );
        INPUT.input.ki.dwExtraInfo = new BaseTSD.ULONG_PTR( 0 );
        // Press
        INPUT.input.ki.wVk = new WinDef.WORD( Character.toUpperCase(ch) ); // 0x41
        INPUT.input.ki.dwFlags = new WinDef.DWORD( 0 );  // keydown

        User32.INSTANCE.SendInput( new WinDef.DWORD( 1 ), ( WinUser.INPUT[] ) INPUT.toArray( 1 ), INPUT.size() );

        // Release
        INPUT.input.ki.wVk = new WinDef.WORD( Character.toUpperCase(ch) ); // 0x41
        INPUT.input.ki.dwFlags = new WinDef.DWORD( 2 );  // keyup

        User32.INSTANCE.SendInput( new WinDef.DWORD( 1 ), ( WinUser.INPUT[] ) INPUT.toArray( 1 ), INPUT.size() );

    }

}
