package club.xiaojiawei.controller.javafx;

import club.xiaojiawei.controls.NotificationManager;
import club.xiaojiawei.controls.NumberField;
import club.xiaojiawei.enums.ConfigurationEnum;
import club.xiaojiawei.enums.WindowEnum;
import club.xiaojiawei.utils.PropertiesUtil;
import club.xiaojiawei.utils.WindowUtil;
import jakarta.annotation.Resource;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

import static club.xiaojiawei.enums.ConfigurationEnum.*;

/**
 *
 * @author 肖嘉威
 * @date 2023/9/10 15:07
 */
@Component
@Slf4j
public class StrategySettingsController implements Initializable {

    @Resource private Properties scriptConfiguration;
    @Resource private PropertiesUtil propertiesUtil;

    private ChangeListener<Scene> sceneListener;

    @FXML
    private AnchorPane rootPane;
    @FXML
    private NumberField actionIntervalField;
    @FXML
    private NotificationManager notificationManager;
    @FXML
    private NumberField mouseMoveIntervalField;
    @FXML
    private VBox mainVBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initStructure();
        initValue();
        listen();
    }

    private void initStructure(){
        actionIntervalField.setMinValue("1");
        actionIntervalField.setPromptText("默认：" + MOUSE_ACTION_INTERVAL.getDefaultValue());
        mouseMoveIntervalField.setMinValue("1");
        mouseMoveIntervalField.setPromptText("默认：" + MOUSE_MOVE_INTERVAL.getDefaultValue());
    }

    private void initValue(){
        actionIntervalField.setText(scriptConfiguration.getProperty(MOUSE_ACTION_INTERVAL.getKey(), MOUSE_ACTION_INTERVAL.getDefaultValue()));
        mouseMoveIntervalField.setText(scriptConfiguration.getProperty(MOUSE_MOVE_INTERVAL.getKey(), MOUSE_MOVE_INTERVAL.getDefaultValue()));
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
            WindowUtil.hideStage(WindowEnum.SETTINGS);
        }else {
            notificationManager.showWarn("保存失败", "值不合法",  2);
        }
    }

    private boolean saveProperties(){
        String actionInterval = actionIntervalField.getText();
        if (actionInterval == null || actionInterval.isBlank()){
            return false;
        }
        scriptConfiguration.setProperty(MOUSE_ACTION_INTERVAL.getKey(), actionInterval);
        String mouseMoveIntervalFieldText = mouseMoveIntervalField.getText();
        if (mouseMoveIntervalFieldText == null || mouseMoveIntervalFieldText.isBlank()){
            return false;
        }
        scriptConfiguration.setProperty(MOUSE_MOVE_INTERVAL.getKey(), mouseMoveIntervalFieldText);
        propertiesUtil.storeScriptProperties();
        return true;
    }
}
