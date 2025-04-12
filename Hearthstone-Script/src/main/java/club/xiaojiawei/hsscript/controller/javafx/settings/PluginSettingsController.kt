package club.xiaojiawei.hsscript.controller.javafx.settings

import club.xiaojiawei.CardAction
import club.xiaojiawei.CardPlugin
import club.xiaojiawei.StrategyPlugin
import club.xiaojiawei.DeckStrategy
import club.xiaojiawei.bean.DBCard
import club.xiaojiawei.bean.PluginWrapper
import club.xiaojiawei.config.EXTRA_THREAD_POOL
import club.xiaojiawei.config.log
import club.xiaojiawei.controls.CopyLabel
import club.xiaojiawei.controls.NotificationManager
import club.xiaojiawei.controls.ProgressModal
import club.xiaojiawei.controls.TableFilterManagerGroup
import club.xiaojiawei.hsscript.component.CardTableView
import club.xiaojiawei.hsscript.component.PluginDeckStrategyItem
import club.xiaojiawei.hsscript.component.PluginItem
import club.xiaojiawei.hsscript.status.PluginManager.CARD_ACTION_PLUGINS
import club.xiaojiawei.hsscript.status.PluginManager.DECK_STRATEGY_PLUGINS
import club.xiaojiawei.hsscript.utils.SystemUtil.openURL
import club.xiaojiawei.hsscript.utils.runUI
import club.xiaojiawei.util.CardDBUtil
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.ListView
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
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
    protected lateinit var pluginTabPane: TabPane

    @FXML
    protected lateinit var deckStrategyListView: ListView<PluginDeckStrategyItem>

    @FXML
    protected lateinit var deckStrategyRootProgressModal: ProgressModal

    @FXML
    protected lateinit var deckTab: Tab

    @FXML
    protected lateinit var cardTab: Tab

    @FXML
    protected lateinit var cardRootProgressModal: ProgressModal

    @FXML
    protected lateinit var rootProgressModal: ProgressModal

    @FXML
    protected lateinit var cardTable: CardTableView

    @FXML
    protected lateinit var cardTableProxy: TableFilterManagerGroup<DBCard, DBCard>

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
    protected lateinit var rootPane: Pane

    @FXML
    protected lateinit var notificationManager: NotificationManager<Any>

    @FXML
    protected lateinit var pluginListView: ListView<PluginItem>

    override fun initialize(
        url: URL?,
        resourceBundle: ResourceBundle?,
    ) {
        initValue()
        listen()
    }

    private fun initValue() {
        val progress = rootProgressModal.show()
        val pluginItems = pluginListView.items
        EXTRA_THREAD_POOL.submit {
            runCatching {
                Stream
                    .concat(
                        DECK_STRATEGY_PLUGINS.values
                            .stream()
                            .flatMap { obj: List<PluginWrapper<DeckStrategy>> -> obj.stream() },
                        CARD_ACTION_PLUGINS.values
                            .stream()
                            .flatMap { obj: List<PluginWrapper<CardAction>> -> obj.stream() },
                    ).map { PluginItem(it, notificationManager) }
                    .toList()
            }.onSuccess { plugins ->
                runUI {
                    pluginItems.addAll(plugins)
                    rootProgressModal.hide(progress)
                }
            }.onFailure {
                runUI {
                    rootProgressModal.hide(progress)
                    notificationManager.showError("加载插件异常", it.message, 5)
                }
            }
        }
    }

    private fun listen() {
        pluginListView.selectionModel
            .selectedItemProperty()
            .addListener { _, _, newPluginItem: PluginItem? ->
                pluginInfo.isVisible = newPluginItem != null
                if (newPluginItem != null) {
                    loadPluginMainMsg(newPluginItem)
                    loadPluginExtraMsg(newPluginItem)
                }
            }
    }

    private fun loadPluginMainMsg(pluginItem: PluginItem) {
        val plugin = pluginItem.pluginWrapper.plugin
        pluginName.text = plugin.name()
        pluginAuthor.text = plugin.author()
        pluginId.text = plugin.id()
        pluginVersion.text = plugin.version()
        pluginDescription.text = plugin.description()
        pluginGraphicDescription.children.clear()
        plugin.graphicDescription()?.let {
            pluginGraphicDescription.children.add(it)
        }
    }

    private fun loadPluginExtraMsg(pluginItem: PluginItem) {
        val spiInstance = pluginItem.pluginWrapper.spiInstance
        if (pluginTabPane.tabs.size > 1) {
            pluginTabPane.tabs.removeLast()
        }
        when (pluginItem.pluginWrapper.plugin) {
            is CardPlugin -> {
                pluginTabPane.tabs.addLast(cardTab)
                val progress = cardRootProgressModal.show()
                EXTRA_THREAD_POOL.submit {
                    runCatching {
                        val dbCards = mutableSetOf<DBCard>()
                        for (cardAction in spiInstance) {
                            cardAction as CardAction
                            val cardIds = cardAction.getCardId()
                            for (cardId in cardIds) {
                                dbCards.addAll(CardDBUtil.queryCardById(cardId, 100, 0, false))
                            }
                        }
                        dbCards
                    }.onSuccess { dbCards ->
                        runUI {
                            cardTableProxy.setAll(dbCards)
                            cardRootProgressModal.hide(progress)
                        }
                    }.onFailure {
                        runUI {
                            cardRootProgressModal.hide(progress)
                            notificationManager.showError(it.message, 5)
                            log.error(it) { }
                        }
                    }
                }
            }

            is StrategyPlugin -> {
                pluginTabPane.tabs.addLast(deckTab)
                val progress = deckStrategyRootProgressModal.show()
                EXTRA_THREAD_POOL.submit {
                    runCatching {
                        val pluginDeckStrategyItems = mutableListOf<PluginDeckStrategyItem>()
                        val deckStrategyList =
                            (spiInstance as List<DeckStrategy>).sortedBy {
                                it.id()
                            }
                        for (deckStrategy in deckStrategyList) {
                            pluginDeckStrategyItems.add(
                                PluginDeckStrategyItem().apply {
                                    this.deckStrategy = deckStrategy
                                },
                            )
                        }
                        pluginDeckStrategyItems
                    }.onSuccess { pluginDeckItems ->
                        runUI {
                            deckStrategyListView.items.setAll(pluginDeckItems)
                            deckStrategyRootProgressModal.hide(progress)
                        }
                    }.onFailure {
                        runUI {
                            deckStrategyRootProgressModal.hide(progress)
                            notificationManager.showError(it.message, 5)
                            log.error(it) { }
                        }
                    }
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
