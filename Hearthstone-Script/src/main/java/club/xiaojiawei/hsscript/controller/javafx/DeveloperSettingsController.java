package club.xiaojiawei.hsscript.controller.javafx;

import ch.qos.logback.classic.Level;
import club.xiaojiawei.controls.NotificationManager;
import club.xiaojiawei.controls.Switch;
import club.xiaojiawei.hsscript.data.PathDataKt;
import club.xiaojiawei.hsscript.enums.ConfigEnum;
import club.xiaojiawei.hsscript.enums.WindowEnum;
import club.xiaojiawei.hsscript.utils.ConfigExUtil;
import club.xiaojiawei.hsscript.utils.ConfigUtil;
import club.xiaojiawei.hsscript.utils.WindowUtil;
import club.xiaojiawei.hsscript.utils.main.MeasureApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author 肖嘉威
 * @date 2025/1/20 22:38
 */
public class DeveloperSettingsController implements Initializable {

    @FXML
    private Switch autoOpenAnalysis;
    @FXML
    private NotificationManager<String> notificationManager;
    @FXML
    private ComboBox<String> fileLogLevelComboBox;
    @FXML
    private Switch strategySwitch;

    private void initValue() {
        strategySwitch.setStatus(ConfigUtil.INSTANCE.getBoolean(ConfigEnum.STRATEGY));
        fileLogLevelComboBox.setValue(ConfigExUtil.INSTANCE.getFileLogLevel().levelStr.toUpperCase(Locale.ROOT));
        autoOpenAnalysis.setStatus(ConfigUtil.INSTANCE.getBoolean(ConfigEnum.AUTO_OPEN_GAME_ANALYSIS));
    }

    private void addListener() {
        //        监听策略开关
        strategySwitch.statusProperty().addListener((observable, oldValue, newValue) -> {
            ConfigUtil.INSTANCE.putBoolean(ConfigEnum.STRATEGY, newValue, true);
        });
        autoOpenAnalysis.statusProperty().addListener((observable, oldValue, newValue) -> {
            ConfigUtil.INSTANCE.putBoolean(ConfigEnum.AUTO_OPEN_GAME_ANALYSIS, newValue, true);
        });
        fileLogLevelComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            ConfigExUtil.INSTANCE.storeFileLogLevel(newValue);
        });
        fileLogLevelComboBox.setCellFactory(new Callback<>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<>() {

                    @Override
                    protected void updateItem(String s, boolean b) {
                        super.updateItem(s, b);
                        if (s == null || b) return;
                        Level level = Level.valueOf(s);
                        if (level == Level.OFF) {
                            setText(level.levelStr);
                        } else if (level == Level.ERROR) {
                            if (isSelected()) {
                                setGraphic(new Label(level.levelStr));
                            } else {
                                setGraphic(new Label(level.levelStr) {{
                                    setStyle("-fx-text-fill: #ff0000;");
                                }});
                            }
                        } else if (level == Level.WARN) {
                            if (isSelected()) {
                                setGraphic(new Label(level.levelStr));
                            } else {
                                setGraphic(new Label(level.levelStr) {{
                                    setStyle("-fx-text-fill: #ff8000;");
                                }});
                            }
                        } else if (level == Level.INFO) {
                            setGraphic(new Label(level.levelStr) {{
                                setStyle("-fx-text-fill: #009e00;");
                            }});
                            if (isSelected()) {
                                setGraphic(new Label(level.levelStr));
                            } else {
                                setGraphic(new Label(level.levelStr) {{
                                    setStyle("-fx-text-fill: #009e00;");
                                }});
                            }
                        } else if (level == Level.DEBUG) {
                            if (isSelected()) {
                                setGraphic(new Label(level.levelStr));
                            } else {
                                setGraphic(new Label(level.levelStr) {{
                                    setStyle("-fx-text-fill: #1982fd;");
                                }});
                            }
                        }
                    }
                };
            }
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initValue();
        addListener();
    }

    @FXML
    @SuppressWarnings("all")
    protected void openLogFile(ActionEvent actionEvent) throws IOException {
        Runtime.getRuntime().exec(String.format("explorer %s", PathDataKt.getLOG_DIR()));
    }

    @FXML
    protected void openMeasureUtil(ActionEvent actionEvent) {
        MeasureApplication.startStage(new Stage());
    }

    @FXML
    protected void openGameDataAnalysis(ActionEvent actionEvent) {
        WindowUtil.INSTANCE.showStage(WindowEnum.GAME_DATA_ANALYSIS, null);
    }
}
