package club.xiaojiawei.hsscript.controller.javafx;

import club.xiaojiawei.config.ThreadPoolConfigKt;
import club.xiaojiawei.controls.Switch;
import club.xiaojiawei.hsscript.data.GameRationConst;
import club.xiaojiawei.hsscript.data.ScriptDataKt;
import club.xiaojiawei.status.War;
import club.xiaojiawei.status.WarKt;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelBuffer;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.net.URL;
import java.nio.IntBuffer;
import java.util.Random;
import java.util.ResourceBundle;

/**
 * @author 肖嘉威
 * @date 2025/1/21 12:52
 */
public class GameDataAnalysisController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(GameDataAnalysisController.class);
    @FXML
    private StackPane rootPane;
    @FXML
    private Canvas canvas;
    @FXML
    private Switch analysisSwitch;

    private final int frame = 60;

    private final int flushInterval = (int) (1000D / frame);

    private War war;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        double width = 800;
        double height = width / GameRationConst.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO;
        canvas.setWidth(width);
        canvas.setHeight(height);
        war = WarKt.getWAR();
        GraphicsContext context = canvas.getGraphicsContext2D();
        ThreadPoolConfigKt.getEXTRA_THREAD_POOL().submit(() -> {
            while (isRunning()) {
                context.setFill(Color.WHITE);
                context.fillRect(0, 0, width, height);
//                war.getMe()
                try {
                    Thread.sleep(flushInterval);
                } catch (InterruptedException e) {
                    log.error("操作中断", e);
                    break;
                }
            }
        });
    }

    private boolean isRunning() {
        Scene scene = rootPane.getScene();
        if (scene != null) {
            Window window = scene.getWindow();
            if (window != null) {
                return window.isShowing();
            }
        }
        return true;
    }


}
