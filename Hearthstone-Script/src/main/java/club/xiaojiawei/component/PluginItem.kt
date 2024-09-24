package club.xiaojiawei.component

import club.xiaojiawei.DeckStrategy
import club.xiaojiawei.bean.PluginWrapper
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.text.Text


/**
 * @author 肖嘉威
 * @date 2024/9/24 13:51
 */
class PluginItem(val deckStrategyPluginWrapper: PluginWrapper<DeckStrategy>): VBox() {

    init {
        children.addAll(
            Text(deckStrategyPluginWrapper.plugin.name()),
            HBox(
                Text(deckStrategyPluginWrapper.plugin.author()),
                Text(deckStrategyPluginWrapper.plugin.version())
            )
        )
        userData = deckStrategyPluginWrapper
    }

}