package club.xiaojiawei.starter;

import club.xiaojiawei.custom.LogRunnable;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.enums.ConfigurationKeyEnum;
import club.xiaojiawei.utils.SystemUtil;
import com.sun.jna.platform.win32.WinDef;
import javafx.beans.property.BooleanProperty;
import lombok.extern.slf4j.Slf4j;
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
    private static ScheduledFuture<?> scheduledFuture;
    private static Process process;
    private static WinDef.HWND platformHWND;
    @Override
    public void start() {
        log.info("开始执行PlatformStarter");
        log.info("开始检查" + ScriptStaticData.PLATFORM_CN_NAME);
        scheduledFuture = launchProgramThreadPool.scheduleAtFixedRate(new LogRunnable(() -> {
            if (isPause.get().get()){
                cancelPlatformTimer();
            }else if ((process != null && process.isAlive())){
                if ((platformHWND = SystemUtil.getHWND(ScriptStaticData.PLATFORM_CN_NAME)) == null){
                    return;
                }
                commonExecute();
            }else if ((platformHWND = SystemUtil.getHWND(ScriptStaticData.PLATFORM_CN_NAME)) != null){
                commonExecute();
            }else{
                log.info("正在打开" + ScriptStaticData.PLATFORM_CN_NAME);
                String platformPath = scriptProperties.getProperty(ConfigurationKeyEnum.PLATFORM_PATH_KEY.getKey());
                try {
//                    Runtime.getRuntime().exec("cmd /c " + platformPath);
                    process = new ProcessBuilder(platformPath).start();
                } catch (IOException e) {
                    throw new RuntimeException(ScriptStaticData.PLATFORM_CN_NAME + "启动异常", e);
                }
            }
        }), 0, 4, TimeUnit.SECONDS);
    }
    public static void cancelPlatformTimer(){
        if (scheduledFuture != null && !scheduledFuture.isDone()){
            scheduledFuture.cancel(true);
        }
    }

    public void commonExecute(){
        log.info(ScriptStaticData.PLATFORM_CN_NAME + "正在运行");
        ScriptStaticData.setPlatformHWND(platformHWND);
        cancelPlatformTimer();
        if (nextStarter != null){
            nextStarter.start();
        }
    }

}
