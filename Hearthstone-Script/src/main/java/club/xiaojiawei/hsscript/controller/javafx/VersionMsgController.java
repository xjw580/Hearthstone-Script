package club.xiaojiawei.hsscript.controller.javafx;

import club.xiaojiawei.hsscript.utils.VersionUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author 肖嘉威
 * @date 2023/10/14 12:43
 */
public class VersionMsgController implements Initializable {

    @FXML
    protected TextArea versionDescription;
    @FXML
    protected AnchorPane rootPane;
    @FXML
    protected Label version;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        version.setText(VersionUtil.INSTANCE.getVERSION());
//        TODO 版本更新时修改！！！
        versionDescription.setText("""
                1. 优化进入游戏主界面时的点击行为
                2. 激进策略支持简单识别伤害法术
                """);
    }

    @FXML
    protected void closeWindow(ActionEvent actionEvent) {
        rootPane.getScene().getWindow().hide();
    }
}