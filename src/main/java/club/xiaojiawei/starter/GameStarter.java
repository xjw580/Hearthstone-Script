package club.xiaojiawei.starter;

import club.xiaojiawei.custom.LogRunnable;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.listener.ScreenFileListener;
import club.xiaojiawei.utils.MouseUtil;
import club.xiaojiawei.utils.RandomUtil;
import club.xiaojiawei.utils.SystemUtil;
import com.sun.jna.platform.win32.WinDef;
import javafx.beans.property.BooleanProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author 肖嘉威
 * @date 2023/7/5 14:38
 * @msg
 */
@Slf4j
@Component
public class GameStarter extends AbstractStarter{
    @Resource
    private Properties scriptProperties;
    @Resource
    private AtomicReference<BooleanProperty> isPause;
    @Resource
    private ScheduledThreadPoolExecutor launchProgramThreadPool;
    @Resource
    private SystemUtil systemUtil;
    @Resource
    private MouseUtil mouseUtil;
    @Resource
    private ScreenFileListener screenFileListener;
    private static ScheduledFuture<?> scheduledFuture;
    private static WinDef.HWND gameHWND;
    @Override
    public void start() {
        log.info("开始执行GameStarter");
        log.info("开始检查" + ScriptStaticData.GAME_CN_NAME);
        scheduledFuture = launchProgramThreadPool.scheduleAtFixedRate(new LogRunnable(() -> {
            if (isPause.get().get()) {
                cancelGameTimer();
            } else {
                try {
                    if (Strings.isNotBlank(new String(Runtime.getRuntime().exec(ScriptStaticData.GAME_MSG_CMD).getInputStream().readAllBytes()))) {
                        if ((gameHWND = SystemUtil.getHWND(ScriptStaticData.GAME_CN_NAME)) == null){
                            return;
                        }
                        commonExecute();
                    }else if ((gameHWND = SystemUtil.getHWND(ScriptStaticData.GAME_CN_NAME)) != null){
                        commonExecute();
                    }else {
                        log.info("正在打开" + ScriptStaticData.GAME_CN_NAME);
                        launchGame();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }), 0, 4, TimeUnit.SECONDS);
    }

    private void launchGame(){
        systemUtil.frontWindow(ScriptStaticData.getPlatformHWND());
        systemUtil.updateRect(ScriptStaticData.getPlatformHWND(), ScriptStaticData.PLATFORM_RECT);
        systemUtil.delayShort();
        mouseUtil.leftButtonClick(ScriptStaticData.PLATFORM_RECT.left + RandomUtil.getRandom(100, 150), ScriptStaticData.PLATFORM_RECT.bottom - RandomUtil.getRandom(110, 125));
    }

    public static void cancelGameTimer(){
        if (scheduledFuture != null && !scheduledFuture.isDone()){
            scheduledFuture.cancel(true);
        }
    }
    public void commonExecute(){
        log.info(ScriptStaticData.GAME_CN_NAME + "正在运行");
        ScriptStaticData.setGameHWND(gameHWND);
        systemUtil.updateRect(ScriptStaticData.getGameHWND(), ScriptStaticData.GAME_RECT);
        screenFileListener.listen();
        cancelGameTimer();
        if (nextStarter != null) {
            nextStarter.start();
        }
    }
}
