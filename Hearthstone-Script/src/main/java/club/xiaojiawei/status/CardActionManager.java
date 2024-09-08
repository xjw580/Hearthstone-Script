package club.xiaojiawei.status;

import club.xiaojiawei.CardAction;

import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author 肖嘉威
 * @date 2024/9/7 16:23
 */
public class CardActionManager {

    public static final Map<String, Supplier<CardAction>> CARD_ACTION_MAP;

    static {
        CARD_ACTION_MAP = load();
        PluginManager.loadCardProperty().addListener((observableValue, aBoolean, t1) -> {
            if (t1) {
                Map<String, Supplier<CardAction>> load = load();
                CARD_ACTION_MAP.clear();
                CARD_ACTION_MAP.putAll(load);
            }
        });
    }

    private static Map<String, Supplier<CardAction>> load(){
        return PluginManager.CARD_ACTION_PLUGINS
                .parallelStream()
                .flatMap(pluginWrapper -> {
                    pluginWrapper.enabledProperty().addListener((observableValue, aBoolean, t1) -> {
                        Map<String, Supplier<CardAction>> load = load();
                        CARD_ACTION_MAP.clear();
                        CARD_ACTION_MAP.putAll(load);
                    });
                    return pluginWrapper.isEnabled()? pluginWrapper.getSpiInstance().stream() : Stream.empty();
                })
                .collect(Collectors.toMap(CardAction::getCardId, cardAction -> cardAction::createNewInstance));
    }
}
