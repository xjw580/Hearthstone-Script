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

    override fun initialize(
        url: URL?,
        resourceBundle: ResourceBundle?,
    ) {
        version.text = VERSION
        //        TODO 版本更新时修改！！！
        versionDescription.text =
            """
            🚀 新功能
            1. 增加卡牌信息设置页，通过设置卡牌信息可以让软件知道如何使用卡牌
            
            🐛 Bug 修复
            1. 修复定时工作的一些逻辑问题
            2. 修复激进策略下剧毒随从选择的攻击目标错乱的问题
            
            🔧 重构与优化
            1. 更新卡牌数据库
            2. 优化mcts策略
            """.trimIndent()
    }

    @FXML
    protected fun closeWindow(actionEvent: ActionEvent) {
        rootPane.scene.window.hide()
    }
}
