package club.xiaojiawei.controller.javafx;

import club.xiaojiawei.controls.NotificationManager;
import club.xiaojiawei.controls.PasswordTextField;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.enums.ConfigurationEnum;
import club.xiaojiawei.enums.WindowEnum;
import club.xiaojiawei.utils.PropertiesUtil;
import club.xiaojiawei.utils.WindowUtil;
import jakarta.annotation.Resource;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.*;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;


/**
 * @author 肖嘉威
 * @date 2023/2/11 17:24
 */
@Component
public class InitSettingsController implements Initializable {

    @FXML
    private VBox mainVBox;
    @FXML
    private NotificationManager notificationManager;
    @FXML
    private Text gamePath;
    @FXML
    private Text platformPath;
    @FXML
    private PasswordTextField password;
    @Resource
    private Properties scriptConfiguration;
    @Resource
    private PropertiesUtil propertiesUtil;

    private ChangeListener<Scene> sceneListener;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private Button apply;
    @FXML
    private Button save;

    @FXML
    protected void gameClicked(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("选择" + ScriptStaticData.GAME_CN_NAME + "安装路径");
        File file = directoryChooser.showDialog(new Stage());
        if (file != null){
            gamePath.setText(file.getAbsolutePath());
        }
    }

    @FXML
    protected void platformClicked(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择" + ScriptStaticData.PLATFORM_CN_NAME + "程序");
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
            WindowUtil.hideStage(WindowEnum.SETTINGS);
        }
    }

    private boolean checkConfiguration(){
        scriptConfiguration.setProperty(ConfigurationEnum.PLATFORM_PASSWORD.getKey(), password.getText());
        propertiesUtil.storeScriptProperties();
        if (!propertiesUtil.storePlatformPath(platformPath.getText())){
            notificationManager.showError(ScriptStaticData.PLATFORM_CN_NAME + "安装路径不正确,请重新选择", 3);
            initValue();
            return false;
        }
        if (!propertiesUtil.storeGamePath(gamePath.getText())){
            notificationManager.showError(ScriptStaticData.GAME_CN_NAME + "安装路径不正确,请重新选择", 3);
            initValue();
            return false;
        }
        ScriptStaticData.setSetPath(true);
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
        gamePath.setText(scriptConfiguration.getProperty(ConfigurationEnum.GAME_PATH.getKey()));
        platformPath.setText(scriptConfiguration.getProperty(ConfigurationEnum.PLATFORM_PATH.getKey()));
        password.setText(scriptConfiguration.getProperty(ConfigurationEnum.PLATFORM_PASSWORD.getKey()));
    }

}
