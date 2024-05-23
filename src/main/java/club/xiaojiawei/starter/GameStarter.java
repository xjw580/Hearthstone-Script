package club.xiaojiawei.starter;

import club.xiaojiawei.closer.StarterTaskCloser;
import club.xiaojiawei.custom.LogRunnable;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.utils.GameUtil;
import club.xiaojiawei.utils.MouseUtil;
import club.xiaojiawei.utils.RandomUtil;
import club.xiaojiawei.utils.SystemUtil;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import jakarta.annotation.Resource;
import javafx.beans.property.BooleanProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Properties;
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
public class GameStarter extends AbstractStarter implements StarterTaskCloser {

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
    @Resource
    private GameUtil gameUtil;

    private ScheduledFuture<?> scheduledFuture;

    private WinDef.HWND gameHWND;

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
                if (launchCount.incrementAndGet() > 3){
                    user32LaunchGame();
                }else if (launchCount.incrementAndGet() > 4){
                    log.warn("打开炉石失败次数过多，重新执行启动器链");
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
                        log.info("炉石传说已在运行，但未找到对应窗口句柄");
                        return;
                    }
                    cancelAndStartNext();
                }else {
                    gameUtil.cmdLaunchGame();
                }
            }
        }), 5, 20, TimeUnit.SECONDS);
    }

    private void user32LaunchGame(){
        log.info("正在通过user32打开" + ScriptStaticData.GAME_CN_NAME);
        WinDef.HWND platformhwnd = SystemUtil.findPlatformHWND();
        mouseUtil.leftButtonClickByUser32(platformhwnd, 145, 120);
    }

    @Deprecated
    private void robotLaunchGame(){
        log.info("正在通过robot打开" + ScriptStaticData.GAME_CN_NAME);
        WinDef.HWND platformHWND = SystemUtil.findPlatformHWND();
        WinDef.RECT rect = new WinDef.RECT();
        SystemUtil.frontWindow(platformHWND);
        SystemUtil.delay(200);
        User32.INSTANCE.MoveWindow(platformHWND, 0,  0, 0, 0, false);
        SystemUtil.delay(200);
        SystemUtil.updateRECT(platformHWND, rect);
        mouseUtil.leftButtonClick(rect.left + RandomUtil.getRandom(100, 150), rect.bottom - RandomUtil.getRandom(110, 125));
    }

    private void cancelGameTimer(){
        if (scheduledFuture != null && !scheduledFuture.isDone()){
            scheduledFuture.cancel(true);
        }
    }

    private void cancelAndStartNext(){
        log.info(ScriptStaticData.GAME_CN_NAME + "正在运行");
        cancelGameTimer();
        GameUtil.hidePlatformWindow();
        extraThreadPool.schedule(() -> {
            ScriptStaticData.setGameHWND(gameHWND);
            SystemUtil.updateGameRect();
            startNextStarter();
        },1, TimeUnit.SECONDS);
    }

    @Override
    public void closeStarterTask() {
        cancelGameTimer();
    }

}
