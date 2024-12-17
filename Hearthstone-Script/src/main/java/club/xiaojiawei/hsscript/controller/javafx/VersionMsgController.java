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
                1. 修复敌方变身时无法正确识别的问题
                2. 修复关闭执行策略开关不生效的问题
                3. 策略设置页中添加增加只打人机选项
                """);
    }

    @FXML
    protected void closeWindow(ActionEvent actionEvent) {
        rootPane.getScene().getWindow().hide();
    }
}