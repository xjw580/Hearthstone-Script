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
                1. 高级设置页增加控制模式
                2. 适配所有皮肤的基础技能
                3. 首页增加清空统计数据按钮
                4. 卡牌插件SDK不兼容更改
                """);
    }

    @FXML
    protected void closeWindow(ActionEvent actionEvent) {
        rootPane.getScene().getWindow().hide();
    }
}