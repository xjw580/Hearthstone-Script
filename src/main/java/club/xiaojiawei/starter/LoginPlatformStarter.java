package club.xiaojiawei.starter;

import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.dll.SystemDll;
import club.xiaojiawei.enums.ConfigurationEnum;
import club.xiaojiawei.utils.SystemUtil;
import com.sun.jna.platform.win32.WinDef;
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

    @Override
    protected void exec() {
        if (SystemUtil.isAliveOfGame()) {
            startNextStarter();
            return;
        }else if (Strings.isBlank(scriptConfiguration.getProperty(ConfigurationEnum.PLATFORM_PASSWORD.getKey()))){
            log.info(String.format("未配置%s账号密码，跳过此步骤", ScriptStaticData.PLATFORM_CN_NAME));
            startNextStarter();
            return;
        }
        final AtomicInteger loginCount = new AtomicInteger();
        scheduledFuture = extraThreadPool.scheduleAtFixedRate(() -> {
            WinDef.HWND loginPlatformHWND;
            if (isPause.get().get()){
                cancelLoginPlatformTimer();
            }else if ((loginPlatformHWND = SystemUtil.findLoginPlatformHWND()) == null){
                cancelAndStartNext();
            }else {
                if (loginCount.incrementAndGet() > 4){
                    log.info("登录战网失败次数过多，重新执行启动器链");
                    cancelLoginPlatformTimer();
                    extraThreadPool.schedule(() -> {
                        SystemUtil.killLoginPlatform();
                        SystemUtil.killPlatform();
                        loginCount.set(0);
                        starter.start();
                    }, 1, TimeUnit.SECONDS);
                    return;
                }
//                SystemUtil.frontWindow(loginPlatformHWND);
//                SystemUtil.delay(500);
                inputPassword(loginPlatformHWND);
                SystemUtil.delayShort();
                clickLoginButton(loginPlatformHWND);
            }
        }, 5, 15, TimeUnit.SECONDS);
    }

    public static void cancelLoginPlatformTimer(){
        if (scheduledFuture != null && !scheduledFuture.isDone()){
            scheduledFuture.cancel(true);
        }
    }

    private void inputPassword(WinDef.HWND loginPlatformHWND){
        log.info("输入战网密码中...");
        String password = scriptConfiguration.getProperty(ConfigurationEnum.PLATFORM_PASSWORD.getKey());
        if (Strings.isNotBlank(password)){
            SystemDll.INSTANCE.sendText(loginPlatformHWND, password, false);
        }
    }

    @Deprecated
    private void inputPassword(){
        log.info("输入战网密码中...");
        String password = scriptConfiguration.getProperty(ConfigurationEnum.PLATFORM_PASSWORD.getKey());
        if (Strings.isNotBlank(password)){
            SystemUtil.deleteAllContent();
            SystemUtil.copyToClipboard(password);
            SystemUtil.pasteFromClipboard();
        }
    }

    private void clickLoginButton(WinDef.HWND loginPlatformHWND){
        log.info("点击登入按钮");
        SystemDll.INSTANCE.clickLoginPlatformLoginBtn(loginPlatformHWND);
    }

    @Deprecated
    private void clickLoginButton(){
        log.info("点击登入按钮");
        SystemUtil.sendKey(KeyEvent.VK_TAB);
        SystemUtil.delay(500);
        SystemUtil.sendKey(KeyEvent.VK_ENTER);
    }

    public void cancelAndStartNext(){
        cancelLoginPlatformTimer();
        extraThreadPool.schedule(this::startNextStarter, 0, TimeUnit.SECONDS);
    }

}
