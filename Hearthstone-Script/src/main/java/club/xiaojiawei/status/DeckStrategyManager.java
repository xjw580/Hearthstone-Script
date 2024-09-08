package club.xiaojiawei.status;

import club.xiaojiawei.DeckStrategy;
import club.xiaojiawei.bean.WsResult;
import club.xiaojiawei.enums.WsResultTypeEnum;
import club.xiaojiawei.utils.PropertiesUtil;
import club.xiaojiawei.utils.SystemUtil;
import club.xiaojiawei.ws.WebSocketServer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Stream;

import static club.xiaojiawei.enums.ConfigurationEnum.DEFAULT_DECK_STRATEGY;

/**
 * @author 肖嘉威
 * @date 2024/9/7 15:17
 */
@Slf4j
@Component
public class DeckStrategyManager {

    private static PropertiesUtil propertiesUtil;

    public DeckStrategyManager(PropertiesUtil propertiesUtil) {
        DeckStrategyManager.propertiesUtil = propertiesUtil;
    }

    public static final ObjectProperty<DeckStrategy> CURRENT_DECK_STRATEGY = new SimpleObjectProperty<>();

    public static final ObservableSet<DeckStrategy> DECK_STRATEGIES = FXCollections.observableSet();

    private static Runnable runnable;

    static {
        CURRENT_DECK_STRATEGY.addListener((observableValue, deckStrategy, t1) -> {
            if (t1 == null) {
                propertiesUtil.getScriptConfiguration().setProperty(DEFAULT_DECK_STRATEGY.getKey(), "");
                propertiesUtil.storeScriptProperties();
                WebSocketServer.sendAllMessage(WsResult.ofNew(WsResultTypeEnum.MODE, ""));
                WebSocketServer.sendAllMessage(WsResult.ofNew(WsResultTypeEnum.DECK, ""));
            } else if (!Objects.equals(propertiesUtil.getScriptConfiguration().getProperty(DEFAULT_DECK_STRATEGY.getKey()), t1.id())){
                propertiesUtil.getScriptConfiguration().setProperty(DEFAULT_DECK_STRATEGY.getKey(), t1.id());
                propertiesUtil.storeScriptProperties();
                WebSocketServer.sendAllMessage(WsResult.ofNew(WsResultTypeEnum.MODE, t1.runMode));
                WebSocketServer.sendAllMessage(WsResult.ofNew(WsResultTypeEnum.DECK, t1.name()));
                SystemUtil.notice("挂机卡组改为：" + t1.name());
                log.info("挂机卡组改为：" + t1.name());
                if (t1.deckCode() != null && !t1.deckCode().isBlank()){
                    log.info("$" + t1.deckCode());
                }
            }
        });

        runnable = () -> {
            DECK_STRATEGIES.clear();
            DECK_STRATEGIES.addAll(PluginManager.DECK_STRATEGY_PLUGINS.stream().flatMap(deckPluginWrapper -> {
                deckPluginWrapper.enabledProperty().addListener((observableValue, aBoolean, t1) -> runnable.run());
                return deckPluginWrapper.isEnabled()? deckPluginWrapper.getSpiInstance().stream().filter(deckStrategy -> Strings.isNotBlank(deckStrategy.name()) && Strings.isNotBlank(deckStrategy.id()) && deckStrategy.id().length() == 36 && deckStrategy.runMode != null && deckStrategy.runMode.length > 0) : Stream.empty();
            }).toList());
        };
        runnable.run();
        PluginManager.loadDeckProperty().addListener((observableValue, aBoolean, t1) -> {
            if (t1) {
                runnable.run();
            }
        });
    }

}
