package club.xiaojiawei.starter;

import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.enums.ConfigurationEnum;
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
            if (SystemUtil.isAliveOfGame()) {
                startNextStarter();
                return;
            }
            log.info("正在进入" + ScriptStaticData.PLATFORM_CN_NAME + ScriptStaticData.GAME_CN_NAME + "启动页");
            String platformPath = scriptConfiguration.getProperty(ConfigurationEnum.PLATFORM_PATH.getKey());
            Runtime.getRuntime().exec("\"" + platformPath + "\"" + " --exec=\"launch WTCG\"");
            WinDef.HWND platformHWND = SystemUtil.findPlatformHWND();
            if (platformHWND != null){
                SystemUtil.delay(100);
                User32.INSTANCE.ShowWindow(platformHWND, WinUser.SW_MINIMIZE);
            }
        } catch (IOException e) {
            log.error("进入" + ScriptStaticData.PLATFORM_CN_NAME + ScriptStaticData.GAME_CN_NAME + "异常", e);
        }
        scheduledFuture = launchProgramThreadPool.scheduleAtFixedRate(() -> {
            if (isPause.get().get()){
                cancelPlatformTimer();
            }else if (SystemUtil.findPlatformHWND() != null || SystemUtil.findLoginPlatformHWND() != null){
                cancelAndStartNext();
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    public static void cancelPlatformTimer(){
        if (scheduledFuture != null && !scheduledFuture.isDone()){
            scheduledFuture.cancel(true);
        }
    }

    public void cancelAndStartNext(){
        cancelPlatformTimer();
        extraThreadPool.schedule(this::startNextStarter, 0, TimeUnit.SECONDS);
    }

}
