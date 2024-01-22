package club.xiaojiawei.controller;

import club.xiaojiawei.controls.NotificationManager;
import club.xiaojiawei.controls.PasswordTextField;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.enums.ConfigurationEnum;
import club.xiaojiawei.enums.WindowEnum;
import club.xiaojiawei.utils.PropertiesUtil;
import club.xiaojiawei.utils.WindowUtil;
import jakarta.annotation.Resource;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
public class JavaFXInitSettingsController implements Initializable {

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
        scriptConfiguration.setProperty(ConfigurationEnum.PLATFORM_PASSWORD.getKey(), password.getText());
        if(propertiesUtil.storePath(gamePath.getText(), platformPath.getText())){
            ScriptStaticData.setSetPath(true);
            notificationManager.showSuccess("应用成功", 2);
        }else {
            notificationManager.showError("安装路径不正确,请重新选择", 3);
            initValue();
        }
    }

    @FXML
    protected void save(){
        scriptConfiguration.setProperty(ConfigurationEnum.PLATFORM_PASSWORD.getKey(), password.getText());
        if(propertiesUtil.storePath(gamePath.getText(), platformPath.getText())){
            ScriptStaticData.setSetPath(true);
            WindowUtil.hideStage(WindowEnum.SETTINGS);
        }else {
            notificationManager.showError("安装路径不正确,请重新选择", 3);
            initValue();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initValue();
    }
    private void initValue(){
        gamePath.setText(scriptConfiguration.getProperty(ConfigurationEnum.GAME_PATH.getKey()));
        platformPath.setText(scriptConfiguration.getProperty(ConfigurationEnum.PLATFORM_PATH.getKey()));
        password.setText(scriptConfiguration.getProperty(ConfigurationEnum.PLATFORM_PASSWORD.getKey()));
    }

}
