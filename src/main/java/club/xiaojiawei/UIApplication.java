package club.xiaojiawei;

import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.data.SpringData;
import club.xiaojiawei.enums.DeckEnum;
import club.xiaojiawei.utils.FrameUtil;
import club.xiaojiawei.utils.SystemUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.kordamp.bootstrapfx.BootstrapFX;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import static club.xiaojiawei.data.ScriptStaticData.MAIN_ICO_NAME;
import static club.xiaojiawei.enums.ConfigurationKeyEnum.DECK_KEY;


/**
 * @author 肖嘉威
 * @date 2023/7/6 9:46
 * @msg javafx启动器
 */
@Component
@Slf4j
public class UIApplication extends Application {
    @Resource
    private Properties scriptConfiguration;
    @Resource
    private SpringData springData;
    private static AtomicReference<JFrame> frame;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = getLoader();
        setStyle(fxmlLoader);
        setTray();
        afterInit();
    }
    private FXMLLoader getLoader(){
        ConfigurableApplicationContext springContext = new SpringApplicationBuilder(ScriptApplication.class).headless(false).run();
        springContext.getAutowireCapableBeanFactory().autowireBean(this);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
        fxmlLoader.setControllerFactory(springContext::getBean);
        return fxmlLoader;
    }
    private void setStyle(FXMLLoader fxmlLoader) throws IOException {
        int width = 225, height = 650;
        Scene scene = new Scene(fxmlLoader.load(), width, height);
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("css/dashboard.css")).toExternalForm());
        frame = FrameUtil.createAlwaysTopWindow(ScriptStaticData.SCRIPT_NAME, scene, width, height, ScriptStaticData.SCRIPT_ICON_PATH);
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
                Platform.runLater(() -> {
                    if (!frame.get().isVisible()){
                        frame.get().setVisible(true);
                    }
                });
            }
        });
        SystemUtil.addTray(MAIN_ICO_NAME, ScriptStaticData.SCRIPT_NAME, show, quit);
    }
    private void afterInit(){
        DeckEnum deckEnum = DeckEnum.valueOf(scriptConfiguration.getProperty(DECK_KEY.getKey()));
        log.info(deckEnum.getComment() + "卡组代码：" + deckEnum.getDeckCode());
        if (SystemUtil.pasteClipboard(deckEnum.getDeckCode())){
            log.info(deckEnum.getComment() + "卡组代码已经粘贴到剪切板");
            SystemUtil.notice(deckEnum.getComment() + "卡组代码已经粘贴到剪切板");
        }
        log.info("脚本数据路径：" + springData.getScriptPath());
    }
}