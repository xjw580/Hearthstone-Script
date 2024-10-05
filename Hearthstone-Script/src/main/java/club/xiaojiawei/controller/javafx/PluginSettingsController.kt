package club.xiaojiawei.controller.javafx

import club.xiaojiawei.utils.PropertiesUtil
import club.xiaojiawei.utils.SystemUtil
import jakarta.annotation.Resource
import javafx.beans.value.ChangeListener
import javafx.event.ActionEvent
import javafx.fxml.Initializable
import javafx.scene.control.ListView
import org.springframework.stereotype.Component
import java.net.URL
import java.util.Properties
import java.util.stream.Stream

/**
 * @author 肖嘉威
 * @date 2023/9/10 15:07
 */
@Component
@Slf4j
class PluginSettingsController : Initializable {
    @FXML
    private val pluginDescription: CopyLabel? = null

    @FXML
    private val pluginInfo: VBox? = null

    @FXML
    private val pluginName: CopyLabel? = null

    @FXML
    private val pluginAuthor: CopyLabel? = null

    @FXML
    private val pluginId: CopyLabel? = null

    @FXML
    private val pluginVersion: CopyLabel? = null

    @FXML
    private val rootPane: AnchorPane? = null

    @FXML
    private val notificationManager: NotificationManager<Any?>? = null

    @FXML
    private val pluginListView: ListView<PluginItem?>? = null

    @Resource
    private val scriptConfiguration: Properties? = null

    @Resource
    private val propertiesUtil: PropertiesUtil? = null

    override fun initialize(url: URL?, resourceBundle: ResourceBundle?) {
        initValue()
        listen()
    }

    private fun initValue() {
        val pluginItems: ObservableList<PluginItem?> = pluginListView!!.getItems()
        Stream.concat<PluginWrapper<out Any?>?>(
            PluginManager.DECK_STRATEGY_PLUGINS.values.stream()
                .flatMap<PluginWrapper<DeckStrategy?>> { obj: MutableList<PluginWrapper<DeckStrategy?>?>? -> obj!!.stream() },
            PluginManager.CARD_ACTION_PLUGINS.values.stream()
                .flatMap<PluginWrapper<CardAction?>> { obj: MutableList<PluginWrapper<CardAction?>?>? -> obj!!.stream() }
        ).forEach { plugin: PluginWrapper<out kotlin.Any?>? ->
            pluginItems.add(
                PluginItem(
                    plugin,
                    notificationManager
                )
            )
        }
    }

    private fun listen() {
        pluginListView!!.getSelectionModel().selectedItemProperty()
            .addListener(ChangeListener { observable: ObservableValue<out PluginItem?>?, oldValue: PluginItem?, newValue: PluginItem? ->
                pluginInfo.setVisible(newValue != null)
                if (newValue != null) {
                    pluginName.setText(newValue.pluginWrapper.plugin.name())
                    pluginAuthor.setText(newValue.pluginWrapper.plugin.author())
                    pluginId.setText(newValue.pluginWrapper.plugin.id())
                    pluginVersion.setText(newValue.pluginWrapper.plugin.version())
                    pluginDescription.setText(newValue.pluginWrapper.plugin.description())
                }
            })
    }


    fun apply(actionEvent: ActionEvent?) {
    }

    fun save(actionEvent: ActionEvent?) {
    }

    @FXML
    protected fun jumpToHome(actionEvent: ActionEvent?) {
        val selectedItem: PluginItem? = pluginListView!!.getSelectionModel().getSelectedItem()
        if (selectedItem == null) return
        val homeUrl: String = selectedItem.pluginWrapper.plugin.homeUrl()
        if (homeUrl.isBlank() || !homeUrl.contains("http")) return
        SystemUtil.openUrlByBrowser(homeUrl)
    }
}
