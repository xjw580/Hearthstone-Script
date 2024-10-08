package club.xiaojiawei.hsscript.controller.javafx;

import club.xiaojiawei.controls.NotificationManager;
import club.xiaojiawei.controls.Switch;
import club.xiaojiawei.hsscript.enums.ConfigEnum;
import club.xiaojiawei.hsscript.utils.ConfigUtil;
import club.xiaojiawei.hsscript.utils.main.MeasureApplication;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author 肖嘉威
 * @date 2023/9/10 15:07
 */
public class AdvancedSettingsController implements Initializable {

    @FXML
    private VBox mainVBox;
    @FXML
    private NotificationManager<Object> notificationManager;
    @FXML
    private Switch strategySwitch;
    @FXML
    private Switch updateDev;
    @FXML
    private Switch autoUpdate;
    @FXML
    private Switch sendNotice;
    @FXML
    private AnchorPane rootPane;

    private ChangeListener<Scene> sceneListener;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initValue();
        listen();
    }

    private void initValue() {
        strategySwitch.setStatus(ConfigUtil.INSTANCE.getBoolean(ConfigEnum.STRATEGY));
        updateDev.setStatus(ConfigUtil.INSTANCE.getBoolean(ConfigEnum.UPDATE_DEV));
        autoUpdate.setStatus(ConfigUtil.INSTANCE.getBoolean(ConfigEnum.AUTO_UPDATE));
        sendNotice.setStatus(ConfigUtil.INSTANCE.getBoolean(ConfigEnum.SEND_NOTICE));
    }

    private void listen() {
//        监听策略开关
        strategySwitch.statusProperty().addListener((observable, oldValue, newValue) -> {
            ConfigUtil.INSTANCE.putBoolean(ConfigEnum.STRATEGY, newValue, true);
        });
//        监听更新开发版开关
        updateDev.statusProperty().addListener((observable, oldValue, newValue) -> {
            ConfigUtil.INSTANCE.putBoolean(ConfigEnum.UPDATE_DEV, newValue, true);
        });
//        监听自动更新开关
        autoUpdate.statusProperty().addListener((observable, oldValue, newValue) -> {
            ConfigUtil.INSTANCE.putBoolean(ConfigEnum.AUTO_UPDATE, newValue, true);
        });
//        监听发送通知开关
        sendNotice.statusProperty().addListener((observable, oldValue, newValue) -> {
            ConfigUtil.INSTANCE.putBoolean(ConfigEnum.SEND_NOTICE, newValue, true);
        });
        sceneListener = (observableValue, scene, t1) -> {
            mainVBox.prefWidthProperty().bind(t1.widthProperty());
            mainVBox.sceneProperty().removeListener(sceneListener);
        };
        mainVBox.sceneProperty().addListener(sceneListener);
    }


    @FXML
    protected void openMeasureUtil(ActionEvent actionEvent) {
        MeasureApplication.startStage(new Stage());
    }

}