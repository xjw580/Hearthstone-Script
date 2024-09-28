package club.xiaojiawei.status

import club.xiaojiawei.DeckStrategy
import club.xiaojiawei.bean.PluginWrapper
import club.xiaojiawei.bean.WsResult
import club.xiaojiawei.config.log
import club.xiaojiawei.enums.ConfigEnum
import club.xiaojiawei.enums.WsResultTypeEnum
import club.xiaojiawei.status.PluginManager.DECK_STRATEGY_PLUGINS
import club.xiaojiawei.status.PluginManager.loadDeckProperty
import club.xiaojiawei.utils.PropertiesUtil
import club.xiaojiawei.utils.SystemUtil
import club.xiaojiawei.ws.WebSocketServer
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableSet
import org.apache.logging.log4j.util.Strings
import org.springframework.stereotype.Component
import java.util.stream.Stream

/**
 * @author 肖嘉威
 * @date 2024/9/7 15:17
 */
@Component
class DeckStrategyManager(propertiesUtil: PropertiesUtil?) {

    init {
        Companion.propertiesUtil = propertiesUtil
    }

    companion object {
        var propertiesUtil: PropertiesUtil? = null

        @JvmField
        val CURRENT_DECK_STRATEGY: ObjectProperty<DeckStrategy> = SimpleObjectProperty()

        @JvmField
        val DECK_STRATEGIES: ObservableSet<DeckStrategy> = FXCollections.observableSet()

        init {
            CURRENT_DECK_STRATEGY.addListener { _: ObservableValue<out DeckStrategy>?, _: DeckStrategy?, t1: DeckStrategy? ->
                if (t1 == null) {
                    propertiesUtil?.scriptConfiguration?.setProperty(
                        ConfigEnum.DEFAULT_DECK_STRATEGY.key,
                        ""
                    )
                    propertiesUtil!!.storeScriptProperties()
                    WebSocketServer.sendAllMessage(WsResult.ofNew(WsResultTypeEnum.MODE, ""))
                    WebSocketServer.sendAllMessage(WsResult.ofNew(WsResultTypeEnum.DECK, ""))
                } else if (propertiesUtil?.scriptConfiguration?.getProperty(
                        ConfigEnum.DEFAULT_DECK_STRATEGY.key
                    ) != t1.id()
                ) {
                    propertiesUtil?.scriptConfiguration?.setProperty(
                        ConfigEnum.DEFAULT_DECK_STRATEGY.key,
                        t1.id()
                    )
                    propertiesUtil?.storeScriptProperties()
                    WebSocketServer.sendAllMessage(
                        WsResult.ofNew(
                            WsResultTypeEnum.MODE,
                            t1.runModes
                        )
                    )
                    WebSocketServer.sendAllMessage(WsResult.ofNew(WsResultTypeEnum.DECK, t1.name()))
                    val text = "挂机卡组改为: ${t1.name()}，模式: ${t1.runModes[0].comment}"
                    SystemUtil.notice(text)
                    log.info { text }
                    if (t1.deckCode().isNotBlank() && t1.deckCode().isNotBlank()) {
                        log.info { "$" + t1.deckCode() }
                    }
                }
            }

            load()
            loadDeckProperty().addListener { _: ObservableValue<out Boolean>?, _: Boolean?, t1: Boolean ->
                if (t1) {
                    reload()
                }
            }
        }

        private fun load():List<DeckStrategy>{
            return DECK_STRATEGY_PLUGINS.values.stream()
                .flatMap { list: List<PluginWrapper<DeckStrategy>> -> list.stream() }
                .flatMap { deckPluginWrapper: PluginWrapper<DeckStrategy> ->
                    if (!deckPluginWrapper.isListen){
                        deckPluginWrapper.addEnabledListener{ _: ObservableValue<out Boolean?>?, _: Boolean?, _: Boolean? ->
                            reload()
                        }
                    }
                    if (deckPluginWrapper.isEnabled()) deckPluginWrapper.spiInstance.stream()
                        .filter { deckStrategy: DeckStrategy ->
                            deckStrategy.pluginId = deckPluginWrapper.plugin.id()
                            Strings.isNotBlank(deckStrategy.name()) && Strings.isNotBlank(deckStrategy.id()) && deckStrategy.runModes.isNotEmpty()
                        } else Stream.empty()
                }.toList()
        }

        private fun reload() {
            DECK_STRATEGIES.clear()
            DECK_STRATEGIES.addAll(load())
            log.info { "重新加载套牌库" }
        }
    }
}
