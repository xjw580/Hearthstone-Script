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
                    1. 增加驱动级鼠标模拟
                    2. 🤖注意！当前版本仅用于测试驱动级鼠标模拟的封禁概率！🤖
                    2. 出牌超时发送的抱歉表情将被随机表情开关控制
                    3. 增加重复打开软件检测
                    """.trimIndent()
    }

    @FXML
    protected fun closeWindow(actionEvent: ActionEvent) {
        rootPane.scene.window.hide()
    }

}