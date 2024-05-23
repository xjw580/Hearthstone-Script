package club.xiaojiawei.utils;

import club.xiaojiawei.closer.ModeTaskCloser;
import club.xiaojiawei.data.GameRationStaticData;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.enums.ConfigurationEnum;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import jakarta.annotation.Resource;
import javafx.beans.property.BooleanProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 游戏工具类
 * @author 肖嘉威
 * @date 2022/11/27 1:42
 */
@Component
@Slf4j
public class GameUtil implements ModeTaskCloser {

    @Resource
    private MouseUtil mouseUtil;
    @Resource
    private ScheduledThreadPoolExecutor extraThreadPool;
    @Resource
    private AtomicReference<BooleanProperty> isPause;
    @Resource
    private Properties scriptConfiguration;

    private ScheduledFuture<?> clickGameEndPageTask;

    private static final float SURRENDER_BUTTON_VERTICAL_TO_BOTTOM_RATION = (float) 0.652;

    /**
     * 点击炉石里的返回按钮
     */
    public void clickBackButton(){
        mouseUtil.leftButtonClick(
                GameRationStaticData.BACK_BUTTON_HORIZONTAL_TO_CENTER_RATION,
                GameRationStaticData.BACK_BUTTON_VERTICAL_TO_BOTTOM_RATION,
                new int[]{-5, 5},
                new int[]{-2, 2}
        );
    }

    /**
     * 如果战网不在运行则相当于启动战网，如果战网已经运行则为启动炉石
     */
    public void cmdLaunchGame(){
        try {
            Runtime.getRuntime().exec("\"" + scriptConfiguration.getProperty(ConfigurationEnum.PLATFORM_PATH.getKey()) + "\"" + " --exec=\"launch WTCG\"");
        } catch (IOException e) {
            log.error("命令行启动炉石异常", e);
        }
    }

    /**
     * 游戏里投降
     */
    public void surrender(){
        SystemUtil.closeGameThread();
        SystemUtil.delay(10000);
        SystemUtil.frontWindow(ScriptStaticData.getGameHWND());
//        按ESC键弹出投降界面
        ScriptStaticData.ROBOT.keyPress(27);
        ScriptStaticData.ROBOT.keyRelease(27);
        SystemUtil.delay(1500);
        SystemUtil.updateGameRect();
//        点击投降按钮
        mouseUtil.leftButtonClick(
            ScriptStaticData.GAME_RECT.right + ScriptStaticData.GAME_RECT.left >> 1,
            (int) (ScriptStaticData.GAME_RECT.bottom - (ScriptStaticData.GAME_RECT.bottom - ScriptStaticData.GAME_RECT.top) * SURRENDER_BUTTON_VERTICAL_TO_BOTTOM_RATION)
        );
        clickGameEndPageTask();
    }

    /**
     * 点掉游戏结束结算页面
     */
    public void clickGameEndPageTask(){
        SystemUtil.updateGameRect();
        cancelTask();
        log.info("点掉游戏结束结算页面");
        clickGameEndPageTask = extraThreadPool.scheduleWithFixedDelay(
            () -> {
                if (isPause.get().get()){
                    cancelTask();
                }else {
                    mouseUtil.leftButtonClick(
                            ScriptStaticData.GAME_RECT.right + ScriptStaticData.GAME_RECT.left >> 1,
                            (int) (ScriptStaticData.GAME_RECT.bottom - (ScriptStaticData.GAME_RECT.bottom - ScriptStaticData.GAME_RECT.top) * SURRENDER_BUTTON_VERTICAL_TO_BOTTOM_RATION)
                    );
                }
            },
            4500,
            2000,
            TimeUnit.MILLISECONDS
        );
    }

    private void cancelTask(){
        if (clickGameEndPageTask != null && !clickGameEndPageTask.isDone()){
            clickGameEndPageTask.cancel(true);
        }
    }

    public static void hidePlatformWindow(){
        WinDef.HWND platformHWND = SystemUtil.findPlatformHWND();
        if (platformHWND != null){
            SystemUtil.delay(500);
//            User32.INSTANCE.MoveWindow(platformHWND, ScriptStaticData.DISPLAY_PIXEL_WIDTH - 100,  ScriptStaticData.DISPLAY_PIXEL_HEIGHT - 150, 0, 0, false);
//            SystemUtil.delay(500);
            if (!User32.INSTANCE.ShowWindow(platformHWND, WinUser.SW_MINIMIZE)){
                log.error("最小化窗口异常，错误代码：{}", Kernel32.INSTANCE.GetLastError());
            }
        }
    }

    @Override
    public void closeModeTask() {
        cancelTask();
    }

}
