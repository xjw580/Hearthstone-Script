package club.xiaojiawei.hsscript.controller.javafx;

import club.xiaojiawei.controls.NotificationManager;
import club.xiaojiawei.controls.NumberField;
import club.xiaojiawei.hsscript.bean.CommonCardAction;
import club.xiaojiawei.hsscript.data.ScriptDataKt;
import club.xiaojiawei.hsscript.enums.ConfigEnum;
import club.xiaojiawei.hsscript.enums.WindowEnum;
import club.xiaojiawei.hsscript.utils.ConfigUtil;
import club.xiaojiawei.hsscript.utils.MouseUtil;
import club.xiaojiawei.hsscript.utils.WindowUtil;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Tooltip;
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
    private NumberField moveSpeedField;
    @FXML
    private NumberField matchMaximumTimeField;
    @FXML
    private NumberField idleMaximumTimeField;
    @FXML
    private NumberField logLimitField;
    @FXML
    private NotificationManager<Object> notificationManager;
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
        actionIntervalField.setTooltip(new Tooltip("默认：" + ConfigEnum.MOUSE_ACTION_INTERVAL.getDefaultValue()));

        moveSpeedField.setMinValue("1");
        moveSpeedField.setPromptText("默认：" + ConfigEnum.PAUSE_STEP.getDefaultValue());
        moveSpeedField.setTooltip(new Tooltip("默认：" + ConfigEnum.PAUSE_STEP.getDefaultValue()));

        matchMaximumTimeField.setMinValue("1");
        matchMaximumTimeField.setPromptText("默认：" + ConfigEnum.MATCH_MAXIMUM_TIME.getDefaultValue());
        matchMaximumTimeField.setTooltip(new Tooltip("默认：" + ConfigEnum.MATCH_MAXIMUM_TIME.getDefaultValue()));

        idleMaximumTimeField.setMinValue("1");
        idleMaximumTimeField.setPromptText("默认：" + ConfigEnum.IDLE_MAXIMUM_TIME.getDefaultValue());
        idleMaximumTimeField.setTooltip(new Tooltip("默认：" + ConfigEnum.IDLE_MAXIMUM_TIME.getDefaultValue()));

        logLimitField.setMinValue("1");
        logLimitField.setMaxValue("102400");
        logLimitField.setPromptText("默认：" + ConfigEnum.GAME_LOG_LIMIT.getDefaultValue());
        logLimitField.setTooltip(new Tooltip("默认：" + ConfigEnum.GAME_LOG_LIMIT.getDefaultValue()));
    }

    private void initValue(){
        actionIntervalField.setText(ConfigUtil.INSTANCE.getString(ConfigEnum.MOUSE_ACTION_INTERVAL));
        moveSpeedField.setText(ConfigUtil.INSTANCE.getString(ConfigEnum.PAUSE_STEP));
        matchMaximumTimeField.setText(ConfigUtil.INSTANCE.getString(ConfigEnum.MATCH_MAXIMUM_TIME));
        idleMaximumTimeField.setText(ConfigUtil.INSTANCE.getString(ConfigEnum.IDLE_MAXIMUM_TIME));
        logLimitField.setText(ConfigUtil.INSTANCE.getString(ConfigEnum.GAME_LOG_LIMIT));
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
            notificationManager.showSuccess("应用成功，即刻生效", 2);
        }
    }

    @FXML
    protected void save(ActionEvent actionEvent) {
        if (saveProperties()){
            WindowUtil.INSTANCE.hideStage(WindowEnum.SETTINGS);
        }
    }

    private boolean saveProperties(){
        String actionInterval = actionIntervalField.getText();
        if (actionInterval == null || actionInterval.isBlank()){
            actionIntervalField.setText(ConfigUtil.INSTANCE.getString(ConfigEnum.MOUSE_ACTION_INTERVAL));
            notificationManager.showWarn("操作间隔不允许为空", 2);
            return false;
        }
        String moveSpeed = moveSpeedField.getText();
        if (moveSpeed == null || moveSpeed.isBlank()){
            moveSpeedField.setText(ConfigUtil.INSTANCE.getString(ConfigEnum.PAUSE_STEP));
            notificationManager.showWarn("鼠标整体移动速度不允许为空", 2);
            return false;
        }
        String matchMaximumTime = matchMaximumTimeField.getText();
        if (matchMaximumTime == null || matchMaximumTime.isBlank()){
            matchMaximumTimeField.setText(ConfigUtil.INSTANCE.getString(ConfigEnum.MATCH_MAXIMUM_TIME));
            notificationManager.showWarn("单次匹配最长时间不允许为空", 2);
            return false;
        }
        String idleMaximumTime = idleMaximumTimeField.getText();
        if (idleMaximumTime == null || idleMaximumTime.isBlank()){
            idleMaximumTimeField.setText(ConfigUtil.INSTANCE.getString(ConfigEnum.IDLE_MAXIMUM_TIME));
            notificationManager.showWarn("最长空闲时间不允许为空", 2);
            return false;
        }
        String logLimit = logLimitField.getText();
        if (logLimit == null || logLimit.isBlank()){
            logLimitField.setText(ConfigUtil.INSTANCE.getString(ConfigEnum.GAME_LOG_LIMIT));
            notificationManager.showWarn("游戏日志大小限制不允许为空", 2);
            return false;
        }

        ConfigUtil.INSTANCE.putString(ConfigEnum.MOUSE_ACTION_INTERVAL, actionInterval, false);
        ConfigUtil.INSTANCE.putString(ConfigEnum.PAUSE_STEP, moveSpeed, false);
        ConfigUtil.INSTANCE.putString(ConfigEnum.MATCH_MAXIMUM_TIME, matchMaximumTime, false);
        ConfigUtil.INSTANCE.putString(ConfigEnum.IDLE_MAXIMUM_TIME, idleMaximumTime, false);
        ConfigUtil.INSTANCE.putString(ConfigEnum.GAME_LOG_LIMIT, logLimit, false);
        ConfigUtil.INSTANCE.store();
        CommonCardAction.Companion.reload();
        ScriptDataKt.reload();
        MouseUtil.INSTANCE.reload();
        return true;
    }
}