package club.xiaojiawei.controller;

import club.xiaojiawei.run.Core;
import club.xiaojiawei.status.War;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.xiaojiawei.Switch;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author zerg
 */
public class DashboardController implements Initializable {

    @FXML
    private ScrollPane logScrollPane;
    @FXML
    private VBox logVBox;
    @FXML
    private Accordion accordion;
    @FXML
    private Button startButton;
    @FXML
    private Button pauseButton;
    @FXML
    private TitledPane titledPaneLog;
    @FXML
    private Text gameCount;

    public static VBox logVBoxBack;
    public static Accordion accordionBack;
    public static Stage settingsStage;
    public static Button startButtonBack;
    public static Button pauseButtonBack;

    @FXML
    protected void start(){
        Core.setPause(false);
        accordion.setExpandedPane(titledPaneLog);
        Core.openGame();
    }

    @FXML
    protected void pause(){
        Core.setPause(true);
    }

    @FXML
    protected void settings() {
        Platform.runLater(() -> Platform.runLater(() -> {
            Stage stage = new Stage();
            try {
                Scene scene = new Scene(new FXMLLoader(getClass().getResource("/club/xiaojiawei/settings.fxml")).load(), 600, 400);
                stage.setScene(scene);
                scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            stage.setTitle("设置");
            stage.getIcons().add(new Image(getClass().getResource("/club/xiaojiawei/images/main.png").toExternalForm()));
            settingsStage = stage;
            stage.show();
        }));
    }

    @FXML
    private Switch logSwitch;
    public static Switch logSwitchBack;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logVBoxBack = logVBox;
        accordionBack = accordion;
        logSwitchBack = logSwitch;
        startButtonBack = startButton;
        pauseButtonBack = pauseButton;
//        监听日志
        logVBox.heightProperty().addListener((observable, oldValue, newValue) -> logScrollPane.setVvalue(logScrollPane.getVmax()));
//        监听局数
        War.warCount.addListener((observable, oldValue, newValue) -> gameCount.setText(newValue.toString()));

    }

    public static void changePauseStyle(boolean isPause){
        startButtonBack.setDisable(!isPause);
        pauseButtonBack.setDisable(isPause);
    }
}