package club.xiaojiawei.utils;

import club.xiaojiawei.custom.MouseClickListener;
import club.xiaojiawei.custom.dll.User32Dll;
import club.xiaojiawei.enums.RegCommonNameEnum;
import club.xiaojiawei.listener.log.DeckLogListener;
import club.xiaojiawei.listener.log.PowerLogListener;
import club.xiaojiawei.listener.log.ScreenLogListener;
import club.xiaojiawei.starter.GameStarter;
import club.xiaojiawei.starter.LoginPlatformStarter;
import club.xiaojiawei.starter.PlatformStarter;
import club.xiaojiawei.strategy.mode.LoginModeStrategy;
import club.xiaojiawei.strategy.mode.TournamentModeStrategy;
import club.xiaojiawei.strategy.phase.GameTurnPhaseStrategy;
import club.xiaojiawei.strategy.phase.ReplaceCardPhaseStrategy;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinReg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.function.Consumer;

import static club.xiaojiawei.data.ScriptStaticData.*;

/**
 * 系统工具类
 * @author 肖嘉威
 * @date 2022/11/24 17:21
 */
@Slf4j
@Component
public class SystemUtil {
    private static ScreenLogListener screenLogListener;
    private static PowerLogListener powerLogListener;
    private static DeckLogListener deckLogListener;
    @Autowired
    public void setScreenLogListener(ScreenLogListener screenLogListener, PowerLogListener powerLogListener, DeckLogListener deckLogListener) {
        SystemUtil.screenLogListener = screenLogListener;
        SystemUtil.powerLogListener = powerLogListener;
        SystemUtil.deckLogListener = deckLogListener;
    }

    public final static Clipboard CLIPBOARD = Toolkit.getDefaultToolkit().getSystemClipboard();
    /**
     * 系统托盘
     */
    public static final SystemTray TRAY = SystemTray.getSystemTray();

    public static TrayIcon trayIcon;

    /**
     * 调用系统通知
     * @param title
     * @param content
     */
    public static void notice(String title, String content){
        trayIcon.displayMessage(title, content, TrayIcon.MessageType.INFO);
    }

    public static void cancelAllTask(){
        log.info("终止所有模式任务");
        GameUtil.cancelTask();
        LoginModeStrategy.cancelTask();
        TournamentModeStrategy.cancelTask();
    }

    public static void cancelAllListener(){
        log.info("终止所有监听器");
        screenLogListener.cancelListener();
        powerLogListener.cancelListener();
        deckLogListener.cancelListener();
    }

    public static void stopAllThread(){
        log.info("终止所有额外线程");
        ReplaceCardPhaseStrategy.stopThread();
        GameTurnPhaseStrategy.stopThread();
    }

    public static void cancelAllProgramTimer(){
        log.info("终止所有程序启动定时器");
        PlatformStarter.cancelPlatformTimer();
        LoginPlatformStarter.cancelLoginPlatformTimer();
        GameStarter.cancelGameTimer();
    }

    public static void cancelAllRunnable(){
        stopAllThread();
        cancelAllTask();
        cancelAllProgramTimer();
        cancelAllListener();
        delay(1000);
    }
    public static void notice(String context){
        notice(SCRIPT_NAME, context);
    }

    /**
     * 获取窗口
     * @param windowTitle
     * @return
     */
    public static WinDef.HWND findHWND(String windowTitle){
        return User32.INSTANCE.FindWindow
                (null, windowTitle);
    }

    /**
     * 更新窗口信息
     */
    public static void updateRECT(WinDef.HWND programHWND, WinDef.RECT programRECT) {
//        如果程序最小化无法获取到准确的窗口信息
        frontWindow(programHWND);
        User32.INSTANCE.GetWindowRect(programHWND, programRECT);
        if ((GAME_RECT.bottom - GAME_RECT.top) != DISPLAY_PIXEL_Y){
            GAME_RECT.top += WINDOW_TITLE_PIXEL_Y;
        }
    }

    /**
     * 更新游戏窗口信息
     */
    public static void updateGameRect(){
        if (getGameHWND() == null){
            findGameHWND();
        }
        updateRECT(getGameHWND(), GAME_RECT);
    }


    public static WinDef.HWND findGameHWND(){
        setGameHWND(SystemUtil.findHWND(GAME_CN_NAME));
        return getGameHWND();
    }

    public static WinDef.HWND findPlatformHWND(){
        return SystemUtil.findHWND(PLATFORM_CN_NAME);
    }

    public static WinDef.HWND findLoginPlatformHWND(){
        return SystemUtil.findHWND(PLATFORM_LOGIN_CN_NAME);
    }

    public static void deleteAllContent(){
        ROBOT.keyPress(KeyEvent.VK_CONTROL);
        sendKey(KeyEvent.VK_A);
        ROBOT.keyRelease(KeyEvent.VK_CONTROL);
        delay(200);
        sendKey(KeyEvent.VK_DELETE);
    }
    public static void pasteFromClipboard(){
        ROBOT.keyPress(KeyEvent.VK_CONTROL);
        sendKey(KeyEvent.VK_V);
        ROBOT.keyRelease(KeyEvent.VK_CONTROL);
    }
    public static void sendKey(int keyCode) {
        ROBOT.keyPress(keyCode);
        ROBOT.keyRelease(keyCode);
    }

        /**
         * 前置窗口
         * @param programHWND
         */
    public static void frontWindow(WinDef.HWND programHWND){
        // 显示窗口
        User32.INSTANCE.ShowWindow(programHWND, 9 );
        delay(100);
        // 前置窗口
        User32.INSTANCE.SetForegroundWindow(programHWND);
        delay(100);
    }
    public static final Desktop DESKTOP = Desktop.getDesktop();
    /**
     * 通过浏览器打开链接
     * @param url
     */
    public static void openUrlByBrowser(String url){
        // 判断桌面是否支持浏览器调用
        if (DESKTOP.isSupported(Desktop.Action.BROWSE)) {
            // 调用默认浏览器打开网页
            try {
                DESKTOP.browse(new URI(url));
            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 单位毫秒
     * @param delay
     */
    public static void delay(int delay){
        ROBOT.delay(delay);
    }
    public static void delayHuman(){
        delay(RandomUtil.getHugeRandom());
    }
    public static void delayTiny(){
        delay(RandomUtil.getTinyRandom());
    }
    public static void delayShort(){
        delay(RandomUtil.getShortRandom());
    }
    public static void delayMedium(){
        delay(RandomUtil.getMediumRandom());
    }
    public static void delayLong(){
        delay(RandomUtil.getLongRandom());
    }
    public static void delayHuge(){
        delay(RandomUtil.getHugeRandom());
    }

    @Deprecated
    public  static void killProgram(WinDef.HWND programHWND){
        frontWindow(programHWND);
        ROBOT.keyPress(18);
        ROBOT.keyPress(115);
        ROBOT.keyRelease(115);
        ROBOT.keyRelease(18);
        log.info("已关闭程序");
    }

    /**
     * 通过此方式停止的游戏，screen.log监听器可能无法监测到游戏被关闭
     */
    public static void killGame(){
        WinDef.HWND gameHWND = findGameHWND();
        if (gameHWND != null){
            try {
                Runtime.getRuntime().exec("cmd /c taskkill /f /t /im " + GAME_PROGRAM_NAME).waitFor();
                SystemUtil.delay(1000);
                log.info("炉石传说已关闭");
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }else {
            log.info("炉石传说不在运行");
        }
    }

    public static void killPlatform(){
        WinDef.HWND platformHWND = findPlatformHWND();
        WinDef.HWND loginPlatformHWND = findLoginPlatformHWND();
        if (platformHWND != null || loginPlatformHWND != null){
            User32Dll.INSTANCE.closeProgram(platformHWND);
            User32Dll.INSTANCE.closeProgram(loginPlatformHWND);
            log.info("战网已关闭");
        }else {
            log.info("战网不在运行");
        }
    }

    /**
     * 添加托盘
     * @param trayIconName
     * @param trayName
     * @param menuItems
     */
    public static void addTray(String trayIconName, String trayName, Consumer<MouseEvent> mouseClickListener, MenuItem... menuItems){
        if (trayIcon != null){
            return;
        }
        Image image = Toolkit.getDefaultToolkit().getImage(SystemUtil.class.getResource(IMAGE_PATH + trayIconName));
//        托盘右键弹出菜单
        PopupMenu popupMenu = new PopupMenu();
        for (MenuItem menuItem : menuItems) {
            popupMenu.add(menuItem);
        }
//        托盘图标
        trayIcon = new TrayIcon(image, trayName, popupMenu);
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip(SCRIPT_NAME);
        trayIcon.addMouseListener(new MouseClickListener(mouseClickListener));
        try {
            TRAY.add(trayIcon);
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 移除托盘
     */
    public static void removeTray(){
        TRAY.remove(trayIcon);
    }

    /**
     * 到系统剪切板
     * @param content
     */
    public static boolean copyToClipboard(String content){
        Transferable contents = CLIPBOARD.getContents(null);
        //判断是否为文本类型
        if (contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            String text;
            try {
                text = (String) contents.getTransferData(DataFlavor.stringFlavor);
            } catch (UnsupportedFlavorException | IOException e) {
                throw new RuntimeException(e);
            }
            if (Objects.equals(content, text)) {
                return false;
            }
        }
        CLIPBOARD.setContents(new StringSelection(content), null);
        return true;
    }

    /**
     * 获取注册表中用户程序REG_SZ类型的信息
     * @param regCommonNameEnum
     * @param userProgramName
     * @return
     */
    public static String registryGetStringValueForUserProgram(RegCommonNameEnum regCommonNameEnum, String userProgramName){
        if (regCommonNameEnum == null){
            return null;
        }
//        64位程序和32位程序
        String path;
        if (!Advapi32Util.registryValueExists(WinReg.HKEY_LOCAL_MACHINE, path = "SOFTWARE\\Wow6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\" + userProgramName, regCommonNameEnum.getValue())
                &&
            !Advapi32Util.registryValueExists(WinReg.HKEY_LOCAL_MACHINE, path = "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\" + userProgramName, regCommonNameEnum.getValue())
        ){
            return null;
        }
        return Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, path, regCommonNameEnum.getValue());
    }

}
