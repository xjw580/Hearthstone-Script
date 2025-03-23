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
                    🐛 Bug 修复
                    1. 修复某些情况下关闭窗口将无法再次打开窗口的问题
                    2. 修复某些情况下主页的开始暂停按钮显示错误的问题
                    
                    🔧 重构与优化
                    1. 优化DRIVE模式下的程序崩溃问题
                    2. 优化鼠标右键
                    3. 美化软件托盘
                    4. 优化软件启动流程，提高启动页的显示速度
                    5. 优化发现卡牌的处理流程
                    6. 未适配的英雄技能默认行为为鼠标左击
                    7. 优化基础卡牌插件性能
                    """.trimIndent()
    }

    @FXML
    protected fun closeWindow(actionEvent: ActionEvent) {
        rootPane.scene.window.hide()
    }

}