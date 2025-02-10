package club.xiaojiawei.hsscript.controller.javafx

import club.xiaojiawei.hsscript.enums.WindowEnum
import club.xiaojiawei.hsscript.utils.WindowUtil.loadRoot
import javafx.beans.value.ObservableValue
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import java.net.URL
import java.util.*

/**
 * @author 肖嘉威
 * @date 2023/10/14 12:43
 */
class SettingsController : Initializable {

    @FXML
    protected lateinit var initTab: Tab

    @FXML
    protected lateinit var advancedTab: Tab

    @FXML
    protected lateinit var pluginTab: Tab

    @FXML
    protected lateinit var strategyTab: Tab

    @FXML
    protected lateinit var weightTab: Tab

    @FXML
    protected lateinit var developerTab: Tab

    @FXML
    protected lateinit var aboutTab: Tab

    @FXML
    protected lateinit var rootPane: TabPane

    override fun initialize(url: URL?, resourceBundle: ResourceBundle?) {
        val selectedItem = rootPane.selectionModel.selectedItem
        if (selectedItem != null) {
            loadTab(selectedItem)
        }
        rootPane.selectionModel.selectedItemProperty()
            .addListener { observable: ObservableValue<out Tab>?, oldValue: Tab?, newValue: Tab ->
                loadTab(newValue)
            }
    }

    private fun loadTab(tab: Tab) {
        if (advancedTab == tab) {
            if (advancedTab.content == null) {
//                Node node = WindowUtil.INSTANCE.loadRoot(WindowEnum.ADVANCED_SETTINGS);
//                ScrollPane scrollPane = new ScrollPane(node);
//                scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                advancedTab.content = loadRoot(WindowEnum.ADVANCED_SETTINGS)
            }
        } else if (initTab == tab) {
            if (initTab.content == null) {
                initTab.content = loadRoot(WindowEnum.INIT_SETTINGS)
            }
        } else if (pluginTab == tab) {
            if (pluginTab.content == null) {
                pluginTab.content = loadRoot(WindowEnum.PLUGIN_SETTINGS)
            }
        } else if (strategyTab == tab) {
            if (strategyTab.content == null) {
                strategyTab.content = loadRoot(WindowEnum.STRATEGY_SETTINGS)
            }
        } else if (weightTab == tab) {
            if (weightTab.content == null) {
                weightTab.content = loadRoot(WindowEnum.WEIGHT_SETTINGS)
            }
        } else if (developerTab == tab) {
            if (developerTab.content == null) {
                developerTab.content = loadRoot(WindowEnum.DEVELOPER_SETTINGS)
            }
        } else if (aboutTab == tab) {
            if (aboutTab.content == null) {
                aboutTab.content = loadRoot(WindowEnum.ABOUT)
            }
        }
    }
}