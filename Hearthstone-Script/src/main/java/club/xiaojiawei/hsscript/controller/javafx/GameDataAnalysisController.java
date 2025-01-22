package club.xiaojiawei.hsscript.controller.javafx;

import club.xiaojiawei.controls.Switch;
import club.xiaojiawei.hsscript.data.GameRationConst;
import club.xiaojiawei.hsscript.interfaces.StageHook;
import club.xiaojiawei.hsscript.utils.GameDataAnalysisUtil;
import club.xiaojiawei.status.War;
import club.xiaojiawei.status.WarKt;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.WindowEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

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

    private double canvasWidth, canvasHeight;

    private double calcHeight(double width) {
        return width / GameRationConst.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO;
    }

    private AnimationTimer animationTimer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        double padding = rootPane.getPadding().getLeft() + rootPane.getPadding().getRight();
        canvasWidth = 800;
        canvasHeight = calcHeight(canvasWidth);
        rootPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            double newWidth = newValue.intValue() - padding;
            canvasWidth = newWidth;
            canvasHeight = calcHeight(newWidth);
        });
        GameDataAnalysisUtil analysisUtil = GameDataAnalysisUtil.INSTANCE;
        analysisUtil.init(canvas);
//        War war = DeckStrategyUtil.INSTANCE.createMCTSWar();
        War war = WarKt.getWAR();
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                canvas.setWidth(canvasWidth);
                canvas.setHeight(canvasHeight);
                analysisUtil.draw(war, canvas);
            }
        };
        animationTimer.start();
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
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }

    @Override
    public void onCloseRequest(@NotNull WindowEvent event) {
    }
}
