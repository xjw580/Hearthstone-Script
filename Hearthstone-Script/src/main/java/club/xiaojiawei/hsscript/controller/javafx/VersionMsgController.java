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
                1. 增加mcts策略
                2. 基础卡牌插件适配沙包战的所有卡牌(AAECAQcC4+YG5eYGDp6fBJ+fBLSfBIagBIigBImgBI7UBJDUBJzUBJ/UBKPUBLT4BbX4Bd3zBgAA)，配合mcts策略使用
                3. 适配星舰，疲劳伤害，武器的耐久
                4. 强化游戏关闭
                5. 高级设置页中增加置顶游戏窗口选项
                6. 卡牌插件sdk不兼容更改
                """);
    }

    @FXML
    protected void closeWindow(ActionEvent actionEvent) {
        rootPane.getScene().getWindow().hide();
    }
}