package club.xiaojiawei.controller;

import club.xiaojiawei.custom.LogRunnable;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.enums.ConfigurationKeyEnum;
import club.xiaojiawei.utils.PropertiesUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * @author 肖嘉威
 * @date 2023/2/11 17:24
 */
@Component
public class JavaFXInitSettingsController implements Initializable {

    @FXML
    private Button apply;
    @FXML
    private Button save;
    @FXML
    private Label game;
    @FXML
    private Label platform;
    @FXML
    private Text tip;
    @Resource
    private ApplicationContext context;
    @Resource
    private Properties scriptConfiguration;
    @Resource
    private PropertiesUtil propertiesUtil;
    @Resource
    private ScheduledThreadPoolExecutor extraThreadPool;
    @FXML
    protected void gameClicked(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("选择" + ScriptStaticData.GAME_CN_NAME + "安装路径");
        File file = directoryChooser.showDialog(new Stage());
        if (file != null){
            game.setText(file.getAbsolutePath());
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
            platform.setText(chooseFile.getAbsolutePath());
        }
    }

    @FXML
    protected void apply(){
        if(propertiesUtil.storePath(game.getText(), platform.getText())){
            ScriptStaticData.setSetPath(true);
            ((Stage)(apply.getScene().getWindow())).close();
        }else {
            tip.setFill(Paint.valueOf("#ff3300"));
            tip.setText(ScriptStaticData.GAME_CN_NAME + "安装路径不正确,请重新选择");
        }
    }
    @FXML
    protected void save(){
        if(propertiesUtil.storePath(game.getText(), platform.getText())){
            ScriptStaticData.setSetPath(true);
            tip.setFill(Paint.valueOf("#00cc00"));
            tip.setText("保存成功😊");
            extraThreadPool.schedule(() -> tip.setText(""), 3, TimeUnit.SECONDS);
        }else {
            tip.setFill(Paint.valueOf("#ff3300"));
            tip.setText(ScriptStaticData.GAME_CN_NAME + "安装路径不正确,请重新选择😩");
            extraThreadPool.schedule(() -> tip.setText(""), 3, TimeUnit.SECONDS);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AnchorPane.setBottomAnchor(apply, 15.0);
        AnchorPane.setRightAnchor(apply, 15.0);
        AnchorPane.setBottomAnchor(save, 15.0);
        AnchorPane.setRightAnchor(save, 120.0);
        game.setText(scriptConfiguration.getProperty(ConfigurationKeyEnum.GAME_PATH_KEY.getKey()));
        platform.setText(scriptConfiguration.getProperty(ConfigurationKeyEnum.PLATFORM_PATH_KEY.getKey()));
    }

    public void showStage(){
        Stage stage = new Stage();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(ScriptStaticData.MAIN_PATH + "settings.fxml"));
            fxmlLoader.setControllerFactory(context::getBean);
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            stage.setScene(scene);
            scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage.setTitle("设置");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource(ScriptStaticData.SCRIPT_ICON_PATH)).toExternalForm()));
        stage.show();
    }
}
