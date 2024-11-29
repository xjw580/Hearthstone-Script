package club.xiaojiawei.hsscript.controller.javafx;

import club.xiaojiawei.controls.NotificationManager;
import club.xiaojiawei.controls.NumberField;
import club.xiaojiawei.controls.Switch;
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
import javafx.scene.layout.StackPane;
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
    private StackPane rootPane;
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
    @FXML
    private Switch randomEventSwitch;
    @FXML
    private Switch randomEmotionSwitch;
    @FXML
    private NumberField autoSurrenderField;

    private ChangeListener<Scene> sceneListener;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initActionInterval();
        initMoveSpeed();
        initMatchMaximumTime();
        initIdleMaximumTime();
        initLogLimit();
        initRandomEvent();
        initRandomEmotion();
        initAutoSurrender();

        listen();
    }

    private void initActionInterval() {
        ConfigEnum key = ConfigEnum.MOUSE_ACTION_INTERVAL;
        actionIntervalField.setMinValue("1");
        actionIntervalField.setPromptText("默认：" + key.getDefaultValue());
        actionIntervalField.setTooltip(new Tooltip("默认：" + key.getDefaultValue()));
        actionIntervalField.setText(ConfigUtil.INSTANCE.getString(key));
    }

    private void initMoveSpeed() {
        ConfigEnum key = ConfigEnum.PAUSE_STEP;
        moveSpeedField.setMinValue("1");
        moveSpeedField.setPromptText("默认：" + key.getDefaultValue());
        moveSpeedField.setTooltip(new Tooltip("默认：" + key.getDefaultValue()));
        moveSpeedField.setText(ConfigUtil.INSTANCE.getString(key));
    }

    private void initMatchMaximumTime() {
        ConfigEnum key = ConfigEnum.MATCH_MAXIMUM_TIME;
        matchMaximumTimeField.setMinValue("1");
        matchMaximumTimeField.setPromptText("默认：" + key.getDefaultValue());
        matchMaximumTimeField.setTooltip(new Tooltip("默认：" + key.getDefaultValue()));
        matchMaximumTimeField.setText(ConfigUtil.INSTANCE.getString(key));
    }

    private void initIdleMaximumTime() {
        ConfigEnum key = ConfigEnum.IDLE_MAXIMUM_TIME;
        idleMaximumTimeField.setMinValue("1");
        idleMaximumTimeField.setPromptText("默认：" + key.getDefaultValue());
        idleMaximumTimeField.setTooltip(new Tooltip("默认：" + key.getDefaultValue()));
        idleMaximumTimeField.setText(ConfigUtil.INSTANCE.getString(key));
    }

    private void initLogLimit() {
        ConfigEnum key = ConfigEnum.GAME_LOG_LIMIT;
        logLimitField.setMinValue("1");
        logLimitField.setMaxValue("102400");
        logLimitField.setPromptText("默认：" + key.getDefaultValue());
        logLimitField.setTooltip(new Tooltip("默认：" + key.getDefaultValue()));
        logLimitField.setText(ConfigUtil.INSTANCE.getString(key));
    }

    private void initRandomEvent() {
        ConfigEnum key = ConfigEnum.RANDOM_EVENT;
        randomEventSwitch.setStatus(ConfigUtil.INSTANCE.getBoolean(key));
        randomEventSwitch.statusProperty().addListener((observable, oldValue, newValue) -> {
            ConfigUtil.INSTANCE.putBoolean(key, newValue, true);
            notificationManager.showSuccess("修改成功", 1);
        });
    }

    private void initRandomEmotion() {
        ConfigEnum key = ConfigEnum.RANDOM_EMOTION;
        randomEmotionSwitch.setStatus(ConfigUtil.INSTANCE.getBoolean(key));
        randomEmotionSwitch.statusProperty().addListener((observable, oldValue, newValue) -> {
            ConfigUtil.INSTANCE.putBoolean(key, newValue, true);
            notificationManager.showSuccess("修改成功", 1);
        });
    }

    private void initAutoSurrender() {
        ConfigEnum key = ConfigEnum.AUTO_SURRENDER;
        autoSurrenderField.setMinValue("-1");
        String defaultText = "默认：" + key.getDefaultValue();
        autoSurrenderField.setPromptText(defaultText);
        autoSurrenderField.setTooltip(new Tooltip(defaultText));
        autoSurrenderField.setText(ConfigUtil.INSTANCE.getString(key));
    }


    private void listen() {
        sceneListener = (observableValue, scene, t1) -> {
            mainVBox.prefWidthProperty().bind(t1.widthProperty());
            mainVBox.sceneProperty().removeListener(sceneListener);
        };
        mainVBox.sceneProperty().addListener(sceneListener);
    }

    @FXML
    protected void apply(ActionEvent actionEvent) {
        if (saveProperties()) {
            notificationManager.showSuccess("应用成功，即刻生效", 2);
        }
    }

    @FXML
    protected void save(ActionEvent actionEvent) {
        if (saveProperties()) {
            WindowUtil.INSTANCE.hideStage(WindowEnum.SETTINGS);
        }
    }

    private boolean saveProperties() {
        String actionInterval = actionIntervalField.getText();
        if (actionInterval == null || actionInterval.isBlank()) {
            actionInterval = ConfigEnum.MOUSE_ACTION_INTERVAL.getDefaultValue();
            actionIntervalField.setText(actionInterval);
            return false;
        }
        ConfigUtil.INSTANCE.putString(ConfigEnum.MOUSE_ACTION_INTERVAL, actionInterval, false);

        String moveSpeed = moveSpeedField.getText();
        if (moveSpeed == null || moveSpeed.isBlank()) {
            moveSpeed = ConfigEnum.PAUSE_STEP.getDefaultValue();
            moveSpeedField.setText(moveSpeed);
        }
        ConfigUtil.INSTANCE.putString(ConfigEnum.PAUSE_STEP, moveSpeed, false);

        String matchMaximumTime = matchMaximumTimeField.getText();
        if (matchMaximumTime == null || matchMaximumTime.isBlank()) {
            matchMaximumTime = ConfigEnum.MATCH_MAXIMUM_TIME.getDefaultValue();
            matchMaximumTimeField.setText(matchMaximumTime);
        }
        ConfigUtil.INSTANCE.putString(ConfigEnum.MATCH_MAXIMUM_TIME, matchMaximumTime, false);

        String idleMaximumTime = idleMaximumTimeField.getText();
        if (idleMaximumTime == null || idleMaximumTime.isBlank()) {
            idleMaximumTime = ConfigEnum.IDLE_MAXIMUM_TIME.getDefaultValue();
            logLimitField.setText(idleMaximumTime);
        }
        ConfigUtil.INSTANCE.putString(ConfigEnum.IDLE_MAXIMUM_TIME, idleMaximumTime, false);

        String logLimit = logLimitField.getText();
        if (logLimit == null || logLimit.isBlank()) {
            logLimit = ConfigEnum.GAME_LOG_LIMIT.getDefaultValue();
            logLimitField.setText(logLimit);
        }
        ConfigUtil.INSTANCE.putString(ConfigEnum.GAME_LOG_LIMIT, logLimit, false);

        String autoSurrender = autoSurrenderField.getText();
        if (autoSurrender == null || autoSurrender.isBlank() || autoSurrender.equals("-")) {
            autoSurrender = ConfigEnum.AUTO_SURRENDER.getDefaultValue();
            autoSurrenderField.setText(autoSurrender);
        }
        ConfigUtil.INSTANCE.putString(ConfigEnum.AUTO_SURRENDER, autoSurrender, false);

        ConfigUtil.INSTANCE.store();
        CommonCardAction.Companion.reload();
        ScriptDataKt.reload();
        MouseUtil.INSTANCE.reload();
        return true;
    }
}