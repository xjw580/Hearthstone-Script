package club.xiaojiawei.starter;

import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.enums.ConfigurationEnum;
import club.xiaojiawei.utils.SystemUtil;
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
 * 启动战网
 * @author 肖嘉威
 * @date 2023/7/5 14:39
 */
@Slf4j
@Component
public class PlatformStarter extends AbstractStarter{
    @Resource
    private Properties scriptConfiguration;
    @Resource
    private AtomicReference<BooleanProperty> isPause;
    @Resource
    private ScheduledThreadPoolExecutor launchProgramThreadPool;
    @Resource
    private ScheduledThreadPoolExecutor extraThreadPool;
    private static ScheduledFuture<?> scheduledFuture;
    @Override
    public void exec() {
        try {
//            检测炉石传说是否存活
            if (Strings.isNotBlank(new String(Runtime.getRuntime().exec(ScriptStaticData.GAME_ALIVE_CMD).getInputStream().readAllBytes()))) {
                startNextStarter();
                return;
            }
            log.info("正在进入" + ScriptStaticData.PLATFORM_CN_NAME + ScriptStaticData.GAME_CN_NAME + "启动页");
            String platformPath = scriptConfiguration.getProperty(ConfigurationEnum.PLATFORM_PATH.getKey());
            Runtime.getRuntime().exec("\"" + platformPath + "\"" + " --exec=\"launch WTCG\"");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        scheduledFuture = launchProgramThreadPool.scheduleAtFixedRate(() -> {
            if (isPause.get().get()){
                cancelPlatformTimer();
            }else if (SystemUtil.findPlatformHWND() != null || SystemUtil.findLoginPlatformHWND() != null){
                cancelAndStartNext();
            }
        }, 1, 3, TimeUnit.SECONDS);
    }
    public static void cancelPlatformTimer(){
        if (scheduledFuture != null && !scheduledFuture.isDone()){
            log.info("已取消战网启动定时器");
            scheduledFuture.cancel(true);
        }
    }

    public void cancelAndStartNext(){
        extraThreadPool.schedule(() -> {
            cancelPlatformTimer();
            startNextStarter();
        }, 1, TimeUnit.SECONDS);
    }

}
