package club.xiaojiawei.core;

import club.xiaojiawei.controller.DashboardController;
import club.xiaojiawei.controller.InitSettingsController;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.enums.DeckEnum;
import club.xiaojiawei.enums.ModeEnum;
import club.xiaojiawei.enums.WarPhaseEnum;
import club.xiaojiawei.initializer.AbstractInitializer;
import club.xiaojiawei.starter.AbstractStarter;
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
    private SystemUtil systemUtil;
    @Resource
    private AbstractInitializer initializer;
    @Resource
    private ConfigurableApplicationContext springContext;
    @Resource
    private DashboardController dashboardController;
    @Resource
    private InitSettingsController initSettingsController;
    @Resource
    private ThreadPoolExecutor coreThreadPool;

    /**
     * 启动需要的程序
     */
    public void start(){
        coreThreadPool.execute(() -> {
            if (!ScriptStaticData.isSetPath()){
                systemUtil.notice("需要配置" + ScriptStaticData.GAME_CN_NAME + "和" + ScriptStaticData.PLATFORM_CN_NAME + "的路径");
                Platform.runLater(() -> initSettingsController.showStage());
                isPause.get().set(true);
            }else if (!isPause.get().get()){
                dashboardController.expandedLogPane();
                log.info("热键：Ctrl+P 开始/停止程序,Alt+P 关闭程序");
                starter.start();
            }
        });
    }

    public void restart(){
        coreThreadPool.execute(() -> {
            isPause.get().set(true);
            try {
//          等待脚本将监听资源关闭
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            systemUtil.killGame();
            isPause.get().set(false);
        });
    }

    @Override
    public void run(ApplicationArguments args) {
        for (WarPhaseEnum phase : WarPhaseEnum.values()) {
            Class<? extends AbstractPhaseStrategy<String>> phaseStrategyClass = phase.getPhaseStrategyClass();
            if (phaseStrategyClass != null){
                phase.setAbstractPhaseStrategy(springContext.getBean(phaseStrategyClass));
            }
        }
        for (ModeEnum mode : ModeEnum.values()) {
            Class<? extends AbstractModeStrategy<Object>> modeStrategyClass = mode.getModeStrategyClass();
            if (modeStrategyClass != null){
                mode.setAbstractModeStrategy(springContext.getBean(modeStrategyClass));
            }
        }
        for (DeckEnum deck : DeckEnum.values()) {
            Class<? extends AbstractDeckStrategy> deckStrategyClass = deck.getAbstractDeckStrategyClass();
            if (deckStrategyClass != null){
                deck.setAbstractDeckStrategy(springContext.getBean(deckStrategyClass));
            }
        }
        initializer.init();
    }
}
