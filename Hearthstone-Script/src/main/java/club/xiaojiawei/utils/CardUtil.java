package club.xiaojiawei.utils;

import club.xiaojiawei.bean.Card;
import club.xiaojiawei.CardAction;
import club.xiaojiawei.bean.DefaultCardAction;
import club.xiaojiawei.bean.area.Area;
import club.xiaojiawei.bean.log.ExtraEntity;
import club.xiaojiawei.bean.log.TagChangeEntity;
import club.xiaojiawei.enums.ZoneEnum;
import club.xiaojiawei.mapper.BaseCardMapper;
import club.xiaojiawei.mapper.EntityMapper;
import club.xiaojiawei.status.CardActionManager;
import club.xiaojiawei.status.War;

import java.util.function.Supplier;

import static club.xiaojiawei.data.ScriptStaticData.CARD_AREA_MAP;

/**
 * @author 肖嘉威
 * @date 2024/9/6 21:07
 */
public class CardUtil {

    public static void addAreaListener(Card card){
        if (card == null) return;
        card.areaProperty().addListener((observableValue, area1, t1) -> {
            CARD_AREA_MAP.remove(card.getEntityId());
            CARD_AREA_MAP.put(card.getEntityId(), t1);
        });
    }

    public static void updateCardByExtraEntity(ExtraEntity extraEntity, Card card){
        if (extraEntity == null || card == null) return;
        BaseCardMapper.INSTANCE.update(extraEntity.getExtraCard().getCard(), card);
        EntityMapper.INSTANCE.update(extraEntity, card);
    }

    public static Card exchangeAreaOfCard(ExtraEntity extraEntity){
        Area sourceArea = CARD_AREA_MAP.get(extraEntity.getEntityId());
        if (sourceArea == null){
            sourceArea = War.getPlayer(extraEntity.getPlayerId()).getArea(extraEntity.getZone());
        }
        Area targetArea = War.getPlayer(extraEntity.getPlayerId()).getArea(extraEntity.getExtraCard().getZone());
        Card card = sourceArea.removeByEntityId(extraEntity.getEntityId());
        updateCardByExtraEntity(extraEntity, card);
        targetArea.add(card, extraEntity.getExtraCard().getZonePos());
        return card;
    }

    public static void exchangeAreaOfCard(TagChangeEntity tagChangeEntity){
        Area sourceArea = CARD_AREA_MAP.get(tagChangeEntity.getEntityId());
        Area targetArea = War.getPlayer(tagChangeEntity.getPlayerId()).getArea(ZoneEnum.valueOf(tagChangeEntity.getValue()));
        targetArea.add(sourceArea.removeByEntityId(tagChangeEntity.getEntityId()), 0);
    }

    public static void setCardAction(Card card){
        if (card == null) {
            return;
        }
        Supplier<CardAction> supplier = CardActionManager.CARD_ACTION_MAP.get(card.getCardId());
        CardAction cardAction;
        if (supplier == null){
            cardAction = card.getAction() == null? new DefaultCardAction() : card.getAction();
        }else {
            cardAction = supplier.get();
        }
        cardAction.setBelongCard(card);
        card.setAction(cardAction);
    }

}
