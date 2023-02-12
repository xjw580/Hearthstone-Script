package club.xiaojiawei.utils;

import club.xiaojiawei.listener.PowerFileListener;
import club.xiaojiawei.listener.ScreenFileListener;
import club.xiaojiawei.run.Core;
import club.xiaojiawei.strategy.mode.LoginAbstractModeStrategy;
import club.xiaojiawei.strategy.mode.TournamentAbstractModeStrategy;
import club.xiaojiawei.strategy.phase.GameOverAbstractPhaseStrategy;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.geom.AffineTransform;

import static club.xiaojiawei.constant.SystemConst.ROBOT;
import static club.xiaojiawei.constant.SystemConst.SCREEN_HEIGHT;

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
        SystemTray tray = SystemTray.getSystemTray();
        Image image = Toolkit.getDefaultToolkit().createImage("D:\\images\\normal\\icon\\youku.ico");
        TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("System tray icon demo");
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
        trayIcon.displayMessage(title, context, TrayIcon.MessageType.INFO);
    }

    public static void notice(String context){
        notice(Core.getScriptName(), context);
    }

    private static final int TITLE_BAR_HEIGHT = MouseUtil.pixelToPosY(33);
    /**
     * 获取窗口信息
     * @param hwnd
     * @return
     */
    public static WinDef.RECT getRect(WinDef.HWND hwnd) {
        WinDef.RECT winRect = new WinDef.RECT();
        User32.INSTANCE.GetWindowRect(hwnd, winRect);
        if ((winRect.bottom - winRect.top) != SCREEN_HEIGHT){
            winRect.top += TITLE_BAR_HEIGHT;
        }
        return winRect;
    }

    /**
     * 获取窗口
     * @param windowTitle
     * @return
     */
    @SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
    public static WinDef.HWND getHWND(String windowTitle){
        return User32.INSTANCE.FindWindow
                (null, windowTitle);
    }

    /**
     * 前置窗口
     * @param hwnd
     */
    public static void frontWindow(WinDef.HWND hwnd){
        if (Core.getPause()){
            return;
        }
        //noinspection AlibabaUndefineMagicConstant
        for (int i = 0; i < 2; i++) {
            // 显示窗口
            User32.INSTANCE.ShowWindow(hwnd, 9 );
            // 前置窗口
            User32.INSTANCE.SetForegroundWindow(hwnd);
            ROBOT.delay(200);
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

    public static void killProgram(){
        ROBOT.keyPress(18);
        ROBOT.keyPress(115);
        ROBOT.keyRelease(115);
        ROBOT.keyRelease(18);
        log.info("已关闭游戏");
    }

    public static void shutdownGame(){
        log.info("发生错误，准备关闭游戏");
        SystemUtil.notice("发生错误，准备关闭游戏");
        cancelAllTask();
        PowerFileListener.setMark(System.currentTimeMillis());
        SystemUtil.frontWindow(Core.getGameHWND());
        ROBOT.delay(2000);
        SystemUtil.killProgram();
    }

    public static void cancelAllTask(){
        LoginAbstractModeStrategy.cancelTimer();
        TournamentAbstractModeStrategy.cancelTimer();
        GameOverAbstractPhaseStrategy.cancelTimer();
    }

    public static void reStart(){
        if (Core.getPause()){
            return;
        }
        synchronized (SystemUtil.class){
            if (Core.getPause()){
                return;
            }
            Core.setPause(true);
        }
        try{
            ROBOT.delay(6000);
            ScreenFileListener.reset();
            PowerFileListener.reset();
            Core.openGame();
        }finally {
            Core.setPause(false);
        }
    }

    /**
     * 获取系统托盘
     */
    public static final SystemTray TRAY = SystemTray.getSystemTray();

    public static TrayIcon trayIcon;

    /**
     * 添加托盘
     * @param trayIconName
     * @param trayName
     * @param menuItems
     */
    public static void addTray(String trayIconName, String trayName, MenuItem... menuItems){
        Image image = Toolkit.getDefaultToolkit().getImage(SystemUtil.class.getResource("/club/xiaojiawei/images/" + trayIconName));
//        托盘右键弹出菜单
        PopupMenu popupMenu = new PopupMenu();
        for (MenuItem menuItem : menuItems) {
            popupMenu.add(menuItem);
        }
//        托盘图标
        trayIcon = new TrayIcon(image, trayName, popupMenu);
        trayIcon.setImageAutoSize(true);
        try {
            TRAY.add(trayIcon);
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    public static void removeTray(){
        TRAY.remove(trayIcon);
    }

    public static void printScreenMsg() {
        Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = defaultToolkit.getScreenSize();
        double width2 = screenSize.getWidth();
        double height2 = screenSize.getHeight();
        log.info("width2:" + width2);
        log.info("height2:" + height2);
        GraphicsDevice graphDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        DisplayMode disMode = graphDevice.getDisplayMode();
        int width = disMode.getWidth();
        int height = disMode.getHeight();
        log.info("width:" + width);
        log.info("height:" + height);
        GraphicsConfiguration gc = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice().
                getDefaultConfiguration();
        AffineTransform tx = gc.getDefaultTransform();
        double uiScaleX = tx.getScaleX();
        double uiScaleY = tx.getScaleY();
        log.info("uiScaleX:" + uiScaleX);
        log.info("uiScaleY:" + uiScaleY);
    }

}
