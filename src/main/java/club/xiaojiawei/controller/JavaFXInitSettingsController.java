package club.xiaojiawei.controller;

import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.enums.ConfigurationEnum;
import club.xiaojiawei.enums.StageEnum;
import club.xiaojiawei.utils.FrameUtil;
import club.xiaojiawei.utils.PropertiesUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.net.URL;
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
            FrameUtil.hideStage(StageEnum.SETTINGS);
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
        game.setText(scriptConfiguration.getProperty(ConfigurationEnum.GAME_PATH.getKey()));
        platform.setText(scriptConfiguration.getProperty(ConfigurationEnum.PLATFORM_PATH.getKey()));
    }
}
