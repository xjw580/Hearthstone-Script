package club.xiaojiawei.hsscript.controller.javafx

import club.xiaojiawei.hsscript.utils.VersionUtil.VERSION
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.layout.AnchorPane
import java.net.URL
import java.util.*

/**
 * @author 肖嘉威
 * @date 2023/10/14 12:43
 */
class VersionMsgController : Initializable {

    @FXML
    protected lateinit var versionDescription: TextArea

    @FXML
    protected lateinit var rootPane: AnchorPane

    @FXML
    protected lateinit var version: Label

    override fun initialize(url: URL?, resourceBundle: ResourceBundle?) {
        version.text = VERSION
        //        TODO 版本更新时修改！！！
        versionDescription.text = """
                    1. 增加mcts策略
                    2. 基础卡牌插件适配沙包战的所有卡牌(AAECAQcC4+YG5eYGDp6fBJ+fBLSfBIagBIigBImgBI7UBJDUBJzUBJ/UBKPUBLT4BbX4Bd3zBgAA)，配合mcts策略使用
                    3. 适配星舰，疲劳伤害，武器的耐久
                    4. 强化游戏关闭
                    5. 高级设置页中增加置顶游戏窗口选项
                    6. 卡牌插件sdk不兼容更改
                    
                    """.trimIndent()
    }

    @FXML
    protected fun closeWindow(actionEvent: ActionEvent) {
        rootPane.scene.window.hide()
    }

}