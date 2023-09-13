package club.xiaojiawei.starter;

import club.xiaojiawei.custom.LogRunnable;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.enums.ConfigurationKeyEnum;
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
 * @author 肖嘉威
 * @date 2023/7/5 14:39
 * @msg
 */
@Slf4j
@Component
public class PlatformStarter extends AbstractStarter{
    @Resource
    private Properties scriptProperties;
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
            if (Strings.isNotBlank(new String(Runtime.getRuntime().exec(ScriptStaticData.GAME_ALIVE_CMD).getInputStream().readAllBytes()))) {
                nextStarter.start();
                return;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("开始检查" + ScriptStaticData.PLATFORM_CN_NAME);
        log.info("正在进入" + ScriptStaticData.PLATFORM_CN_NAME + ScriptStaticData.GAME_CN_NAME + "启动页");
        String platformPath = scriptProperties.getProperty(ConfigurationKeyEnum.PLATFORM_PATH_KEY.getKey());
        try {
            Runtime.getRuntime().exec("\"" + platformPath + "\"" + " --exec=\"launch WTCG\"");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        scheduledFuture = launchProgramThreadPool.scheduleAtFixedRate(new LogRunnable(() -> {
            if (isPause.get().get()){
                cancelPlatformTimer();
            }else if (SystemUtil.getHWND(ScriptStaticData.PLATFORM_CN_NAME) != null){
                commonExecute();
            }
        }), 1, 4, TimeUnit.SECONDS);
    }
    public static void cancelPlatformTimer(){
        if (scheduledFuture != null && !scheduledFuture.isDone()){
            log.info("已取消战网启动定时器");
            scheduledFuture.cancel(true);
        }
    }

    public void commonExecute(){
        log.info(ScriptStaticData.PLATFORM_CN_NAME + "正在运行");
        extraThreadPool.schedule(new LogRunnable(() -> {
            cancelPlatformTimer();
            if (nextStarter != null){
                nextStarter.start();
            }
        }), 1, TimeUnit.SECONDS);
    }

}
