package club.xiaojiawei.starter;

import club.xiaojiawei.config.ThreadPoolConfigKt;
import club.xiaojiawei.interfaces.closer.StarterTaskCloser;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.utils.GameUtil;
import club.xiaojiawei.utils.SystemUtil;
import jakarta.annotation.Resource;
import javafx.beans.property.BooleanProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 启动战网
 * @author 肖嘉威
 * @date 2023/7/5 14:39
 */
@Slf4j
@Component
public class PlatformStarter extends AbstractStarter implements StarterTaskCloser {

    @Resource
    private AtomicReference<BooleanProperty> isPause;
    @Resource
    private GameUtil gameUtil;
    private ScheduledFuture<?> scheduledFuture;

    @Override
    public void exec() {
        if (SystemUtil.isAliveOfGame()) {
            startNextStarter();
            return;
        }
        log.info("正在进入" + ScriptStaticData.PLATFORM_CN_NAME + ScriptStaticData.GAME_CN_NAME + "启动页");
        GameUtil.cmdLaunchGame();
        GameUtil.hidePlatformWindow();
        scheduledFuture = ThreadPoolConfigKt.getLAUNCH_PROGRAM_THREAD_POOL().scheduleAtFixedRate(() -> {
            if (isPause.get().get()){
                cancelPlatformTimer();
            }else if (SystemUtil.findPlatformHWND() != null || SystemUtil.findLoginPlatformHWND() != null){
                cancelAndStartNext();
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    private void cancelPlatformTimer(){
        if (scheduledFuture != null && !scheduledFuture.isDone()){
            scheduledFuture.cancel(true);
        }
    }

    private void cancelAndStartNext(){
        cancelPlatformTimer();
        ThreadPoolConfigKt.getEXTRA_THREAD_POOL().schedule(this::startNextStarter, 0, TimeUnit.SECONDS);
    }

    @Override
    public void closeStarterTask() {
        cancelPlatformTimer();
    }

    @Override
    public int getOrder() {
        return 90;
    }
}
