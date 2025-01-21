package club.xiaojiawei.hsscript.controller.javafx;

import club.xiaojiawei.config.ThreadPoolConfigKt;
import club.xiaojiawei.controls.Switch;
import club.xiaojiawei.hsscript.data.GameRationConst;
import club.xiaojiawei.hsscript.interfaces.StageHook;
import club.xiaojiawei.hsscript.utils.GameDataAnalysisUtil;
import club.xiaojiawei.hsscript.utils.GameUtil;
import club.xiaojiawei.hsscript.utils.SystemUtil;
import club.xiaojiawei.status.War;
import club.xiaojiawei.status.WarKt;
import club.xiaojiawei.util.RandomUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Future;

/**
 * @author 肖嘉威
 * @date 2025/1/21 12:52
 */
public class GameDataAnalysisController implements Initializable, StageHook {

    private static final Logger log = LoggerFactory.getLogger(GameDataAnalysisController.class);

    @FXML
    private Label switchLabel;
    @FXML
    private StackPane canvasPane;
    @FXML
    private StackPane rootPane;
    @FXML
    private Canvas canvas;
    @FXML
    private Switch analysisSwitch;

    private final int frame = 20;

    private final int flushInterval = Math.max((int) (1000D / frame), 0);

    private Future<?> drawTask;

    private volatile boolean isRunning = true;

    private double canvasWidth, canvasHeight;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        canvas.setWidth(rootPane.getPrefWidth() - 100);
//        canvas.setHeight((canvas.getWidth() / GameRationConst.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO));
        double padding = rootPane.getPadding().getLeft() + rootPane.getPadding().getRight();
        System.out.println("padding = " + padding);
        rootPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            double newWidth = newValue.intValue() - padding;
            canvasWidth = newWidth;
            canvasHeight = newWidth / GameRationConst.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO;
        });
        GameDataAnalysisUtil analysisUtil = GameDataAnalysisUtil.INSTANCE;
        analysisUtil.init(canvas);
//        War war = DeckStrategyUtil.INSTANCE.createMCTSWar();
        War war = WarKt.getWAR();
        drawTask = ThreadPoolConfigKt.getEXTRA_THREAD_POOL().submit(() -> {
            while (isRunning) {
                if (analysisSwitch.getStatus()) {
                    if (canvasWidth > 0 && canvasHeight > 0) {
                        canvas.setWidth(canvasWidth);
                        canvas.setHeight(canvasHeight);
                        analysisUtil.draw(war, canvas);
                    }
                }
                try {
                    Thread.sleep(flushInterval);
                } catch (InterruptedException e) {
                    break;
                }
            }
            log.info("绘制结束");
        });
    }


    @Override
    public void onShown() {
    }

    @Override
    public void onShowing() {
    }

    @Override
    public void onHidden() {
    }

    @Override
    public void onHiding() {
        if (drawTask != null) {
            drawTask.cancel(true);
            isRunning = false;
        }
    }

    @Override
    public void onCloseRequest(@NotNull WindowEvent event) {
    }
}
