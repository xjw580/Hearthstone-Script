package club.xiaojiawei.hsscript.controller.javafx;

import club.xiaojiawei.controls.NotificationManager;
import club.xiaojiawei.controls.Switch;
import club.xiaojiawei.hsscript.bean.HotKey;
import club.xiaojiawei.hsscript.data.ScriptDataKt;
import club.xiaojiawei.hsscript.dll.SystemDll;
import club.xiaojiawei.hsscript.enums.ConfigEnum;
import club.xiaojiawei.hsscript.listener.GlobalHotkeyListener;
import club.xiaojiawei.hsscript.starter.InjectStarter;
import club.xiaojiawei.hsscript.utils.ConfigExUtil;
import club.xiaojiawei.hsscript.utils.ConfigUtil;
import club.xiaojiawei.hsscript.utils.main.MeasureApplication;
import com.melloware.jintellitype.JIntellitypeConstants;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
    private TextField pauseHotKey;
    @FXML
    private TextField exitHotKey;
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
    private Switch runningMinimize;
    @FXML
    private Switch controlMode;
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
        runningMinimize.setStatus(ConfigUtil.INSTANCE.getBoolean(ConfigEnum.RUNNING_MINIMIZE));
        controlMode.setStatus(ConfigUtil.INSTANCE.getBoolean(ConfigEnum.CONTROL_MODE));
        sendNotice.setStatus(ConfigUtil.INSTANCE.getBoolean(ConfigEnum.SEND_NOTICE));

        HotKey pauseKey = ConfigExUtil.INSTANCE.getPauseHotKey();
        if (pauseKey != null) {
            pauseHotKey.setText(pauseKey.toString());
        }
        HotKey exitKey = ConfigExUtil.INSTANCE.getExitHotKey();
        if (exitKey != null) {
            exitHotKey.setText(exitKey.toString());
        }
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
//        监听运行最小化开关
        runningMinimize.statusProperty().addListener((observable, oldValue, newValue) -> {
            ConfigUtil.INSTANCE.putBoolean(ConfigEnum.RUNNING_MINIMIZE, newValue, true);
        });
//        监听控制开关
        controlMode.statusProperty().addListener((observable, oldValue, newValue) -> {
            ConfigUtil.INSTANCE.putBoolean(ConfigEnum.CONTROL_MODE, newValue, true);
            if (newValue) {
                SystemDll.INSTANCE.uninstallDll(ScriptDataKt.getGAME_HWND());
            } else {
                InjectStarter injectStarter = new InjectStarter();
                injectStarter.start();
                injectStarter.release();
            }
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

        pauseHotKey.setOnKeyPressed(event -> {
            HotKey hotKey = plusModifier(event);
            if (hotKey != null) {
                if (hotKey.getKeyCode() == 0) {
                    pauseHotKey.setText("");
                    ConfigExUtil.INSTANCE.storePauseHotKey(hotKey);
                    GlobalHotkeyListener.INSTANCE.reload();
                    notificationManager.showSuccess("开始/暂停热键热键已删除", 2);
                } else {
                    pauseHotKey.setText(hotKey.toString());
                    ConfigExUtil.INSTANCE.storePauseHotKey(hotKey);
                    GlobalHotkeyListener.INSTANCE.reload();
                    notificationManager.showSuccess("开始/暂停热键已修改", 2);
                }
            }
        });
        pauseHotKey.setOnKeyReleased(this::reduceModifier);
        pauseHotKey.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                modifier = 0;
            }
        });

        exitHotKey.setOnKeyPressed(event -> {
            HotKey hotKey = plusModifier(event);
            if (hotKey != null) {
                if (hotKey.getKeyCode() == 0) {
                    exitHotKey.setText("");
                    ConfigExUtil.INSTANCE.storeExitHotKey(hotKey);
                    GlobalHotkeyListener.INSTANCE.reload();
                    notificationManager.showSuccess("退出热键已删除", 2);
                } else {
                    exitHotKey.setText(hotKey.toString());
                    ConfigExUtil.INSTANCE.storeExitHotKey(hotKey);
                    GlobalHotkeyListener.INSTANCE.reload();
                    notificationManager.showSuccess("退出热键已修改", 2);
                }
            }
        });
        exitHotKey.setOnKeyReleased(this::reduceModifier);
        exitHotKey.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                modifier = 0;
            }
        });

    }

    private HotKey plusModifier(KeyEvent event) {
        if (event.getCode() == KeyCode.ALT) {
            modifier += JIntellitypeConstants.MOD_ALT;
        } else if (event.getCode() == KeyCode.CONTROL) {
            modifier += JIntellitypeConstants.MOD_CONTROL;
        } else if (event.getCode() == KeyCode.SHIFT) {
            modifier += JIntellitypeConstants.MOD_SHIFT;
        } else if (event.getCode() == KeyCode.WINDOWS) {
            modifier += JIntellitypeConstants.MOD_WIN;
        } else if (event.getCode() == KeyCode.BACK_SPACE) {
            return new HotKey();
        } else {
            int code = event.getCode().getCode();
            if (code >= 65 && code <= 90) {
                return new HotKey(modifier, code);
            }
        }
        return null;
    }

    private void reduceModifier(KeyEvent event) {
        if (event.getCode() == KeyCode.ALT) {
            modifier -= JIntellitypeConstants.MOD_ALT;
        } else if (event.getCode() == KeyCode.CONTROL) {
            modifier -= JIntellitypeConstants.MOD_CONTROL;
        } else if (event.getCode() == KeyCode.SHIFT) {
            modifier -= JIntellitypeConstants.MOD_SHIFT;
        } else if (event.getCode() == KeyCode.WINDOWS) {
            modifier -= JIntellitypeConstants.MOD_WIN;
        }
    }

    private int modifier;


    @FXML
    protected void openMeasureUtil(ActionEvent actionEvent) {
        MeasureApplication.startStage(new Stage());
    }

}