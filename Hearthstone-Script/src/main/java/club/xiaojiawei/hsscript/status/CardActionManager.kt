package club.xiaojiawei.hsscript.status

import club.xiaojiawei.CardAction
import club.xiaojiawei.config.log
import club.xiaojiawei.hsscript.status.PluginManager.CARD_ACTION_PLUGINS
import club.xiaojiawei.hsscript.status.PluginManager.loadCardProperty
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import java.util.function.Supplier

/**
 * @author 肖嘉威
 * @date 2024/9/7 16:23
 */
object CardActionManager {
    /**
     * key1：pluginId
     * key2：cardId
     */
    val CARD_ACTION_MAP: MutableMap<String, Map<String, Supplier<CardAction>>> = FXCollections.observableMap(load())

    init {
        loadCardProperty().addListener { _: ObservableValue<out Boolean>?, _: Boolean?, t1: Boolean ->
            if (t1) {
                reload()
            }
        }
    }

    private fun load(): MutableMap<String, Map<String, Supplier<CardAction>>> {
        return CARD_ACTION_PLUGINS.mapValues { entry ->
            // 将每个 PluginWrapper 的 CardAction 转换为 Map<String, Supplier<CardAction>>
            entry.value
                .flatMap { pluginWrapper ->
                    pluginWrapper.addEnabledListener { _: ObservableValue<out Boolean?>?, _: Boolean?, _: Boolean? ->
                        reload()
                    }
                    if (pluginWrapper.isEnabled()) pluginWrapper.spiInstance else emptyList()
                }
                .associate { cardAction ->
                    // 生成的内层 Map 的 key 可以是 CardAction 名称，并创建 Supplier
                    cardAction.getCardId() to Supplier { cardAction.createNewInstance() }
                }
        }.toMutableMap()
    }

    private fun reload() {
        log.info { "重新加载卡牌库" }
        CARD_ACTION_MAP.clear()
        CARD_ACTION_MAP.putAll(load())
    }
}
