package club.xiaojiawei.hsscript.component

import club.xiaojiawei.DeckStrategy
import club.xiaojiawei.hsscript.utils.FXUtil
import club.xiaojiawei.hsscript.utils.SystemUtil
import club.xiaojiawei.hsscript.utils.runUI
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.text.Text

/**
 * @author 肖嘉威
 * @date 2025/2/28 12:19
 */
class PluginDeckStrategyItem : HBox() {

    @FXML
    lateinit var name: Text

    @FXML
    lateinit var copyLabel: StackPane

    @FXML
    lateinit var description: Text

    var deckStrategy: DeckStrategy? = null
        set(value) {
            field = value
            value?.let {
                runUI {
                    if (it.deckCode().isNotBlank()) {
                        copyLabel.children.clear()
                        copyLabel.children.add(FXUtil.buildCopyNode({
                            SystemUtil.copyToClipboard(it.deckCode())
                        }, "复制卡组代码", 0.7))
                    }
                }
            }
        }

    init {
        val fxmlLoader = FXMLLoader(javaClass.getResource("/fxml/component/PluginDeckStrategyItem.fxml"))
        fxmlLoader.setRoot(this)
        fxmlLoader.setController(this)
        fxmlLoader.load<Any>()
    }
}