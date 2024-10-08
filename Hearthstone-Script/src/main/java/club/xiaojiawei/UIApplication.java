package club.xiaojiawei;

import club.xiaojiawei.bean.CommonCardAction;
import club.xiaojiawei.bean.LogRunnable;
import club.xiaojiawei.bean.LogThread;
import club.xiaojiawei.config.ThreadPoolConfigKt;
import club.xiaojiawei.controller.javafx.StartupController;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.dll.SystemDll;
import club.xiaojiawei.enums.WindowEnum;
import club.xiaojiawei.utils.SystemUtil;
import club.xiaojiawei.utils.WindowUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.stage.Stage;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static club.xiaojiawei.data.ScriptStaticData.MAIN_IMG_PNG_NAME;

/**
 * javaFX启动器
 * @author 肖嘉威
 * @date 2023/7/6 9:46
 */
public class UIApplication extends Application implements ApplicationRunner {

    private ChangeListener<Boolean> mainShowingListener;

    @Override
    public void start(Stage stage) throws IOException {
        CardAction.Companion.setCommonActionFactory(CommonCardAction.Companion.getDEFAULT()::createNewInstance);
        Platform.setImplicitExit(false);
        Thread.ofVirtual().name("Launch VThread").start(new LogRunnable(() -> {
            launchSpringBoot();
            setSystemTray();
            Platform.runLater(this::showMainPage);
        }));
        showStartupPage();
    }

    private void showStartupPage(){
        WindowUtil.showStage(WindowEnum.STARTUP);
    }

    private void showMainPage(){
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
        SystemUtil.addTray(MAIN_IMG_PNG_NAME, ScriptStaticData.SCRIPT_NAME, e -> {
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

        Runtime.getRuntime().addShutdownHook(new LogThread(() -> SystemDll.INSTANCE.uninstallDll(SystemUtil.findGameHWND())));

        List<String> args = this.getParameters().getRaw();

        String pause = "";
        for (String arg : args) {
            if (arg.startsWith("--pause=")) {
                String[] split = arg.split("=", 2);
                if (split.length > 1) {
                    pause = split[1];
                }
            }
        }
        if (Objects.equals("false", pause)){
            log.info("接收到开始参数，开始脚本");
            ThreadPoolConfigKt.getEXTRA_THREAD_POOL().schedule(() -> Platform.runLater(() -> isPause.get().set(false)), 1500, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void run(ApplicationArguments args) {
        ThreadPoolConfigKt.getEXTRA_THREAD_POOL().submit(() -> initializer.init());
    }

}