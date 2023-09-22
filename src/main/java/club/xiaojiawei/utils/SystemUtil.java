package club.xiaojiawei.utils;

import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.enums.RegCommonNameEnum;
import club.xiaojiawei.listener.DeckLogListener;
import club.xiaojiawei.listener.PowerLogListener;
import club.xiaojiawei.listener.ScreenLogListener;
import club.xiaojiawei.starter.GameStarter;
import club.xiaojiawei.starter.PlatformStarter;
import club.xiaojiawei.strategy.mode.LoginAbstractModeStrategy;
import club.xiaojiawei.strategy.mode.TournamentAbstractModeStrategy;
import club.xiaojiawei.strategy.phase.GameTurnAbstractPhaseStrategy;
import club.xiaojiawei.strategy.phase.ReplaceCardAbstractPhaseStrategy;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinReg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.awt.*;
import java.awt.datatransfer.*;
import java.io.*;
import java.net.*;
import java.util.Objects;

import static club.xiaojiawei.data.ScriptStaticData.GAME_PROGRAM_NAME;

/**
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
        LoginAbstractModeStrategy.cancelTask();
        TournamentAbstractModeStrategy.cancelTask();
    }

    public static void cancelAllListener(){
        log.info("终止所有监听器");
        screenLogListener.cancelListener();
        powerLogListener.cancelListener();
        deckLogListener.cancelListener();
    }

    public static void stopAllThread(){
        log.info("终止所有额外线程");
        ReplaceCardAbstractPhaseStrategy.stopThread();
        GameTurnAbstractPhaseStrategy.stopThread();
    }

    public static void cancelAllProgramTimer(){
        log.info("终止所有程序启动定时器");
        PlatformStarter.cancelPlatformTimer();
        GameStarter.cancelGameTimer();
    }

    public static void cancelAllRunnable(){
        stopAllThread();
        cancelAllTask();
        cancelAllProgramTimer();
        cancelAllListener();
        delay(2000);
    }
    public static void notice(String context){
        notice(ScriptStaticData.SCRIPT_NAME, context);
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
     * 更新窗口信息
     */
    public static void updateRect(WinDef.HWND programHWND, WinDef.RECT programRECT) {
//        如果程序最小化无法获取到准确的窗口信息
        frontWindow(programHWND);
        User32.INSTANCE.GetWindowRect(programHWND, programRECT);
        if ((ScriptStaticData.GAME_RECT.bottom - ScriptStaticData.GAME_RECT.top) != ScriptStaticData.DISPLAY_PIXEL_Y){
            ScriptStaticData.GAME_RECT.top += ScriptStaticData.WINDOW_TITLE_Y;
        }
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
        ScriptStaticData.ROBOT.delay(delay);
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
    public  static void killProgram(){
        ScriptStaticData.ROBOT.keyPress(18);
        ScriptStaticData.ROBOT.keyPress(115);
        ScriptStaticData.ROBOT.keyRelease(115);
        ScriptStaticData.ROBOT.keyRelease(18);
        log.info("已关闭程序");
    }

    /**
     * 通过此方式停止的游戏，screen.log监听器可能无法监测到游戏被关闭
     */
    public static void killGame(){
        try {
            Runtime.getRuntime().exec("cmd /c taskkill /f /t /im " + GAME_PROGRAM_NAME).waitFor();
            SystemUtil.delay(1000);
            log.info("游戏已关闭");
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 添加托盘
     * @param trayIconName
     * @param trayName
     * @param menuItems
     */
    public static void addTray(String trayIconName, String trayName, MenuItem... menuItems){
        if (trayIcon != null){
            return;
        }
        Image image = Toolkit.getDefaultToolkit().getImage(SystemUtil.class.getResource(ScriptStaticData.IMAGE_PATH + trayIconName));
//        托盘右键弹出菜单
        PopupMenu popupMenu = new PopupMenu();
        for (MenuItem menuItem : menuItems) {
            popupMenu.add(menuItem);
        }
//        托盘图标
        trayIcon = new TrayIcon(image, trayName, popupMenu);
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip(ScriptStaticData.SCRIPT_NAME);
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
     * 粘贴到系统剪切板
     * @param content
     */
    public static boolean pasteClipboard(String content){
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
