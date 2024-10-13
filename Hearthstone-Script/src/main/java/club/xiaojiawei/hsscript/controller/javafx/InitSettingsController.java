package club.xiaojiawei.hsscript.controller.javafx;

import club.xiaojiawei.controls.NotificationManager;
import club.xiaojiawei.controls.PasswordTextField;
import club.xiaojiawei.hsscript.enums.ConfigEnum;
import club.xiaojiawei.hsscript.enums.WindowEnum;
import club.xiaojiawei.hsscript.utils.ConfigExUtil;
import club.xiaojiawei.hsscript.utils.ConfigUtil;
import club.xiaojiawei.hsscript.utils.WindowUtil;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import static club.xiaojiawei.hsscript.consts.ScriptDataKt.*;


/**
 * @author 肖嘉威
 * @date 2023/2/11 17:24
 */
public class InitSettingsController implements Initializable {

    @FXML
    private VBox mainVBox;
    @FXML
    private NotificationManager<Object> notificationManager;
    @FXML
    private Text gamePath;
    @FXML
    private Text platformPath;
    @FXML
    private PasswordTextField password;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private Button apply;
    @FXML
    private Button save;

    private ChangeListener<Scene> sceneListener;

    @FXML
    protected void gameClicked(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("选择" + GAME_CN_NAME + "安装路径");
        File file = directoryChooser.showDialog(new Stage());
        if (file != null){
            gamePath.setText(file.getAbsolutePath());
        }
    }

    @FXML
    protected void platformClicked(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择" + PLATFORM_CN_NAME + "程序");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("程序", "*.exe")
        );
        File chooseFile = fileChooser.showOpenDialog(new Stage());
        if (chooseFile != null){
            platformPath.setText(chooseFile.getAbsolutePath());
        }
    }

    @FXML
    protected void apply(){
        if (checkConfiguration()){
            notificationManager.showSuccess("应用成功", 2);
        }
    }

    @FXML
    protected void save(){
        if (checkConfiguration()){
            WindowUtil.INSTANCE.hideStage(WindowEnum.SETTINGS);
        }
    }

    private boolean checkConfiguration(){
        ConfigUtil.INSTANCE.putString(ConfigEnum.PLATFORM_PASSWORD, password.getText(), true);
        if (!ConfigExUtil.INSTANCE.storePlatformPath(platformPath.getText())){
            notificationManager.showError(PLATFORM_CN_NAME + "安装路径不正确,请重新选择", 3);
            initValue();
            return false;
        }
        if (!ConfigExUtil.INSTANCE.storeGamePath(gamePath.getText())){
            notificationManager.showError(GAME_CN_NAME + "安装路径不正确,请重新选择", 3);
            initValue();
            return false;
        }
        setSetPath(true);
        return true;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initValue();
        sceneListener = (observableValue, scene, t1) -> {
            mainVBox.prefWidthProperty().bind(t1.widthProperty());
            mainVBox.prefWidthProperty().bind(t1.widthProperty());
            mainVBox.sceneProperty().removeListener(sceneListener);
        };
        mainVBox.sceneProperty().addListener(sceneListener);
    }

    private void initValue(){
        gamePath.setText(ConfigUtil.INSTANCE.getString(ConfigEnum.GAME_PATH));
        platformPath.setText(ConfigUtil.INSTANCE.getString(ConfigEnum.PLATFORM_PATH));
        password.setText(ConfigUtil.INSTANCE.getString(ConfigEnum.PLATFORM_PASSWORD));
    }

}