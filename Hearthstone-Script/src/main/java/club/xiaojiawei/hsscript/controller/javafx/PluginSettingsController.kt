package club.xiaojiawei.hsscript.controller.javafx

import club.xiaojiawei.CardAction
import club.xiaojiawei.DeckStrategy
import club.xiaojiawei.bean.PluginWrapper
import club.xiaojiawei.controls.CopyLabel
import club.xiaojiawei.controls.NotificationManager
import club.xiaojiawei.hsscript.component.PluginItem
import club.xiaojiawei.hsscript.status.PluginManager.CARD_ACTION_PLUGINS
import club.xiaojiawei.hsscript.status.PluginManager.DECK_STRATEGY_PLUGINS
import club.xiaojiawei.hsscript.utils.SystemUtil.openURL
import javafx.beans.value.ObservableValue
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.ListView
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import java.net.URL
import java.util.*
import java.util.stream.Stream

/**
 * @author 肖嘉威
 * @date 2023/9/10 15:07
 */
class PluginSettingsController : Initializable {
    @FXML
    protected lateinit var pluginGraphicDescription: Pane

    @FXML
    protected lateinit var pluginDescription: CopyLabel

    @FXML
    protected lateinit var pluginInfo: VBox

    @FXML
    protected lateinit var pluginName: CopyLabel

    @FXML
    protected lateinit var pluginAuthor: CopyLabel

    @FXML
    protected lateinit var pluginId: CopyLabel

    @FXML
    protected lateinit var pluginVersion: CopyLabel

    @FXML
    protected lateinit var rootPane: AnchorPane

    @FXML
    protected lateinit var notificationManager: NotificationManager<Any>

    @FXML
    protected lateinit var pluginListView: ListView<PluginItem>

    override fun initialize(url: URL?, resourceBundle: ResourceBundle?) {
        initValue()
        listen()
    }

    private fun initValue() {
        val pluginItems = pluginListView.items
        Stream.concat(
            DECK_STRATEGY_PLUGINS.values.stream().flatMap { obj: List<PluginWrapper<DeckStrategy>> -> obj.stream() },
            CARD_ACTION_PLUGINS.values.stream().flatMap { obj: List<PluginWrapper<CardAction>> -> obj.stream() }
        ).forEach { plugin: PluginWrapper<out Any> -> pluginItems.add(PluginItem(plugin, notificationManager)) }
    }

    private fun listen() {
        pluginListView.selectionModel.selectedItemProperty()
            .addListener { observable: ObservableValue<out PluginItem>?, oldValue: PluginItem?, newValue: PluginItem? ->
                pluginInfo.isVisible =
                    newValue != null
                if (newValue != null) {
                    pluginName.text = newValue.pluginWrapper.plugin.name()
                    pluginAuthor.text = newValue.pluginWrapper.plugin.author()
                    pluginId.text = newValue.pluginWrapper.plugin.id()
                    pluginVersion.text = newValue.pluginWrapper.plugin.version()
                    pluginDescription.text = newValue.pluginWrapper.plugin.description()
                    val pane = newValue.pluginWrapper.plugin.graphicDescription()
                    pluginGraphicDescription.children.clear()
                    if (pane != null) {
                        pluginGraphicDescription.children.add(pane)
                    }
                }
            }
    }


    fun apply(actionEvent: ActionEvent?) {
    }

    fun save(actionEvent: ActionEvent?) {
    }

    @FXML
    private fun jumpToHome(actionEvent: ActionEvent?) {
        val selectedItem = pluginListView.selectionModel.selectedItem ?: return
        val homeUrl = selectedItem.pluginWrapper.plugin.homeUrl()
        if (homeUrl.isBlank() || !homeUrl.contains("http")) return
        openURL(homeUrl)
    }
}