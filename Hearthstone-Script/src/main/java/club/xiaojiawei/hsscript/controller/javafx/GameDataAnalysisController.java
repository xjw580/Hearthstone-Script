package club.xiaojiawei.hsscript.controller.javafx;

import club.xiaojiawei.config.ThreadPoolConfigKt;
import club.xiaojiawei.controls.Switch;
import club.xiaojiawei.hsscript.data.GameRationConst;
import club.xiaojiawei.hsscript.utils.GameDataAnalysisUtil;
import club.xiaojiawei.status.War;
import club.xiaojiawei.status.WarKt;
import club.xiaojiawei.util.DeckStrategyUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author 肖嘉威
 * @date 2025/1/21 12:52
 */
public class GameDataAnalysisController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(GameDataAnalysisController.class);

    @FXML
    private StackPane canvasPane;
    @FXML
    private StackPane rootPane;
    @FXML
    private Canvas canvas;
    @FXML
    private Switch analysisSwitch;

    private final int frame = 30;

    private final int flushInterval = Math.max((int) (1000D / frame), 0);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        canvas.setWidth(rootPane.getPrefWidth() - 100);
        canvas.setHeight((canvas.getWidth() / GameRationConst.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO));
        canvasPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            canvas.setWidth(newValue.intValue());
        });
        canvasPane.heightProperty().addListener((observable, oldValue, newValue) -> {
            canvas.setHeight(newValue.intValue());
        });
        GameDataAnalysisUtil analysisUtil = GameDataAnalysisUtil.INSTANCE;
        analysisUtil.init(canvas);
        War war = DeckStrategyUtil.INSTANCE.createMCTSWar();
//        War war = WarKt.getWAR();
        ThreadPoolConfigKt.getEXTRA_THREAD_POOL().submit(() -> {
            while (true) {
                if (analysisSwitch.getStatus()) {
                    analysisUtil.draw(war, canvas);
                } else {
                    System.out.println("no draw");
                }
                try {
                    Thread.sleep(flushInterval);
                } catch (InterruptedException e) {
                    log.error("操作中断", e);
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
