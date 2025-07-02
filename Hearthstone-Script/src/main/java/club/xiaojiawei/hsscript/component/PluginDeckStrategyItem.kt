package club.xiaojiawei.hsscript.component

import club.xiaojiawei.DeckStrategy
import club.xiaojiawei.hsscript.utils.FXUtil
import club.xiaojiawei.hsscript.utils.SystemUtil
import club.xiaojiawei.hsscript.utils.runUI
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.text.Text

/**
 * @author 肖嘉威
 * @date 2025/2/28 12:19
 */
class PluginDeckStrategyItem : HBox() {
    @FXML
    protected lateinit var name: Text

    @FXML
    protected lateinit var copyLabel: StackPane

    @FXML
    protected lateinit var description: Text

    @FXML
    protected lateinit var weightPane: HBox

    var deckStrategy: DeckStrategy? = null
        set(value) {
            field = value
            copyLabel.children.clear()
            weightPane.children.clear()
            value?.let {
                runUI {
                    name.text = it.name()
                    description.text = it.description()
                    if (it.referWeight()) {
                        weightPane.children.add(TagNode("参考权重"))
                    }
                    if (it.referPowerWeight()) {
                        weightPane.children.add(TagNode("参考使用权重"))
                    }
                    if (it.referChangeWeight()) {
                        weightPane.children.add(TagNode("参考换牌权重"))
                    }
                    if (it.referCardInfo()) {
                        weightPane.children.add(TagNode("参考卡牌信息"))
                    }
                    if (it.deckCode().isNotBlank()) {
                        copyLabel.children.add(
                            FXUtil.buildCopyNode({
                                SystemUtil.copyToClipboard(it.deckCode())
                            }, "复制卡组代码", 0.7),
                        )
                    }
                }
            } ?: let {
                name.text = ""
                description.text = ""
            }
        }

    init {
        val fxmlLoader = FXMLLoader(javaClass.getResource("/fxml/component/PluginDeckStrategyItem.fxml"))
        fxmlLoader.setRoot(this)
        fxmlLoader.setController(this)
        fxmlLoader.load<Any>()
    }
}
