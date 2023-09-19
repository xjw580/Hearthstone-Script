package club.xiaojiawei.core;

import club.xiaojiawei.controller.JavaFXDashboardController;
import club.xiaojiawei.controller.JavaFXInitSettingsController;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.enums.DeckEnum;
import club.xiaojiawei.enums.ModeEnum;
import club.xiaojiawei.enums.WarPhaseEnum;
import club.xiaojiawei.initializer.AbstractInitializer;
import club.xiaojiawei.starter.AbstractStarter;
import club.xiaojiawei.status.Work;
import club.xiaojiawei.strategy.AbstractDeckStrategy;
import club.xiaojiawei.strategy.AbstractModeStrategy;
import club.xiaojiawei.strategy.AbstractPhaseStrategy;
import club.xiaojiawei.utils.SystemUtil;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author 肖嘉威
 * @date 2023/7/5 13:15
 * @msg
 */
@Component
@Slf4j
@Order(520)
public class Core implements ApplicationRunner {

    @Lazy
    @Resource
    private AbstractStarter starter;
    @Resource
    private AtomicReference<BooleanProperty> isPause;
    @Resource
    private AbstractInitializer initializer;
    @Resource
    private JavaFXDashboardController javaFXDashboardController;
    @Resource
    private JavaFXInitSettingsController javaFXInitSettingsController;
    @Resource
    private ThreadPoolExecutor coreThreadPool;

    /**
     * 启动脚本
     */
    public synchronized void start(){
        if (Work.isWorking()){
            log.warn("正在工作，无法重复工作");
            return;
        }
        Work.setWorking(true);
        coreThreadPool.execute(() -> {
            if (!ScriptStaticData.isSetPath()){
                SystemUtil.notice("需要配置" + ScriptStaticData.GAME_CN_NAME + "和" + ScriptStaticData.PLATFORM_CN_NAME + "的路径");
                Platform.runLater(() -> javaFXInitSettingsController.showStage());
                isPause.get().set(true);
            }else if (!isPause.get().get()){
                javaFXDashboardController.expandedLogPane();
                log.info("热键：Ctrl+P 开始/停止程序,Alt+P 关闭程序");
                starter.start();
            }
        });
    }

    /**
     * 重启脚本
     */
    public void restart(){
        coreThreadPool.execute(() -> {
            SystemUtil.killGame();
            isPause.get().set(true);
            isPause.get().set(false);
        });
    }

    @Override
    public void run(ApplicationArguments args){
        initializer.init();
    }
}
