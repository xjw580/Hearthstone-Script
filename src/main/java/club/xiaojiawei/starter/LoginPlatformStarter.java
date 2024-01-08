package club.xiaojiawei.starter;

import club.xiaojiawei.enums.ConfigurationEnum;
import club.xiaojiawei.utils.SystemUtil;
import jakarta.annotation.Resource;
import javafx.beans.property.BooleanProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.awt.event.KeyEvent;
import java.util.Properties;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 登录战网
 * @author 肖嘉威
 * @date 2023/10/13 22:24
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
    @Lazy
    @Resource
    private AbstractStarter starter;
    private static ScheduledFuture<?> scheduledFuture;
    private static final AtomicInteger COUNT = new AtomicInteger();
    @Override
    protected void exec() {
        if (SystemUtil.findLoginPlatformHWND() == null){
            cancelAndStartNext();
            return;
        }
        scheduledFuture = extraThreadPool.scheduleAtFixedRate(() -> {
            if (isPause.get().get()){
                cancelLoginPlatformTimer();
            }else if (SystemUtil.findLoginPlatformHWND() == null){
                cancelAndStartNext();
            }else {
                if (COUNT.incrementAndGet() == 5){
                    log.info("登录战网失败次数过多，重新执行启动器链");
                    COUNT.set(0);
                    extraThreadPool.schedule(() -> {
                        cancelLoginPlatformTimer();
                        starter.start();
                    }, 1, TimeUnit.SECONDS);
                    return;
                }
                SystemUtil.frontWindow(SystemUtil.findLoginPlatformHWND());
                SystemUtil.delay(500);
                if (!inputPassword()){
                    log.warn("未设置战网账号密码");
                    startNextStarter();
                    return;
                }
                SystemUtil.delay(500);
                clickLoginButton();
            }
        }, 5, 15, TimeUnit.SECONDS);
    }

    public static void cancelLoginPlatformTimer(){
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
            cancelLoginPlatformTimer();
            startNextStarter();
        }, 1, TimeUnit.SECONDS);
    }

}
