package club.xiaojiawei.hearthstone.run;

import club.xiaojiawei.hearthstone.enums.ModeEnum;
import club.xiaojiawei.hearthstone.listen.ScreenFileListen;
import club.xiaojiawei.hearthstone.utils.CircleUtil;
import club.xiaojiawei.hearthstone.utils.MouseUtil;
import club.xiaojiawei.hearthstone.utils.RandomUtil;
import club.xiaojiawei.hearthstone.utils.SystemUtil;
import com.sun.jna.platform.win32.WinDef;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static club.xiaojiawei.hearthstone.constant.GameConst.MODE_MAP;
import static club.xiaojiawei.hearthstone.constant.SystemConst.PROPERTIES;
import static club.xiaojiawei.hearthstone.constant.SystemConst.ROBOT;
import static java.awt.event.InputEvent.BUTTON1_DOWN_MASK;

/**
 * @author 肖嘉威
 * @date 2022/11/24 19:09
 */
@Slf4j
@Component
public class Core {

    private static String GAME_NAME;
    private static String PLATFORM_NAME;
    private static String SCRIPT_NAME;

    @Value("${game.name}")
    public void setGameName(String gameName){
        GAME_NAME = gameName;
    }
    @Value("${platform.name}")
    public void setPlatformName(String platformName){
        PLATFORM_NAME= platformName;
    }
    @Value("${script.name}")
    public void setScriptName(String scriptName){
        SCRIPT_NAME = scriptName;
    }

    private static WinDef.HWND platformHWND;
    private static WinDef.HWND gameHWND;

    public static WinDef.HWND getPlatformHWND() {
        return platformHWND;
    }
    public static WinDef.HWND getGameHWND() {
        return gameHWND;
    }

    public static void openGame() {
        Timer platformTimer = new Timer();
        log.info("开始检查" + PLATFORM_NAME);
//        检查战网
        platformTimer.schedule(new TimerTask() {
            @SneakyThrows
            @Override
            public void run() {
                if ((platformHWND = SystemUtil.getHWND(PLATFORM_NAME)) == null){
                    String platformPath = PROPERTIES.getProperty("platformpath");
                    if (Strings.isBlank(platformPath)){
                        SystemUtil.notice(SCRIPT_NAME, "请手动打开" + PLATFORM_NAME);
                        log.info("等待" + PLATFORM_NAME + "被手动打开");
                    }else {
                        log.info("正在打开" + PLATFORM_NAME);
                        Runtime.getRuntime().exec("cmd /c " + platformPath);
                    }
                }else {
                    platformTimer.cancel();
                    log.info("开始检查" + GAME_NAME);
//                    判断脚本和炉石谁先启动
                    if ((gameHWND = SystemUtil.getHWND(GAME_NAME)) == null){
                        log.info("正在启动" + GAME_NAME);
                        Timer gameTimer = new Timer();
//                        检查炉石
                        gameTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                if ((gameHWND = SystemUtil.getHWND(GAME_NAME)) != null){
                                    gameTimer.cancel();
                                }else {
                                    WinDef.RECT rect = SystemUtil.getRect(platformHWND);
                                    SystemUtil.frontWindow(platformHWND);
                                    MouseUtil.mouseLeftButtonClick(rect.left + RandomUtil.getRandom(100, 150), rect.bottom - RandomUtil.getRandom(100, 120));
                                }
                            }
                        }, 0, 3000);
                    }else {
                        log.info(GAME_NAME + "正在运行");
                        gameHWND = SystemUtil.getHWND(GAME_NAME);
                        ScreenFileListen.initReadScreenLog();
                    }
                }
            }
        }, 2000, 5000);
    }

    public static void enterMode(){
        SystemUtil.frontWindow(gameHWND);
        log.info("准备进入指定模式");
        MODE_MAP.getOrDefault(PROPERTIES.getProperty("mode"), ModeEnum.UNKNOWN).getModeStrategy().get().intoMode();
    }

    public static void main(String[] args) throws InterruptedException, IOException {
//        WinDef.HWND hwnd = SystemUtil.getHWND(GAME_NAME);
//        WinDef.RECT rect = SystemUtil.getRect(hwnd);
//        SystemUtil.frontWindow(hwnd);
//        ROBOT.delay(200);
//        SystemUtil.frontWindow(hwnd);
//        int x = ((rect.left + rect.right) >> 1) - 140;
//        int y = rect.bottom - 50;
//        MouseUtil.mouseLeftButtonDrag(x, y, 0, 0);
//        MouseUtil.mouseLeftButtonClick(x, y);
//        BufferedImage screenCapture = ROBOT.createScreenCapture(new Rectangle(x, y, 50, 50));
//        ImageIO.write(screenCapture, "png", new File("C:\\Users\\zerg\\Desktop\\button.png"));
        WinDef.HWND hwnd = SystemUtil.getHWND("无标题 - 画图");
        SystemUtil.frontWindow(hwnd);
        double startX = 1400, startY = 600, endX = 600, endY = 600;
        CircleUtil circleUtil = new CircleUtil();
        circleUtil.setC(new CircleUtil.Coordinates(startX, startY), new CircleUtil.Coordinates(endX, endY));
        ROBOT.mouseMove((int) startX, (int) startY);
        ROBOT.mousePress(BUTTON1_DOWN_MASK);
        for (int i = 1400; i >= 600; i -= 5) {
            ROBOT.mouseMove(i, circleUtil.getY(i));
            ROBOT.delay(10);
        }
        ROBOT.mouseRelease(BUTTON1_DOWN_MASK);
    }



}
