package club.xiaojiawei.hearthstone.utils;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;

import static club.xiaojiawei.hearthstone.constant.SystemConst.ROBOT;

/**
 * @author 肖嘉威
 * @date 2022/11/24 17:21
 */
@Slf4j
public class SystemUtil {

    /**
     * 调用系统通知
     * @param title
     * @param context
     * @throws AWTException
     */
    public static void notice(String title, String context){
        //Obtain only one instance of the SystemTray object
        SystemTray tray = SystemTray.getSystemTray();

        //If the icon is a file
        Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
        //Alternative (if the icon is on the classpath):
        //Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("icon.png"));

        TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");
        //Let the system resize the image if needed
        trayIcon.setImageAutoSize(true);
        //Set tooltip text for the tray icon
        trayIcon.setToolTip("System tray icon demo");
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
        trayIcon.displayMessage(title, context, TrayIcon.MessageType.INFO);
    }

    /**
     * 获取窗口尺寸信息
     * @param hwnd
     * @return
     */
    public static WinDef.RECT getRect(WinDef.HWND hwnd) {
        WinDef.RECT winRect = new WinDef.RECT();
        User32.INSTANCE.GetWindowRect(hwnd, winRect);
        return winRect;
    }

    /**
     * 获取窗口
     * @param windowTitle
     * @return
     */
    public static WinDef.HWND getHWND(String windowTitle){
        return User32.INSTANCE.FindWindow
                (null, windowTitle);
    }

    /**
     * 前置窗口
     * @param hwnd
     */
    public static void frontWindow(WinDef.HWND hwnd){
        for (int i = 0; i < 2; i++) {
            User32.INSTANCE.ShowWindow(hwnd, 9 );        // 显示窗口
            User32.INSTANCE.SetForegroundWindow(hwnd);   // 前置窗口
            ROBOT.delay(300);
        }
    }

    public static void delayShort(){
        ROBOT.delay(RandomUtil.getShortRandom());
    }

    public static void delayMedium(){
        ROBOT.delay(RandomUtil.getMediumRandom());
    }

    public static void delayLong(){
        ROBOT.delay(RandomUtil.getLongRandom());
    }

}
