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
                1. 修复突袭随从首次登场可能打脸的问题
                2. 增加权重设置页
                3. 提高设置页的打开速度
                4. 基础策略、激进策略接入权重设置
                5. 高级设置页增加控制模式
                6. 适配所有皮肤的基础技能
                7. 首页增加清空统计数据按钮
                8. 适配练习模式（套牌插件中添加RunModeEnum.PRACTICE即可支持）
                9. 优化鼠标移动
                10. 优化开始匹配前的点击流程
                """);
    }

    @FXML
    protected void closeWindow(ActionEvent actionEvent) {
        rootPane.getScene().getWindow().hide();
    }
}