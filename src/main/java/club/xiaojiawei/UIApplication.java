package club.xiaojiawei;

import club.xiaojiawei.controller.JavaFXStartupController;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.data.SpringData;
import club.xiaojiawei.enums.DeckEnum;
import club.xiaojiawei.enums.StageEnum;
import club.xiaojiawei.utils.SystemUtil;
import club.xiaojiawei.utils.WindowUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import static club.xiaojiawei.data.ScriptStaticData.MAIN_ICO_NAME;
import static club.xiaojiawei.enums.ConfigurationEnum.DECK;
import static javafx.stage.StageStyle.UNDECORATED;


/**
 * javaFX启动器
 * @author 肖嘉威
 * @date 2023/7/6 9:46
 */
@Component
@Slf4j
public class UIApplication extends Application {
    @Resource
    private Properties scriptConfiguration;
    @Resource
    private SpringData springData;
    @Resource
    private AtomicReference<BooleanProperty> isPause;
    @Override
    public void start(Stage stage) throws IOException {
        showStartupPage();
        showMainPage();
    }
    private void showStartupPage(){
        Stage startupStage = WindowUtil.getStage(StageEnum.STARTUP);
        startupStage.initStyle(UNDECORATED);
        startupStage.setAlwaysOnTop(true);
        startupStage.show();
    }
    private void showMainPage(){
        Thread thread = new Thread(() -> {
            try {
                int width = 220, height = 670;
                setStyle(getMainPageLoader(launchSpringBoot()), width, height);
                Rectangle2D bounds = Screen.getPrimary().getBounds();
                Platform.runLater(() -> {
                    Stage mainStage = WindowUtil.getStage(StageEnum.DASHBOARD);
                    mainStage.setX((bounds.getWidth() - width + 5));
                    mainStage.setY((bounds.getHeight() - height) / 2);
//                    mainStage.setWidth(width);
//                    mainStage.setHeight(height);
                    mainStage.setAlwaysOnTop(true);
                    mainStage.show();
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            setTray();
            afterInit();
        });
        thread.setName("MainPage Thread");
        thread.start();
    }
    private ConfigurableApplicationContext launchSpringBoot(){
        ConfigurableApplicationContext springContext = new SpringApplicationBuilder(ScriptApplication.class).headless(false).run();
        springContext.getAutowireCapableBeanFactory().autowireBean(this);
        return springContext;
    }
    private FXMLLoader getMainPageLoader(ConfigurableApplicationContext springContext){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
        fxmlLoader.setControllerFactory(springContext::getBean);
        return fxmlLoader;
    }
    private void setStyle(FXMLLoader fxmlLoader, int width, int height) throws IOException {
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(JavaFXUI.javafxUIStylesheet());
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
                Platform.runLater(() -> WindowUtil.showStage(StageEnum.DASHBOARD));
            }
        });
        SystemUtil.addTray(MAIN_ICO_NAME, ScriptStaticData.SCRIPT_NAME, e -> {
//            左键点击
            if (e.getButton() == 1){
                Platform.runLater(() -> WindowUtil.showStage(StageEnum.DASHBOARD));
            }
        }, show, quit);
    }
    private void afterInit(){
        DeckEnum deckEnum = DeckEnum.valueOf(scriptConfiguration.getProperty(DECK.getKey()));
        log.info(deckEnum.getComment() + "卡组代码：" + deckEnum.getDeckCode());
        if (SystemUtil.copyToClipboard(deckEnum.getDeckCode())){
            String content = deckEnum.getComment() + "卡组代码已经复制到剪切板";
            log.info(content);
            SystemUtil.notice(content);
        }
        log.info("脚本数据路径：" + springData.getScriptPath());
        JavaFXStartupController.complete();
        Platform.runLater(() -> WindowUtil.hideStage(StageEnum.STARTUP));
        List<String> args = this.getParameters().getRaw();
        if (!args.isEmpty() && Objects.equals("false", args.get(0))){
            isPause.get().set(false);
        }
    }
}