package club.xiaojiawei.utils;

import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.listener.PowerFileListener;
import club.xiaojiawei.listener.ScreenFileListener;
import club.xiaojiawei.starter.GameStarter;
import club.xiaojiawei.starter.PlatformStarter;
import club.xiaojiawei.strategy.mode.LoginAbstractModeStrategy;
import club.xiaojiawei.strategy.mode.TournamentAbstractModeStrategy;
import club.xiaojiawei.strategy.phase.GameTurnAbstractPhaseStrategy;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import javafx.beans.property.BooleanProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static club.xiaojiawei.data.ScriptStaticData.GAME_NAME;

/**
 * @author 肖嘉威
 * @date 2022/11/24 17:21
 */
@Slf4j
@Component
public class SystemUtil {
    @Resource
    private AtomicReference<BooleanProperty> isPause;

    public final static Clipboard CLIPBOARD = Toolkit.getDefaultToolkit().getSystemClipboard();

    /**
     * 调用系统通知
     * @param title
     * @param content
     */
    public void notice(String title, String content){
        trayIcon.displayMessage(title, content, TrayIcon.MessageType.WARNING);
    }

    public static void cancelAllTask(){
        log.info("终止所有模式任务");
        GameUtil.cancelTask();
        LoginAbstractModeStrategy.cancelTask();
        TournamentAbstractModeStrategy.cancelTask();
    }

    public static void cancelAllListener(){
        log.info("终止所有监听器");
        PowerFileListener.cancelListener();
        ScreenFileListener.cancelListener();
    }

    public static void stopAllThread(){
        log.info("终止所有额外线程");
        GameTurnAbstractPhaseStrategy.stopThread();
    }

    public static void cancelAllProgramTimer(){
        log.info("终止所有程序启动定时器");
        PlatformStarter.cancelPlatformTimer();
        GameStarter.cancelGameTimer();
    }

    public static void cancelAll(){
        cancelAllListener();
        stopAllThread();
        cancelAllTask();
        cancelAllProgramTimer();
    }
    public void notice(String context){
        notice(ScriptStaticData.SCRIPT_NAME, context);
    }

    /**
     * 更新窗口信息
     */
    public void updateRect(WinDef.HWND gameHWND, WinDef.RECT gameRECT) {
        User32.INSTANCE.GetWindowRect(gameHWND, gameRECT);
        if ((ScriptStaticData.GAME_RECT.bottom - ScriptStaticData.GAME_RECT.top) != ScriptStaticData.DISPLAY_PIXEL_Y){
            ScriptStaticData.GAME_RECT.top += ScriptStaticData.WINDOW_TOP_PIXEL;
        }
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
    public void frontWindow(WinDef.HWND hwnd){
        if (isPause.get().get()){
            return;
        }
        // 显示窗口
        User32.INSTANCE.ShowWindow(hwnd, 9 );
        // 前置窗口
        User32.INSTANCE.SetForegroundWindow(hwnd);
    }

    public void delayShort(){
        ScriptStaticData.ROBOT.delay(RandomUtil.getShortRandom());
    }

    public void delayMedium(){
        ScriptStaticData.ROBOT.delay(RandomUtil.getMediumRandom());
    }

    public void delayLong(){
        ScriptStaticData.ROBOT.delay(RandomUtil.getLongRandom());
    }

    @Deprecated
    public void killProgram(){
        ScriptStaticData.ROBOT.keyPress(18);
        ScriptStaticData.ROBOT.keyPress(115);
        ScriptStaticData.ROBOT.keyRelease(115);
        ScriptStaticData.ROBOT.keyRelease(18);
        log.info("已关闭程序");
    }

    /**
     * 通过此方式停止的游戏，screen.log监听器无法监测到游戏被关闭
     */
    public void killGame(){
        try {
            Runtime.getRuntime().exec("cmd /c taskkill /f /t /im " + GAME_NAME);
            log.info("已关闭游戏");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 系统托盘
     */
    public static final SystemTray TRAY = SystemTray.getSystemTray();

    public static TrayIcon trayIcon;

    /**
     * 添加托盘
     * @param trayIconName
     * @param trayName
     * @param menuItems
     */
    public void addTray(String trayIconName, String trayName, MenuItem... menuItems){
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
    public void removeTray(){
        TRAY.remove(trayIcon);
    }

    /**
     * 粘贴到系统剪切板
     * @param content
     */
    public boolean pasteClipboard(String content){
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

}
