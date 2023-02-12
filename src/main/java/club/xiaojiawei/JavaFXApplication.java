package club.xiaojiawei;

import club.xiaojiawei.constant.SystemConst;
import club.xiaojiawei.utils.FrameUtil;
import club.xiaojiawei.utils.SystemUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import static club.xiaojiawei.constant.SystemConst.*;

/**
 * @author zerg
 */
public class JavaFXApplication extends Application {

    public static AtomicReference<JFrame> frame;

    @Override
    public void start(Stage stage) throws IOException {
        int width = (int) (SCREEN_WIDTH / 7.6 / UI_SCALE_X), height = (int) (SCREEN_HEIGHT / 1.4 / UI_SCALE_Y);
        Scene scene = new Scene(new FXMLLoader(getClass().getResource("dashboard.fxml")).load(), width, height);
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        scene.getStylesheets().add(getClass().getResource("css/dashboard.css").toExternalForm());
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
        SystemUtil.addTray("main.png", "HS脚本", show, quit);
        frame = FrameUtil.createTopWindow("HS脚本控制台", scene, width, height, "main.png");
    }

    public static void main(String[] args) {
        launch();
    }
}