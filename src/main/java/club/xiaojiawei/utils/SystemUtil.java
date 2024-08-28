package club.xiaojiawei.utils;

import club.xiaojiawei.interfaces.closer.GameThreadCloser;
import club.xiaojiawei.interfaces.closer.LogListenerCloser;
import club.xiaojiawei.interfaces.closer.ModeTaskCloser;
import club.xiaojiawei.interfaces.closer.StarterTaskCloser;
import club.xiaojiawei.custom.MouseClickListener;
import club.xiaojiawei.data.SpringData;
import club.xiaojiawei.dll.NoticeDll;
import club.xiaojiawei.dll.SystemDll;
import club.xiaojiawei.enums.ConfigurationEnum;
import club.xiaojiawei.enums.RegCommonNameEnum;
import club.xiaojiawei.ws.WebSocketServer;
import com.sun.jna.platform.win32.*;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;
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

    private static AtomicReference<BooleanProperty> isPause;
    private static SpringData springData;
    private static Properties scriptConfiguration;
    private static Map<String, GameThreadCloser> gameThreadCloserMap;
    private static Map<String, LogListenerCloser> logListenerCloserMap;
    private static Map<String, ModeTaskCloser> modeTaskCloserMap;
    private static Map<String, StarterTaskCloser> starterCloserMap;

    @Autowired
    public void setScreenLogListener(
            AtomicReference<BooleanProperty> isPause,
            SpringData springData,
            Properties scriptConfiguration,
            ApplicationContext applicationContext
    ) {
        SystemUtil.isPause = isPause;
        SystemUtil.springData = springData;
        SystemUtil.scriptConfiguration = scriptConfiguration;
        SystemUtil.gameThreadCloserMap = applicationContext.getBeansOfType(GameThreadCloser.class);
        SystemUtil.logListenerCloserMap = applicationContext.getBeansOfType(LogListenerCloser.class);
        SystemUtil.modeTaskCloserMap = applicationContext.getBeansOfType(ModeTaskCloser.class);
        SystemUtil.starterCloserMap = applicationContext.getBeansOfType(StarterTaskCloser.class);
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
    public static void notice(String title, String content, String btnText, String btnURL){
        if (Objects.equals(scriptConfiguration.getProperty(ConfigurationEnum.SEND_NOTICE.getKey()), "true")){
//        trayIcon.displayMessage(title, content, TrayIcon.MessageType.NONE);
            byte[] appIDBytes = SCRIPT_NAME.getBytes(StandardCharsets.UTF_8);
            byte[] titleBytes = title.getBytes(StandardCharsets.UTF_8);
            byte[] msgBytes = content.getBytes(StandardCharsets.UTF_8);
            byte[] icoPathBytes = (springData.getResourcePath() + MAIN_IMG_PNG_NAME).getBytes(StandardCharsets.UTF_8);
            byte[] btnTextBytes = btnText.getBytes(StandardCharsets.UTF_8);
            byte[] btnURLBytes = btnURL.getBytes(StandardCharsets.UTF_8);
            NoticeDll.INSTANCE.notice(
                    appIDBytes, titleBytes, msgBytes, icoPathBytes, btnTextBytes, btnURLBytes,
                    appIDBytes.length, titleBytes.length, msgBytes.length, icoPathBytes.length, btnTextBytes.length, btnURLBytes.length
            );
        }
    }
    public static void notice(String content){
        notice("", content, "", "");
    }

    public static void closeModeTask(){
        log.info("终止所有模式任务");
//        GameUtil.cancelTask();
//        LoginModeStrategy.cancelTask();
//        TournamentModeStrategy.cancelTask();
        modeTaskCloserMap.forEach((key, value) -> value.closeModeTask());
    }

    public static void closeLogListener(){
        log.info("终止所有监听器");
//        screenLogListener.cancelListener();
//        powerLogListener.cancelListener();
//        deckLogListener.cancelListener();
        logListenerCloserMap.forEach((key, value) -> value.closeLogListener());
    }

    public static void closeGameThread(){
        log.info("终止所有额外线程");
//        ReplaceCardPhaseStrategy.stopThread();
//        GameTurnPhaseStrategy.stopThread();
        gameThreadCloserMap.forEach((key, value) -> value.closeGameThread());
    }

    public static void closeStarterTask(){
        log.info("终止所有程序启动定时器");
//        PlatformStarter.cancelPlatformTimer();
//        LoginPlatformStarter.cancelLoginPlatformTimer();
//        GameStarter.cancelGameTimer();
        starterCloserMap.forEach((key, value) -> value.closeStarterTask());
    }

    public static void closeAll(){
        closeLogListener();
        closeGameThread();
        closeModeTask();
        closeStarterTask();
        delay(1000);
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
        if (!User32.INSTANCE.GetClientRect(programHWND, programRECT)){
            log.error("获取窗口尺寸异常，错误代码：{}", Kernel32.INSTANCE.GetLastError());
        }
//        非全屏时，需要去除窗口标题栏的高度
//        if ((programRECT.bottom - programRECT.top) != DISPLAY_PIXEL_HEIGHT){
//            programRECT.top += WINDOW_TITLE_PIXEL_Y;
//        }
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
        WinDef.HWND hwnd;
        return (hwnd = SystemUtil.findHWND(GAME_CN_NAME)) == null? SystemUtil.findHWND(GAME_US_NAME) : hwnd;
    }

    public static WinDef.HWND findPlatformHWND(){
        WinDef.HWND hwnd;
        return (hwnd = SystemUtil.findHWND(PLATFORM_CN_NAME)) == null? SystemUtil.findHWND(PLATFORM_US_NAME) : hwnd;
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
    public static boolean frontWindow(WinDef.HWND programHWND){
        // 显示窗口
        if (!User32.INSTANCE.ShowWindow(programHWND, 9 )){
            log.error("显示窗口异常，错误代码：" + Kernel32.INSTANCE.GetLastError());
            return false;
        }
        delay(100);
        // 前置窗口
        if (!User32.INSTANCE.SetForegroundWindow(programHWND)){
            log.error("前置窗口异常，错误代码：" + Kernel32.INSTANCE.GetLastError());
            return false;
        }
        delay(100);
        return true;
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
    public static void delay(int minDelay, int maxDelay){
        delay(RandomUtil.getRandom(minDelay, maxDelay));
    }
    public static void delayHuman(){
        delay(RandomUtil.getHumanRandom());
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

    /**
     * @deprecated 由 {@link SystemDll#closeProgram(WinDef.HWND)} 取代
     * @param programHWND
     */
    @Deprecated
    public static void killProgram(WinDef.HWND programHWND){
        frontWindow(programHWND);
        ROBOT.keyPress(18);
        ROBOT.keyPress(115);
        ROBOT.keyRelease(115);
        ROBOT.keyRelease(18);
        log.info("已关闭程序");
    }

    /**
     * 检测游戏是否存活
     * @return
     */
    public static boolean isAliveOfGame(){
        try {
            return Strings.isNotBlank(new String(Runtime.getRuntime().exec("cmd /c tasklist | find \"" + GAME_PROGRAM_NAME + "\"").getInputStream().readAllBytes()));
        } catch (IOException e) {
            log.error("检测游戏是否存活异常", e);
        }
        return true;
    }

    /**
     * 通过此方式停止的游戏，screen.log监听器可能无法监测到游戏被关闭
     */
    public static void killGame(){
        if (findGameHWND() != null){
            try {
                Runtime.getRuntime().exec("cmd /c taskkill /f /t /im " + GAME_PROGRAM_NAME).waitFor();
                SystemUtil.delay(1000);
                log.info("炉石传说已关闭");
            } catch (IOException e) {
                log.error("关闭炉石传说异常", e);
            } catch (InterruptedException e) {
                log.warn("关闭炉石传说异常", e);
            }
        }else {
            log.info("炉石传说不在运行");
        }
    }

    public static void killPlatform(){
        WinDef.HWND platformHWND = findPlatformHWND();
        WinDef.HWND loginPlatformHWND = findLoginPlatformHWND();
        if (platformHWND != null || loginPlatformHWND != null){
            SystemDll.INSTANCE.closeProgram(platformHWND);
            SystemDll.INSTANCE.closeProgram(loginPlatformHWND);
            log.info("战网已关闭");
        }else {
            log.info("战网不在运行");
        }
    }

    public static void killLoginPlatform(){
        WinDef.HWND loginPlatformHWND = SystemUtil.findLoginPlatformHWND();
        if (loginPlatformHWND != null){
            SystemDll.INSTANCE.closeProgram(loginPlatformHWND);
            log.info("登录战网已关闭");
        }else {
            log.info("登录战网不在运行");
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
        if (!SystemTray.isSupported()){
            log.warn("当前系统不支持系统托盘");
            return;
        }
        Image image = Toolkit.getDefaultToolkit().getImage(SystemUtil.class.getResource(FXML_IMAGE_PATH + trayIconName));
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

    /**
     * 关闭本软件
     */
    public static void shutdown(){
        SystemUtil.removeTray();
        isPause.get().set(true);
        Platform.exit();
        Thread.startVirtualThread(() -> {
            try {
                WebSocketServer.closeAll();
                log.info("关闭软件...");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error("休眠被中断", e);
            }
            System.exit(0);
        });
    }

}
