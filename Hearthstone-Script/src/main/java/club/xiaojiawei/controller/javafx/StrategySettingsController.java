package club.xiaojiawei.controller.javafx;

import club.xiaojiawei.controls.NotificationManager;
import club.xiaojiawei.controls.NumberField;
import club.xiaojiawei.enums.ConfigEnum;
import club.xiaojiawei.enums.WindowEnum;
import club.xiaojiawei.utils.ConfigUtil;
import club.xiaojiawei.utils.WindowUtil;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

/**
 *
 * @author 肖嘉威
 * @date 2023/9/10 15:07
 */
public class StrategySettingsController implements Initializable {

    @FXML
    private AnchorPane rootPane;
    @FXML
    private NumberField actionIntervalField;
    @FXML
    private NotificationManager<Object> notificationManager;
    @FXML
    private NumberField mouseMoveIntervalField;
    @FXML
    private VBox mainVBox;

    private ChangeListener<Scene> sceneListener;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initStructure();
        initValue();
        listen();
    }

    private void initStructure(){
        actionIntervalField.setMinValue("1");
        actionIntervalField.setPromptText("默认：" + ConfigEnum.MOUSE_ACTION_INTERVAL.getDefaultValue());
        mouseMoveIntervalField.setMinValue("1");
        mouseMoveIntervalField.setPromptText("默认：" + ConfigEnum.MOUSE_MOVE_INTERVAL.getDefaultValue());
    }

    private void initValue(){
        actionIntervalField.setText(ConfigUtil.INSTANCE.getString(ConfigEnum.MOUSE_ACTION_INTERVAL));
        mouseMoveIntervalField.setText(ConfigUtil.INSTANCE.getString(ConfigEnum.MOUSE_MOVE_INTERVAL));
    }

    private void listen(){
        sceneListener = (observableValue, scene, t1) -> {
            mainVBox.prefWidthProperty().bind(t1.widthProperty());
            mainVBox.sceneProperty().removeListener(sceneListener);
        };
        mainVBox.sceneProperty().addListener(sceneListener);
    }

    @FXML
    protected void apply(ActionEvent actionEvent) {
        if (saveProperties()){
            notificationManager.showSuccess("应用成功", 2);
        }else {
            notificationManager.showWarn("应用失败", "值不合法",  2);
        }
    }

    @FXML
    protected void save(ActionEvent actionEvent) {
        if (saveProperties()){
            WindowUtil.INSTANCE.hideStage(WindowEnum.SETTINGS);
        }else {
            notificationManager.showWarn("保存失败", "值不合法",  2);
        }
    }

    private boolean saveProperties(){
        String actionInterval = actionIntervalField.getText();
        if (actionInterval == null || actionInterval.isBlank()){
            return false;
        }
        ConfigUtil.INSTANCE.putString(ConfigEnum.MOUSE_ACTION_INTERVAL, actionInterval, true);
        String mouseMoveIntervalFieldText = mouseMoveIntervalField.getText();
        if (mouseMoveIntervalFieldText == null || mouseMoveIntervalFieldText.isBlank()){
            return false;
        }
        ConfigUtil.INSTANCE.putString(ConfigEnum.MOUSE_MOVE_INTERVAL, mouseMoveIntervalFieldText, true);
        return true;
    }
}