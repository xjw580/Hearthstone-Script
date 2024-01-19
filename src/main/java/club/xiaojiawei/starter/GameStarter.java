package club.xiaojiawei.starter;

import club.xiaojiawei.custom.LogRunnable;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.utils.MouseUtil;
import club.xiaojiawei.utils.SystemUtil;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import jakarta.annotation.Resource;
import javafx.beans.property.BooleanProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 启动游戏
 * @author 肖嘉威
 * @date 2023/7/5 14:38
 */
@Slf4j
@Component
public class GameStarter extends AbstractStarter{

    @Resource
    private AtomicReference<BooleanProperty> isPause;
    @Resource
    private ScheduledThreadPoolExecutor launchProgramThreadPool;
    @Resource
    private MouseUtil mouseUtil;
    @Resource
    private ScheduledThreadPoolExecutor extraThreadPool;
    private static ScheduledFuture<?> scheduledFuture;
    private static WinDef.HWND gameHWND;

    @Override
    public void exec() {
        log.info("开始检查" + ScriptStaticData.GAME_CN_NAME);
        if ((gameHWND = SystemUtil.findGameHWND()) != null){
            cancelAndStartNext();
            return;
        }
        scheduledFuture = launchProgramThreadPool.scheduleAtFixedRate(new LogRunnable(() -> {
            if (isPause.get().get()) {
                cancelGameTimer();
            } else {
                if (SystemUtil.isAliveOfGame()) {
//                    游戏刚启动时找不到HWND
                    if ((gameHWND = SystemUtil.findGameHWND()) == null){
                        return;
                    }
                    cancelAndStartNext();
                }else {
                    launchGame();
                }
            }
        }), 5, 30, TimeUnit.SECONDS);
    }

    private void launchGame(){
        log.info("正在打开" + ScriptStaticData.GAME_CN_NAME);
        WinDef.HWND platformhwnd = SystemUtil.findHWND(ScriptStaticData.PLATFORM_CN_NAME);
        mouseUtil.leftButtonClickByUser32(platformhwnd, 145, 120);
    }


    public static void cancelGameTimer(){
        if (scheduledFuture != null && !scheduledFuture.isDone()){
            scheduledFuture.cancel(true);
        }
    }
    public void cancelAndStartNext(){
        log.info(ScriptStaticData.GAME_CN_NAME + "正在运行");
        cancelGameTimer();
        WinDef.HWND platformHWND = SystemUtil.findPlatformHWND();
        if (platformHWND != null){
            User32.INSTANCE.ShowWindow(platformHWND, WinUser.SW_MINIMIZE);
        }
        extraThreadPool.schedule(() -> {
            ScriptStaticData.setGameHWND(gameHWND);
            SystemUtil.updateGameRect();
            startNextStarter();
        },1, TimeUnit.SECONDS);
    }
}
