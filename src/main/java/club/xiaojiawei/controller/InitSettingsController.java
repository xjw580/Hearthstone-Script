package club.xiaojiawei.controller;

import club.xiaojiawei.constant.SystemConst;
import club.xiaojiawei.entity.Result;
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
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ResourceBundle;

import static club.xiaojiawei.constant.SystemConst.PROPERTIES;

/**
 * @author 肖嘉威
 * @date 2023/2/11 17:24
 */
public class InitSettingsController implements Initializable {

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
    @FXML
    protected void gameClicked(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("选择炉石传说安装路径");
        File file = directoryChooser.showDialog(new Stage());
        if (file != null){
            game.setText(file.getAbsolutePath());
        }

    }

    @FXML
    protected void platformClicked(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择战网程序");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("程序", "*.exe")
        );
        File chooseFile = fileChooser.showOpenDialog(new Stage());
        if (chooseFile != null){
            platform.setText(chooseFile.getAbsolutePath());
        }
    }

    @FXML
    protected void save(){
        if(!validateGamePath()){
            tip.setFill(Paint.valueOf("#ff3300"));
            tip.setText("炉石传说安装路径不正确,请重新选择");
        }else if (!validatePlatformPath()){
            tip.setFill(Paint.valueOf("#ff3300"));
            tip.setText("战网程序路径不正确,请重新选择");
        }else {
            DashboardController.settingsStage.close();
        }
    }
    @FXML
    protected void apply(){
        if(!validateGamePath()){
            tip.setFill(Paint.valueOf("#ff3300"));
            tip.setText("炉石传说安装路径不正确,请重新选择");
        }else if (!validatePlatformPath()){
            tip.setFill(Paint.valueOf("#ff3300"));
            tip.setText("战网程序路径不正确,请重新选择");
        }else {
            tip.setFill(Paint.valueOf("#00cc00"));
            tip.setText("保存成功,重启生效");
        }
    }

    @SneakyThrows
    public boolean validateGamePath(){
        String path = game.getText();
        if (new File(path).exists()){
            if (!new File(path + "/" + SystemConst.GAME_PROGRAM_NAME).exists()){
                return false;
            }
            PROPERTIES.setProperty("gamepath", path);
            try(FileOutputStream fileOutputStream = new FileOutputStream(SystemConst.getPath() + SystemConst.getName())){
                PROPERTIES.store(fileOutputStream, "zerg");
            }
            return true;
        }
        return false;
    }
    @SneakyThrows
    public boolean validatePlatformPath(){
        String path = platform.getText();
        if (new File(path).exists()){
            PROPERTIES.setProperty("platformpath", path);
            try(FileOutputStream fileOutputStream = new FileOutputStream(SystemConst.getPath() + SystemConst.getName())){
                PROPERTIES.store(fileOutputStream, "zerg");
            }
            return true;
        }
        return false;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AnchorPane.setBottomAnchor(apply, 15.0);
        AnchorPane.setRightAnchor(apply, 15.0);
        AnchorPane.setBottomAnchor(save, 15.0);
        AnchorPane.setRightAnchor(save, 120.0);
        game.setText(PROPERTIES.getProperty("gamepath"));
        platform.setText(PROPERTIES.getProperty("platformpath"));
    }
}
