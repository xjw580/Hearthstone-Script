package club.xiaojiawei.hsscript.component

import club.xiaojiawei.CardPlugin
import club.xiaojiawei.StrategyPlugin
import club.xiaojiawei.bean.PluginWrapper
import club.xiaojiawei.controls.NotificationManager
import club.xiaojiawei.controls.ico.FailIco
import club.xiaojiawei.controls.ico.OKIco
import club.xiaojiawei.hsscript.bean.Release
import club.xiaojiawei.hsscript.utils.ConfigExUtil
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.CheckBox
import javafx.scene.control.ContentDisplay
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.scene.layout.HBox
import javafx.scene.text.Text

/**
 * @author 肖嘉威
 * @date 2024/9/24 13:51
 */
class PluginItem(
    val pluginWrapper: PluginWrapper<*>,
    var notificationManager: NotificationManager<*>? = null,
) : HBox() {
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

    @FXML
    private lateinit var sdkVersion: Label

    init {
        val fxmlLoader = FXMLLoader(javaClass.getResource("/fxml/component/PluginItem.fxml"))
        fxmlLoader.setRoot(this)
        fxmlLoader.setController(this)
        fxmlLoader.load<Any>()
        afterLoaded()
    }

    private fun afterLoaded() {
        name.text = pluginWrapper.plugin.name()
        val minimumCompatibleVersion: String
        if (pluginWrapper.plugin is StrategyPlugin) {
            type.text = "策略"
            type.styleClass.add("label-ui-success")
            minimumCompatibleVersion = StrategyPlugin.MINIMUM_COMPATIBLE_VERSION.removeSuffix("v")
        } else {
            type.text = "卡牌"
            type.styleClass.add("label-ui-normal")
            minimumCompatibleVersion = CardPlugin.MINIMUM_COMPATIBLE_VERSION.removeSuffix("v")
        }

        author.text = pluginWrapper.plugin.author()
        version.text = pluginWrapper.plugin.version()

        val sdkVersionStr = pluginWrapper.plugin.sdkVersion()
        sdkVersion.contentDisplay = ContentDisplay.RIGHT
        if (sdkVersionStr.isBlank() ||
            Release.compareVersion(
                minimumCompatibleVersion,
                sdkVersionStr.removeSuffix("v"),
            ) > 0
        ) {
            sdkVersion.styleClass.add("label-ui-error")
            sdkVersion.graphic =
                FailIco().apply {
                    scaleX = 0.8
                    scaleY = 0.8
                }
            sdkVersion.tooltip = Tooltip("版本不兼容，最低为$minimumCompatibleVersion，可能无法正常使用")
        } else {
            sdkVersion.styleClass.add("label-ui-success")
            sdkVersion.graphic =
                OKIco().apply {
                    scaleX = 0.8
                    scaleY = 0.8
                }
            sdkVersion.tooltip = Tooltip("版本兼容")
        }
        sdkVersion.text = if (sdkVersionStr.isBlank()) "版本号错误" else sdkVersionStr

        enable.selectedProperty().bindBidirectional(pluginWrapper.enabledProperty())
        enable.selectedProperty().addListener { _, _, enable ->
            notificationManager?.showSuccess(
                "已${if (enable) "启用" else "禁用"}${name.text}",
                2,
            )

            val isDeck = pluginWrapper.plugin is StrategyPlugin

            val disableList =
                if (isDeck) {
                    ConfigExUtil.getDeckPluginDisabled()
                } else {
                    ConfigExUtil.getCardPluginDisabled()
                }

            if (enable) {
                disableList.remove(pluginWrapper.plugin.id())
            } else {
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
