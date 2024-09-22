package club.xiaojiawei.status

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.PluginWrapper
import club.xiaojiawei.status.PluginManager.CARD_ACTION_PLUGINS
import club.xiaojiawei.status.PluginManager.loadCardProperty
import javafx.beans.value.ObservableValue
import java.util.function.Supplier
import java.util.stream.Collectors
import java.util.stream.Stream

/**
 * @author 肖嘉威
 * @date 2024/9/7 16:23
 */
object CardActionManager {
    /**
     * key1：pluginId
     * key2：cardId
     */
    val CARD_ACTION_MAP: MutableMap<String, Map<String, Supplier<CardAction>>>

    init {
        CARD_ACTION_MAP = load()
        loadCardProperty().addListener { _: ObservableValue<out Boolean>?, _: Boolean?, t1: Boolean ->
            if (t1) {
                val load = load()
                CARD_ACTION_MAP.clear()
                CARD_ACTION_MAP.putAll(load)
            }
        }
    }

    private fun load(): MutableMap<String, Map<String, Supplier<CardAction>>> {
        return CARD_ACTION_PLUGINS.mapValues { entry ->
            // 将每个 PluginWrapper 的 CardAction 转换为 Map<String, Supplier<CardAction>>
            entry.value
                .flatMap { pluginWrapper ->
                    pluginWrapper.enabledProperty()
                        .addListener { _: ObservableValue<out Boolean?>?, _: Boolean?, _: Boolean? ->
                            val load = load()
                            CARD_ACTION_MAP.clear()
                            CARD_ACTION_MAP.putAll(load)
                        }
                    if (pluginWrapper.isEnabled()) pluginWrapper.spiInstance else emptyList()
                }
                .associate { cardAction ->
                    // 生成的内层 Map 的 key 可以是 CardAction 名称，并创建 Supplier
                    cardAction.getCardId() to Supplier { cardAction.createNewInstance() }
                }
        }.toMutableMap()
//        return CARD_ACTION_PLUGINS.values
//            .parallelStream()
//            .flatMap { list -> list.stream() }
//            .flatMap { pluginWrapper: PluginWrapper<CardAction> ->
//                pluginWrapper.enabledProperty()
//                    .addListener { _: ObservableValue<out Boolean?>?, _: Boolean?, _: Boolean? ->
//                        val load: Map<String, Supplier<CardAction>> =
//                            load()
//                        CARD_ACTION_MAP.clear()
//                        CARD_ACTION_MAP.putAll(load)
//                    }
//                if (pluginWrapper.isEnabled()) pluginWrapper.spiInstance.stream() else Stream.empty()
//            }
//            .collect(
//                Collectors.toMap(
//                    { obj: CardAction -> obj.getCardId() },
//                    { cardAction: CardAction -> Supplier { cardAction.createNewInstance() } })
//            )
    }
}
