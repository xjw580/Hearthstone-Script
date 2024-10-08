package club.xiaojiawei.hsscript.component

import club.xiaojiawei.DeckPlugin
import club.xiaojiawei.bean.PluginWrapper
import club.xiaojiawei.config.ConfigurationConfig
import club.xiaojiawei.controls.NotificationManager
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.status.DeckStrategyManager
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.text.Text


/**
 * @author 肖嘉威
 * @date 2024/9/24 13:51
 */
class PluginItem(val pluginWrapper: PluginWrapper<*>, var notificationManager: NotificationManager<*>? = null) :
    HBox() {

    @FXML
    private lateinit var enable: CheckBox

    @FXML
    private lateinit var name: Text

    @FXML
    private lateinit var type: Label

    @FXML
    private lateinit var author: Text

    @FXML
    private lateinit var version: Text

    init {
        val fxmlLoader = FXMLLoader(javaClass.getResource("/fxml/component/PluginItem.fxml"))
        fxmlLoader.setRoot(this)
        fxmlLoader.setController(this)
        fxmlLoader.load<Any>()
        afterLoaded()
    }

    private fun afterLoaded() {
        name.text = pluginWrapper.plugin.name()
        if (pluginWrapper.plugin is DeckPlugin) {
            type.text = "套牌"
            type.styleClass.add("label-ui-success")
        } else {
            type.text = "卡牌"
            type.styleClass.add("label-ui-normal")
        }

        author.text = pluginWrapper.plugin.author()
        version.text = pluginWrapper.plugin.version()

        enable.selectedProperty().bindBidirectional(pluginWrapper.enabledProperty())
        enable.selectedProperty().addListener { _, _, newValue ->
            notificationManager?.showSuccess(
                "已${if (newValue) "启用" else "禁用"}${name.text}",
                2
            )
            val key = if (pluginWrapper.plugin is DeckPlugin) {
                ConfigEnum.DECK_PLUGIN_DISABLED
            } else {
                ConfigEnum.CARD_PLUGIN_DISABLED
            }

            val disableList = ConfigurationConfig.scriptConfiguration.getProperty(
                key.name,
                key.defaultValue
            ).split(",").toMutableList()
            disableList.removeAll { it.trim().isEmpty() }

            if (newValue){
                disableList.remove(pluginWrapper.plugin.id())
            }else{
                disableList.add(pluginWrapper.plugin.id())
            }

            ConfigurationConfig.scriptConfiguration[key.name] = disableList.joinToString(",")
            DeckStrategyManager.propertiesUtil?.storeScriptProperties()
        }
    }

}