package club.xiaojiawei.hearthstone.run;

import club.xiaojiawei.hearthstone.enums.ModeEnum;
import club.xiaojiawei.hearthstone.listener.ScreenFileListener;
import club.xiaojiawei.hearthstone.utils.MouseUtil;
import club.xiaojiawei.hearthstone.utils.RandomUtil;
import club.xiaojiawei.hearthstone.utils.SystemUtil;
import com.sun.jna.platform.win32.WinDef;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Timer;
import java.util.TimerTask;

import static club.xiaojiawei.hearthstone.constant.GameMapConst.MODE_MAP;
import static club.xiaojiawei.hearthstone.constant.SystemConst.PROPERTIES;

/**
 * @author 肖嘉威
 * @date 2022/11/24 19:09
 */
@Slf4j
@Component
public class Core {

    private volatile static boolean pause = true;
    private static String gameName;
    private static String platformName;
    private static String scriptName;
    @Value("${game.name}")
    public void setGameName(String gameName) {
        if (Core.gameName == null) {
            Core.gameName = gameName;
        }
    }

    @Value("${platform.name}")
    public void setPlatformName(String platformName) {
        if (Core.platformName == null) {
            Core.platformName = platformName;
        }
    }

    @Value("${script.name}")
    public void setScriptName(String scriptName) {
        if (Core.scriptName == null) {
            Core.scriptName = scriptName;
        }
    }

    private static WinDef.HWND platformHWND;
    private static WinDef.HWND gameHWND;

    public static void openGame() {
        Timer platformTimer = new Timer();
        log.info("开始检查" + platformName);
//        检查战网
        platformTimer.schedule(new TimerTask() {
            @SneakyThrows
            @Override
            public void run() {
                if (!Core.getPause()) {
                    if ((platformHWND = SystemUtil.getHWND(platformName)) == null) {
                        String platformPath = PROPERTIES.getProperty("platformpath");
                        if (Strings.isBlank(platformPath)) {
                            SystemUtil.notice(scriptName, "请手动打开" + platformName);
                            log.info("等待" + platformName + "被手动打开");
                        } else {
                            log.info("正在打开" + platformName);
                            Runtime.getRuntime().exec("cmd /c " + platformPath);
                            new ProcessBuilder(platformPath).start();
                        }
                    } else {
                        log.info(platformName + "正在运行");
                        platformTimer.cancel();
                        log.info("开始检查" + gameName);
//                    判断脚本和炉石谁先启动
                        if ((gameHWND = SystemUtil.getHWND(gameName)) == null) {
                            log.info("正在启动" + gameName);
                            Timer gameTimer = new Timer();
//                        检查炉石
                            gameTimer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    if (!Core.getPause()) {
                                        if ((gameHWND = SystemUtil.getHWND(gameName)) != null) {
                                            gameTimer.cancel();
                                        } else {
                                            SystemUtil.frontWindow(platformHWND);
                                            WinDef.RECT rect = SystemUtil.getRect(platformHWND);
                                            MouseUtil.leftButtonClick(rect.left + RandomUtil.getRandom(100, 150), rect.bottom - RandomUtil.getRandom(100, 120));
                                        }
                                    }
                                }
                            }, 0, 3000);
                        } else {
                            log.info(gameName + "正在运行");
                            gameHWND = SystemUtil.getHWND(gameName);
                            ScreenFileListener.initReadScreenLog();
                        }
                    }
                }
            }
        }, 2000, 6000);
    }

    public static void enterMode() {
        SystemUtil.frontWindow(gameHWND);
        log.info("准备进入指定模式");
        MODE_MAP.getOrDefault(PROPERTIES.getProperty("mode"), ModeEnum.UNKNOWN).getModeStrategy().get().intoMode();
    }

    /**
     * 检测脚本开关
     */
    public static void start() {
        String date = PROPERTIES.getProperty("date");
        String s = String.valueOf(LocalDate.now().getDayOfWeek().getValue());
        String[] dates = date.split(",");
//            是否在指定时间内
        for (String w : dates) {
            if (w.equals(s)) {
                String time = PROPERTIES.getProperty("time");
                int hour = LocalTime.now().getHour();
                String[] times = time.split(",");
                for (String time1 : times) {
                    String[] split = time1.split("-");
                    int start = Integer.parseInt(split[0]), end = Integer.parseInt(split[1]);
                    if (hour >= start && hour < end) {
                        log.info("脚本准备运行中");
                        Core.setPause(false);
                        Core.openGame();
                        return;
                    }
                }
                break;
            }
        }
        log.info("未到指定时间，脚本暂停中");
    }

    public static WinDef.HWND getPlatformHWND() {
        return platformHWND;
    }

    public static WinDef.HWND getGameHWND() {
        return gameHWND;
    }

    public static boolean getPause() {
        return pause;
    }

    public static void setPause(boolean isPause) {
        Core.pause = isPause;
    }

    public static String getGameName() {
        return gameName;
    }

    public static String getPlatformName() {
        return platformName;
    }

    public static String getScriptName() {
        return scriptName;
    }


}
