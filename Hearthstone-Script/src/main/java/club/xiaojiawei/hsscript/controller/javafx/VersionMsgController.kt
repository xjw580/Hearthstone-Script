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
            1. 增加更新源选项
            2. 增加重复打开软件检测
            3. 增加鼠标控制模式选项，包含驱动级鼠标模拟
            4. 增加统计功能
            5. 增加游戏超时检测服务
            6. 允许设置游戏窗口透明度
            7. 增加限制鼠标范围功能
            8. 增加游戏对局超时检测
            9. 增加自定义时间
            10. 适配交易
            11. 支持关闭未领取的奖励窗口
            12. 增加实时检测游戏窗口功能
            13. 增加打脸策略
            14. 增加随机选择卡组位置
            15. 支持修改游戏和战网窗口大小
            
            🔧 重构与优化
            1. 出牌超时发送的抱歉表情将被随机表情开关控制
            2. 优化鼠标右键点击
            3. 美化软件托盘
            4. 切换模式时，所选策略不会强制清空
            5. 提高启动页的显示速度
            6. 优化发现卡牌的处理流程，更加精准识别发现场景
            7. 未适配的英雄技能默认行为为鼠标左击
            8. 优化基础卡牌插件性能
            9. 提高配置文件的性能
            
            🐛 Bug 修复
            1. 修复检查更新会卡住的问题
            2. 修复下载更新没有进度条的问题
            3. 修复软件窗口最小化时关闭窗口将无法再次打开窗口的问题
            4. 修复会攻击休眠随从的问题
            """.trimIndent()
    }

    @FXML
    protected fun closeWindow(actionEvent: ActionEvent) {
        rootPane.scene.window.hide()
    }
}
