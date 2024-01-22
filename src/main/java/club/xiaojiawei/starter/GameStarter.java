package club.xiaojiawei.starter;

import club.xiaojiawei.custom.LogRunnable;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.utils.GameUtil;
import club.xiaojiawei.utils.MouseUtil;
import club.xiaojiawei.utils.SystemUtil;
import com.sun.jna.platform.win32.WinDef;
import jakarta.annotation.Resource;
import javafx.beans.property.BooleanProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
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
    @Lazy
    @Resource
    private AbstractStarter starter;
    private static ScheduledFuture<?> scheduledFuture;
    private static WinDef.HWND gameHWND;

    @Override
    public void exec() {
        log.info("开始检查" + ScriptStaticData.GAME_CN_NAME);
        if ((gameHWND = SystemUtil.findGameHWND()) != null){
            cancelAndStartNext();
            return;
        }
        final AtomicInteger launchCount = new AtomicInteger();
        scheduledFuture = launchProgramThreadPool.scheduleAtFixedRate(new LogRunnable(() -> {
            if (isPause.get().get()) {
                cancelGameTimer();
            } else {
                if (launchCount.incrementAndGet() > 4){
                    log.info("打开炉石失败次数过多，重新执行启动器链");
                    cancelGameTimer();
                    extraThreadPool.schedule(() -> {
                        SystemUtil.killLoginPlatform();
                        SystemUtil.killPlatform();
                        launchCount.set(0);
                        starter.start();
                    }, 1, TimeUnit.SECONDS);
                    return;
                }
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
        }), 5, 20, TimeUnit.SECONDS);
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
        GameUtil.hidePlatformWindow();
        extraThreadPool.schedule(() -> {
            ScriptStaticData.setGameHWND(gameHWND);
            SystemUtil.updateGameRect();
            startNextStarter();
        },1, TimeUnit.SECONDS);
    }
}
