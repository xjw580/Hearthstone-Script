package club.xiaojiawei;

import club.xiaojiawei.controller.JavaFXStartupController;
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
import java.util.*;
import java.util.List;
import java.util.Timer;
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
    private ChangeListener<Boolean> mainShowingListener;

    @Override
    public void start(Stage stage) throws IOException {
        Platform.setImplicitExit(false);
        showStartupPage();
        showMainPage();
    }

    private void showStartupPage(){
        Thread thread = new Thread(() -> Platform.runLater(() -> WindowUtil.showStage(WindowEnum.STARTUP)));
        thread.setName("StartupPage Thread");
        thread.start();
    }

    private void showMainPage(){
        Thread thread = new Thread(() -> {
            launchSpringBoot();
            setTray();
            Platform.runLater(() -> {
                Stage stage = WindowUtil.buildStage(WindowEnum.DASHBOARD);
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
        });
        thread.setName("MainPage Thread");
        thread.start();
    }

    private void launchSpringBoot(){
        ConfigurableApplicationContext springContext = new SpringApplicationBuilder(ScriptApplication.class).headless(false).run();
        springContext.getAutowireCapableBeanFactory().autowireBean(this);
    }

    private void setTray(){
        MenuItem quit = new MenuItem("退出");
        MenuItem show = new MenuItem("显示");
        quit.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Platform.runLater(() -> {
                    SystemUtil.removeTray();
                    System.exit(0);
                });
            }
        });
        show.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Platform.runLater(() -> WindowUtil.showStage(WindowEnum.DASHBOARD));
            }
        });
        SystemUtil.addTray(MAIN_ICO_NAME, ScriptStaticData.SCRIPT_NAME, e -> {
//            左键点击
            if (e.getButton() == 1){
                Platform.runLater(() -> WindowUtil.showStage(WindowEnum.DASHBOARD));
            }
        }, show, quit);
    }

    private void afterShowing(){
        JavaFXStartupController.complete();
        WindowUtil.hideStage(WindowEnum.STARTUP);
        DeckEnum deckEnum = DeckEnum.valueOf(scriptConfiguration.getProperty(DECK.getKey()));
        log.info(deckEnum.getComment() + "卡组代码：" + deckEnum.getDeckCode());
        if (SystemUtil.copyToClipboard(deckEnum.getDeckCode())){
            String content = deckEnum.getComment() + "卡组代码已经复制到剪切板";
            log.info(content);
            SystemUtil.notice(content);
        }
        log.info("脚本数据路径：" + springData.getScriptPath());
        List<String> args = this.getParameters().getRaw();
        if (!args.isEmpty() && Objects.equals("false", args.getFirst())){
            log.info("接收到开始参数，开始脚本");
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> isPause.get().set(false));
                }
            }, 1500);
        }
    }

    @Override
    public void run(ApplicationArguments args) {
        initializer.init();
    }
}