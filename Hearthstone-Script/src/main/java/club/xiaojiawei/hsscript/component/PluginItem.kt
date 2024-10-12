package club.xiaojiawei.hsscript.component

import club.xiaojiawei.DeckPlugin
import club.xiaojiawei.bean.PluginWrapper
import club.xiaojiawei.controls.NotificationManager
import club.xiaojiawei.hsscript.utils.ConfigExUtil
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
        enable.selectedProperty().addListener { _, _, enable ->
            notificationManager?.showSuccess(
                "已${if (enable) "启用" else "禁用"}${name.text}",
                2
            )

            val isDeck = pluginWrapper.plugin is DeckPlugin

            val disableList = if (isDeck) {
                ConfigExUtil.getDeckPluginDisabled()
            } else {
                ConfigExUtil.getCardPluginDisabled()
            }

            if (enable){
                disableList.remove(pluginWrapper.plugin.id())
            }else{
                disableList.add(pluginWrapper.plugin.id())
            }

            if (isDeck) {
                ConfigExUtil.storeDeckPluginDisabled(disableList)
            } else {
                ConfigExUtil.storeCardPluginDisabled(disableList)
            }
        }
    }

}