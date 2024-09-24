package club.xiaojiawei.utils;

import club.xiaojiawei.annotations.NotNull;
import club.xiaojiawei.bean.GameRect;
import club.xiaojiawei.config.ThreadPoolConfigKt;
import club.xiaojiawei.interfaces.closer.ModeTaskCloser;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.enums.ConfigurationEnum;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import javafx.beans.property.BooleanProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static com.sun.jna.platform.win32.Win32VK.VK_ESCAPE;
import static com.sun.jna.platform.win32.WinUser.WM_KEYDOWN;
import static com.sun.jna.platform.win32.WinUser.WM_KEYUP;

/**
 * 游戏工具类
 * @author 肖嘉威
 * @date 2022/11/27 1:42
 */
@Component
@Slf4j
public class GameUtil implements ModeTaskCloser {


    private static AtomicReference<BooleanProperty> isPause;

    private static Properties scriptConfiguration;

    public GameUtil(AtomicReference<BooleanProperty> isPause, Properties scriptConfiguration) {
        GameUtil.isPause = isPause;
        GameUtil.scriptConfiguration = scriptConfiguration;
    }

    private static ScheduledFuture<?> clickGameEndPageTask;

    public static final GameRect CENTER_RECT = new GameRect(-0.1D, 0.1D, 0.1D, -0.1D);

    public static final GameRect CONFIRM_RECT = new GameRect(-0.0546D, 0.0601D, 0.2709D, 0.3222D);

    public static final GameRect END_TURN_RECT = new GameRect(0.3535D, 0.4533D, -0.0636D, -0.0196D);

    public static final GameRect RECONNECT_RECT = new GameRect(-0.1845D, -0.0396D, 0.2282D, 0.2904D);

    public static final GameRect CANCEL_CONNECT_RECT = new GameRect(0.0266D, 0.1714D, 0.2282D, 0.2904D);

    public static final GameRect SURRENDER_RECT = new GameRect(-0.0629D, 0.0607D, -0.1677D, -0.1279D);

    public static final GameRect THANK_RECT = new GameRect(-0.1604D, -0.0404D, 0.1153D, 0.1502D);
    public static final GameRect PRAISE_RECT = new GameRect(-0.1930D, -0.0730D, 0.1971D, 0.2320D);
    public static final GameRect GREET_RECT = new GameRect(-0.1907D, -0.0707D, 0.2799D, 0.3148D);
    public static final GameRect THREATEN_RECT = new GameRect(0.0754D, 0.1954D, 0.2830D, 0.3180D);
    public static final GameRect ERROR_RECT = new GameRect(0.0786D, 0.1986D, 0.1981D, 0.2331D);
    public static final GameRect WONDER_RECT = new GameRect(0.0444D, 0.1644D, 0.1174D, 0.1523D);

    public static final GameRect RIVAL_HERO_RECT = new GameRect(-0.0453D, 0.0488D, -0.3620D, -0.2355D);
    public static final GameRect MY_HERO_RECT = new GameRect(-0.0453D, 0.0488D, 0.2229D, 0.3494D);

    public static final GameRect RIVAL_POWER_RECT = new GameRect(0.0840D, 0.1554D, -0.3260D, -0.2338D);
    public static final GameRect MY_POWER_RECT = new GameRect(0.0855D, 0.1569D, 0.2254D, 0.3176D);

    private static final GameRect[] FOUR_DISCOVER_RECTS = new GameRect[]{
            new GameRect(-0.3332D, -0.1911D, -0.1702D, 0.1160D),
            new GameRect(-0.1570D, -0.0149D, -0.1702D, 0.1160D),
            new GameRect(0.0182D, 0.1603D, -0.1702D, 0.1160D),
            new GameRect(0.1934D, 0.3355D, -0.1702D, 0.1160D),
    };

    private static final GameRect[] THREE_DISCOVER_RECTS = new GameRect[]{
            new GameRect(-0.3037D, -0.1595D, -0.1702D, 0.1160D),
            new GameRect(-0.0666D, 0.0741D, -0.1702D, 0.1160D),
            new GameRect(0.1656D, 0.3106D, -0.1702D, 0.1160D),
    };

    private static final GameRect[][] MY_HAND_DECK_RECTS = new GameRect[][]{
            new GameRect[]{
                    new GameRect(-0.0693D, 0.0136D, 0.3675D, 0.5000D),
            },
            new GameRect[]{
                    new GameRect(-0.1149D, -0.0316D, 0.3675D, 0.5000D),
                    new GameRect(-0.0242D, 0.0590D, 0.3675D, 0.5000D),
            },
            new GameRect[]{
                    new GameRect(-0.1599D, -0.0767D, 0.3675D, 0.5000D),
                    new GameRect(-0.0693D, 0.0140D, 0.3675D, 0.5000D),
                    new GameRect(0.0214D, 0.1047D, 0.3675D, 0.5000D),
            },
            new GameRect[]{
                    new GameRect(-0.1930D, -0.1307D, 0.3855D, 0.5000D),
                    new GameRect(-0.1092D, -0.0347D, 0.3742D, 0.5000D),
                    new GameRect(-0.0208D, 0.0507D, 0.3814D, 0.4995D),
                    new GameRect(0.0744D, 0.1425D, 0.4158D, 0.5000D),
            },
            new GameRect[]{
                    new GameRect(-0.2034D, -0.1471D, 0.4116D, 0.5000D),
                    new GameRect(-0.1338D, -0.0704D, 0.3888D, 0.5000D),
                    new GameRect(-0.0704D, -0.0071D, 0.3698D, 0.5000D),
                    new GameRect(0.0077D, 0.0604D, 0.3935D, 0.5000D),
                    new GameRect(0.0858D, 0.1456D, 0.4144D, 0.5000D),
            },
            new GameRect[]{
                    new GameRect(-0.2115D, -0.1672D, 0.4144D, 0.5000D),
                    new GameRect(-0.1514D, -0.1028D, 0.3964D, 0.5000D),
                    new GameRect(-0.0975D, -0.0448D, 0.3755D, 0.5000D),
                    new GameRect(-0.0384D, 0.0087D, 0.3755D, 0.5000D),
                    new GameRect(0.0270D, 0.0671D, 0.3812D, 0.4990D),
                    new GameRect(0.0903D, 0.1579D, 0.4240D, 0.5000D),
            },
            new GameRect[]{
                    new GameRect(-0.2179D, -0.1799D, 0.4192D, 0.5000D),
                    new GameRect(-0.1640D, -0.1232D, 0.4040D, 0.5000D),
                    new GameRect(-0.1155D, -0.0690D, 0.3869D, 0.5000D),
                    new GameRect(-0.0712D, -0.0233D, 0.3717D, 0.5000D),
                    new GameRect(-0.0152D, 0.0235D, 0.3755D, 0.5000D),
                    new GameRect(0.0418D, 0.0727D, 0.3821D, 0.5000D),
                    new GameRect(0.0956D, 0.1617D, 0.4211D, 0.5000D),
            },
            new GameRect[]{
                    new GameRect(-0.2210D, -0.1901D, 0.4259D, 0.5000D),
                    new GameRect(-0.1746D, -0.1394D, 0.4125D, 0.5000D),
                    new GameRect(-0.1324D, -0.0916D, 0.3973D, 0.5000D),
                    new GameRect(-0.0912D, -0.0490D, 0.3745D, 0.5000D),
                    new GameRect(-0.0469D, -0.0103D, 0.3688D, 0.5000D),
                    new GameRect(0.0038D, 0.0326D, 0.3745D, 0.5000D),
                    new GameRect(0.0534D, 0.0759D, 0.4040D, 0.5000D),
                    new GameRect(0.1030D, 0.1536D, 0.4163D, 0.4990D),
            },
            new GameRect[]{
                    new GameRect(-0.2274D, -0.1964D, 0.4335D, 0.5000D),
                    new GameRect(-0.1820D, -0.1496D, 0.4335D, 0.5000D),
                    new GameRect(-0.1429D, -0.1099D, 0.4059D, 0.5000D),
                    new GameRect(-0.1060D, -0.0687D, 0.3888D, 0.5000D),
                    new GameRect(-0.0712D, -0.0346D, 0.3698D, 0.5000D),
                    new GameRect(-0.0268D, 0.0034D, 0.3745D, 0.5000D),
                    new GameRect(0.0186D, 0.0502D, 0.3764D, 0.4563D),
                    new GameRect(0.0639D, 0.0942D, 0.3878D, 0.4610D),
                    new GameRect(0.1083D, 0.1653D, 0.4125D, 0.5000D),
            },
            new GameRect[]{
                    new GameRect(-0.2305D, -0.2024D, 0.4401D, 0.5000D),
                    new GameRect(-0.1894D, -0.1598D, 0.4401D, 0.5000D),
                    new GameRect(-0.1524D, -0.1250D, 0.4097D, 0.5000D),
                    new GameRect(-0.1176D, -0.0859D, 0.3964D, 0.5000D),
                    new GameRect(-0.0859D, -0.0522D, 0.3726D, 0.5000D),
                    new GameRect(-0.0511D, -0.0208D, 0.3726D, 0.5000D),
                    new GameRect(-0.0089D, 0.0207D, 0.3740D, 0.4501D),
                    new GameRect(0.0302D, 0.0583D, 0.3783D, 0.4515D),
                    new GameRect(0.0692D, 0.0974D, 0.3926D, 0.4610D),
                    new GameRect(0.1093D, 0.1677D, 0.4163D, 0.5000D),
            },
    };

    private static final GameRect[][] MY_PLAY_DECK_RECTS = new GameRect[][]{
//            偶数
            new GameRect[]{
                    new GameRect(-0.2689D, -0.2111D, -0.0033D, 0.1050D),
                    new GameRect(-0.1731D, -0.1153D, -0.0033D, 0.1050D),
                    new GameRect(-0.0773D, -0.0195D, -0.0033D, 0.1050D),
                    new GameRect(0.0195D, 0.0773D, -0.0033D, 0.1050D),
                    new GameRect(0.1153D, 0.1731D, -0.0033D, 0.1050D),
                    new GameRect(0.2111D, 0.2689D, -0.0033D, 0.1050D),
            },
//            奇数
            new GameRect[]{
                    new GameRect(-0.3156D, -0.2578D, -0.0041D, 0.1043D),
                    new GameRect(-0.2204D, -0.1626D, -0.0041D, 0.1043D),
                    new GameRect(-0.1257D, -0.0691D, -0.0041D, 0.1043D),
                    new GameRect(-0.0299D, 0.0267D, -0.0041D, 0.1043D),
                    new GameRect(0.0691D, 0.1257D, -0.0041D, 0.1043D),
                    new GameRect(0.1626D, 0.2204D, -0.0041D, 0.1043D),
                    new GameRect(0.2578D, 0.3156D, -0.0041D, 0.1043D),
            },
    };

    private static final GameRect[][] RIVAL_PLAY_DECK_RECTS = new GameRect[][]{
//            偶数
            new GameRect[]{
                    new GameRect(-0.2689D, -0.2111D, -0.1730D, -0.0716D),
                    new GameRect(-0.1731D, -0.1153D, -0.1730D, -0.0716D),
                    new GameRect(-0.0773D, -0.0195D, -0.1730D, -0.0716D),
                    new GameRect(0.0195D, 0.0773D, -0.1730D, -0.0716D),
                    new GameRect(0.1153D, 0.1731D, -0.1730D, -0.0716D),
                    new GameRect(0.2111D, 0.2689D, -0.1730D, -0.0716D),
            },
//            奇数
            new GameRect[]{
                    new GameRect(-0.3156D, -0.2578D, -0.1730D, -0.0716D),
                    new GameRect(-0.2204D, -0.1626D, -0.1730D, -0.0716D),
                    new GameRect(-0.1257D, -0.0691D, -0.1730D, -0.0716D),
                    new GameRect(-0.0299D, 0.0267D, -0.1730D, -0.0716D),
                    new GameRect(0.0691D, 0.1257D, -0.1730D, -0.0716D),
                    new GameRect(0.1626D, 0.2204D, -0.1730D, -0.0716D),
                    new GameRect(0.2578D, 0.3156D, -0.1730D, -0.0716D),
            },
    };

    @NotNull
    public static GameRect getThreeDiscoverCardRect(int index){
        if (index < 0 || index > THREE_DISCOVER_RECTS.length - 1) {
            return GameRect.INVALID;
        }
        return THREE_DISCOVER_RECTS[index];
    }

    @NotNull
    public static GameRect getFourDiscoverCardRect(int index){
        if (index < 0 || index > FOUR_DISCOVER_RECTS.length - 1) {
            return GameRect.INVALID;
        }
        return FOUR_DISCOVER_RECTS[index];
    }

    @NotNull
    public static GameRect getMyHandCardRect(int index, int size){
        if (index < 0 || index > size - 1 || size > MY_HAND_DECK_RECTS.length - 1){
            return GameRect.INVALID;
        }
        return MY_HAND_DECK_RECTS[size - 1][index];
    }

    @NotNull
    public static GameRect getMyPlayCardRect(int index, int size){
        return getPlayCardRect(index, size, MY_PLAY_DECK_RECTS);
    }

    @NotNull
    public static GameRect getRivalPlayCardRect(int index, int size){
        return getPlayCardRect(index, size, RIVAL_PLAY_DECK_RECTS);
    }

    private static GameRect getPlayCardRect(int index, int size, GameRect[][] gameRects){
        size = Math.max(size ,0);
        GameRect[] rects = gameRects[size & 1];
        int offset = (rects.length - size) >> 1;
        index = Math.max(offset + index, 0);
        index = Math.min(index, rects.length - 1);
        return rects[index];
    }

    public static void clickDiscover(int index, int discoverSize){
        if (discoverSize == 3){
            getThreeDiscoverCardRect(index).lClick();
        }else {
            getFourDiscoverCardRect(index).lClick();
        }
    }

    public static void leftButtonClick(Point point){
        MouseUtil.leftButtonClick(point, ScriptStaticData.getGameHWND());
    }

    public static void rightButtonClick(Point point){
        MouseUtil.leftButtonClick(point, ScriptStaticData.getGameHWND());
    }

    public static void moveMouse(Point startPos, Point endPos){
        MouseUtil.moveMouseByLine(startPos, endPos, ScriptStaticData.getGameHWND());
    }

    public static void moveMouse(Point endPos){
        MouseUtil.moveMouseByLine(endPos, ScriptStaticData.getGameHWND());
    }


    /**
     * 如果战网不在运行则相当于启动战网，如果战网已经运行则为启动炉石
     */
    public static void cmdLaunchGame(){
        try {
            Runtime.getRuntime().exec("\"" + scriptConfiguration.getProperty(ConfigurationEnum.PLATFORM_PATH.getKey()) + "\"" + " --exec=\"launch WTCG\"");
        } catch (IOException e) {
            log.error("命令行启动炉石异常", e);
        }
    }

    /**
     * 游戏里投降
     */
    public static void surrender(){
        SystemUtil.closeGameThread();
        SystemUtil.delay(10000);
//        SystemUtil.frontWindow(ScriptStaticData.getGameHWND());
//        按ESC键弹出投降界面
//        ScriptStaticData.ROBOT.keyPress(27);
//        ScriptStaticData.ROBOT.keyRelease(27);
        int width = ScriptStaticData.GAME_RECT.right - ScriptStaticData.GAME_RECT.left;
        int height = ScriptStaticData.GAME_RECT.bottom - ScriptStaticData.GAME_RECT.top;
        GameUtil.leftButtonClick(new Point((int) (width - width * 0.0072992700729927D), (int) (height - height * 0.015625D)));
        SystemUtil.delay(1500);
        SURRENDER_RECT.lClick();
        clickGameEndPageTask();
    }

    public static void cancelAction(){
        MouseUtil.rightButtonClick(ScriptStaticData.getGameHWND());
    }

    public static void lClickCenter(){
        CENTER_RECT.lClick();
    }

    public static void rClickCenter(){
        CENTER_RECT.rClick();
    }

    public static void reconnect(){
        RECONNECT_RECT.lClick();
    }

    /**
     * 点掉游戏结束结算页面
     */
    public static void clickGameEndPageTask(){
        cancelTask();
        log.info("点掉游戏结束结算页面");
        clickGameEndPageTask = ThreadPoolConfigKt.getEXTRA_THREAD_POOL().scheduleWithFixedDelay(
            () -> {
                if (isPause.get().get()){
                    cancelTask();
                }else {
                    lClickCenter();
                }
            },
            4500,
            2000,
            TimeUnit.MILLISECONDS
        );
    }

    public static void hidePlatformWindow(){
        WinDef.HWND platformHWND = SystemUtil.findPlatformHWND();
        if (platformHWND != null && !User32.INSTANCE.ShowWindow(platformHWND, WinUser.SW_MINIMIZE)){
            log.error("最小化战网窗口异常，错误代码：{}", Kernel32.INSTANCE.GetLastError());
        }
    }

    private static void cancelTask(){
        if (clickGameEndPageTask != null && !clickGameEndPageTask.isDone()){
            clickGameEndPageTask.cancel(true);
        }
    }

    @Override
    public void closeModeTask() {
        cancelTask();
    }

}
