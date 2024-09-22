package club.xiaojiawei.utils;

import club.xiaojiawei.CardAction;
import club.xiaojiawei.DeckStrategy;
import club.xiaojiawei.bean.Card;
import club.xiaojiawei.bean.CommonCardAction;
import club.xiaojiawei.bean.area.Area;
import club.xiaojiawei.bean.log.ExtraEntity;
import club.xiaojiawei.bean.log.TagChangeEntity;
import club.xiaojiawei.enums.ZoneEnum;
import club.xiaojiawei.mapper.BaseCardMapper;
import club.xiaojiawei.mapper.EntityMapper;
import club.xiaojiawei.status.CardActionManager;
import club.xiaojiawei.status.War;
import club.xiaojiawei.strategy.DeckStrategyActuator;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Supplier;

import static club.xiaojiawei.data.ScriptStaticData.CARD_AREA_MAP;

/**
 * @author 肖嘉威
 * @date 2024/9/6 21:07
 */
@Component
public class CardUtil {

    private static DeckStrategyActuator actuator;

    @Resource
    public void setActuator(DeckStrategyActuator actuator) {
        CardUtil.actuator = actuator;
    }

    public static void addAreaListener(Card card) {
        if (card == null) return;
        card.getAreaProperty().addListener((observableValue, area1, t1) -> {
            CARD_AREA_MAP.remove(card.getEntityId());
            CARD_AREA_MAP.put(card.getEntityId(), t1);
        });
    }

    public static void updateCardByExtraEntity(ExtraEntity extraEntity, Card card) {
        if (extraEntity == null || card == null) return;
        BaseCardMapper.Companion.getINSTANCE().update(extraEntity.getExtraCard().getCard(), card);
        EntityMapper.Companion.getINSTANCE().update(extraEntity, card);
    }

    public static Card exchangeAreaOfCard(ExtraEntity extraEntity) {
        Area sourceArea = CARD_AREA_MAP.get(extraEntity.getEntityId());
        if (sourceArea == null) {
            sourceArea = War.INSTANCE.getPlayer(extraEntity.getPlayerId()).getArea(extraEntity.getZone());
        }
        Area targetArea = War.INSTANCE.getPlayer(extraEntity.getPlayerId()).getArea(extraEntity.getExtraCard().getZone());
        Card card = sourceArea.removeByEntityId(extraEntity.getEntityId());
        updateCardByExtraEntity(extraEntity, card);
        targetArea.add(card, extraEntity.getExtraCard().getZonePos());
        return card;
    }

    public static void exchangeAreaOfCard(TagChangeEntity tagChangeEntity) {
        Area sourceArea = CARD_AREA_MAP.get(tagChangeEntity.getEntityId());
        Area targetArea = War.INSTANCE.getPlayer(tagChangeEntity.getPlayerId()).getArea(ZoneEnum.valueOf(tagChangeEntity.getValue()));
        targetArea.add(sourceArea.removeByEntityId(tagChangeEntity.getEntityId()), 0);
    }

    public static void setCardAction(Card card) {
        if (card == null) {
            return;
        }
        Supplier<CardAction> supplier = null;
        DeckStrategy deckStrategy = DeckStrategyActuator.INSTANCE.getDeckStrategy();
        Map<String, Supplier<CardAction>> map = CardActionManager.INSTANCE.getCARD_ACTION_MAP().get(deckStrategy.getPluginId());
        if (map == null) {
            map = CardActionManager.INSTANCE.getCARD_ACTION_MAP().get("");
        }
        if (map != null) {
            supplier = map.get(card.getCardId());
        }

        CardAction cardAction;
        if (supplier == null) {
            cardAction = card.getAction() == CommonCardAction.Companion.getDEFAULT() ? new CommonCardAction() : card.getAction();
        } else {
            cardAction = supplier.get();
        }
        cardAction.setBelongCard(card);
        card.setAction(cardAction);
    }

}
