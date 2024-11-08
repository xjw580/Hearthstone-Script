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
        versionDescription.setText("""
                1. 适配练习模式（套牌插件中添加RunModeEnum.PRACTICE即可支持）
                2. 优化鼠标移动
                """);
    }

    @FXML
    protected void closeWindow(ActionEvent actionEvent) {
        rootPane.getScene().getWindow().hide();
    }
}