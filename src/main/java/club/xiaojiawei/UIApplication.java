package club.xiaojiawei;

import club.xiaojiawei.bean.LogRunnable;
import club.xiaojiawei.controller.javafx.StartupController;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.data.SpringData;
import club.xiaojiawei.enums.DeckEnum;
import club.xiaojiawei.enums.WindowEnum;
import club.xiaojiawei.initializer.AbstractInitializer;
import club.xiaojiawei.utils.SystemUtil;
import club.xiaojiawei.utils.WindowUtil;
import jakarta.annotation.Resource;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static club.xiaojiawei.data.ScriptStaticData.MAIN_ICO_NAME;
import static club.xiaojiawei.enums.ConfigurationEnum.DECK;

/**
 * javaFX启动器
 * @author 肖嘉威
 * @date 2023/7/6 9:46
 */
@Slf4j
@Component
@Order(520)
public class UIApplication extends Application implements ApplicationRunner {

    @Lazy
    @Resource
    private AbstractInitializer initializer;
    @Resource
    private Properties scriptConfiguration;
    @Resource
    private SpringData springData;
    @Resource
    private AtomicReference<BooleanProperty> isPause;
    @Resource
    private ScheduledThreadPoolExecutor extraThreadPool;

    private ChangeListener<Boolean> mainShowingListener;

    @Override
    public void start(Stage stage) throws IOException {
        Platform.setImplicitExit(false);
        showMainPage();
        showStartupPage();
    }

    private void showStartupPage(){
        WindowUtil.showStage(WindowEnum.STARTUP);
    }

    private void showMainPage(){
        Thread.ofVirtual().name("MainPage VThread").start(new LogRunnable(() -> {
            launchSpringBoot();
            setSystemTray();
            Platform.runLater(() -> {
                Stage stage = WindowUtil.buildStage(WindowEnum.MAIN);
                mainShowingListener = (observableValue, aBoolean, t1) -> {
                    if (t1) {
                        stage.showingProperty().removeListener(mainShowingListener);
                        mainShowingListener = null;
                        afterShowing();
                    }
                };
                stage.showingProperty().addListener(mainShowingListener);
                stage.show();
            });
        }));
    }

    private void launchSpringBoot(){
        ConfigurableApplicationContext springContext = new SpringApplicationBuilder(ScriptApplication.class).headless(false).run();
        springContext.getAutowireCapableBeanFactory().autowireBean(this);
    }

    private void setSystemTray(){
        MenuItem quit = new MenuItem("退出");
        quit.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SystemUtil.shutdown();
            }
        });
        SystemUtil.addTray(MAIN_ICO_NAME, ScriptStaticData.SCRIPT_NAME, e -> {
//            左键点击
            if (e.getButton() == 1){
                Platform.runLater(() -> {
                    Stage stage = WindowUtil.getStage(WindowEnum.MAIN);
                    if (stage.isShowing()){
                        WindowUtil.hideStage(WindowEnum.MAIN);
                    }else {
                        WindowUtil.showStage(WindowEnum.MAIN);
                    }
                });
            }
        }, quit);
    }

    private void afterShowing(){
        StartupController.complete();

        DeckEnum deckEnum = DeckEnum.valueOf(scriptConfiguration.getProperty(DECK.getKey()));
        log.info(deckEnum.getComment() + "卡组代码：");
        log.info("$" + deckEnum.getDeckCode());
        log.info("脚本数据路径：" + springData.getScriptPath());

        List<String> args = this.getParameters().getRaw();
        if (!args.isEmpty() && Objects.equals("false", args.getFirst())){
            log.info("接收到开始参数，开始脚本");
            extraThreadPool.schedule(() -> Platform.runLater(() -> isPause.get().set(false)), 1500, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void run(ApplicationArguments args) {
        initializer.init();
    }

}