package club.xiaojiawei.starter;

import club.xiaojiawei.enums.ConfigurationEnum;
import club.xiaojiawei.utils.MouseUtil;
import club.xiaojiawei.utils.SystemUtil;
import com.sun.jna.platform.win32.WinDef;
import javafx.beans.property.BooleanProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.awt.event.KeyEvent;
import java.security.Key;
import java.util.Properties;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author 肖嘉威
 * @date 2023/10/13 22:24
 * @msg
 */
@Component
@Slf4j
public class LoginPlatformStarter extends AbstractStarter{

    @Resource
    private Properties scriptConfiguration;
    @Resource
    private ScheduledThreadPoolExecutor extraThreadPool;
    @Resource
    private AtomicReference<BooleanProperty> isPause;
    private static ScheduledFuture<?> scheduledFuture;
    @Override
    protected void exec() {
        if (SystemUtil.findLoginPlatformHWND() == null){
            startNextStarter();
        }else {
            //        停顿以判断能否自动登录
            SystemUtil.delay(4000);
            WinDef.HWND hwnd;
            if ((hwnd = SystemUtil.findLoginPlatformHWND()) == null){
                startNextStarter();
            }else {
                SystemUtil.frontWindow(hwnd);
                SystemUtil.delay(500);
                if (!inputPassword()){
                    log.warn("未设置战网账号密码");
                    startNextStarter();
                    return;
                }
                SystemUtil.delay(500);
                clickLoginButton();
                scheduledFuture = extraThreadPool.scheduleAtFixedRate(() -> {
                    if (isPause.get().get()){
                        cancelPlatformTimer();
                    }else if (SystemUtil.findPlatformHWND() != null){
                        cancelAndStartNext();
                    }
                }, 2, 2, TimeUnit.SECONDS);
            }
        }
    }

    public static void cancelPlatformTimer(){
        if (scheduledFuture != null && !scheduledFuture.isDone()){
            log.info("已取消战网登录定时器");
            scheduledFuture.cancel(true);
        }
    }

    private boolean inputPassword(){
        String password = scriptConfiguration.getProperty(ConfigurationEnum.PLATFORM_PASSWORD.getKey());
        if (Strings.isBlank(password)){
            return false;
        }
        log.info("输入战网密码中...");
        SystemUtil.deleteAllContent();
        SystemUtil.copyToClipboard(password);
        SystemUtil.pasteFromClipboard();
        return true;
    }

    private void clickLoginButton(){
        log.info("点击登录按钮");
        SystemUtil.sendKey(KeyEvent.VK_TAB);
        SystemUtil.delay(500);
        SystemUtil.sendKey(KeyEvent.VK_ENTER);
    }

    public void cancelAndStartNext(){
        extraThreadPool.schedule(() -> {
            cancelPlatformTimer();
            startNextStarter();
        }, 1, TimeUnit.SECONDS);
    }

}