package club.xiaojiawei.hsscript.controller.javafx

import club.xiaojiawei.hsscriptbase.util.VersionUtil.VERSION
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
            1. 增加被斩杀投降的功能
            2. 增加对锻造词条的识别
            3. 卡牌信息设置页增加使用行为（为兼容旧配置需要打开软件根目录config/card.info文件将actions替换成playActions）
            
            🐛 Bug修复
            1. 修复注入被中断的问题
            2. 修复无法通过带参数命令启动游戏的问题
            3. 修复抉择位置不对的问题
            
            🔧 重构与优化
            1. 修改换牌权重逻辑
            2. 修改部分设置项的分类，部分设置项需要重新设置
            """.trimIndent()
    }

    @FXML
    protected fun closeWindow(actionEvent: ActionEvent) {
        rootPane.scene.window.hide()
    }
}
