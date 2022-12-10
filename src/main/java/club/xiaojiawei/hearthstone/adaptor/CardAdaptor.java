package club.xiaojiawei.hearthstone.adaptor;

import club.xiaojiawei.hearthstone.entity.Card;
import club.xiaojiawei.hearthstone.entity.CommonEntity;
import club.xiaojiawei.hearthstone.entity.ExtraEntity;

import java.util.Objects;

/**
 * @author 肖嘉威
 * @date 2022/11/29 12:17
 */
public class CardAdaptor {

    public static void extraEntityToCard(ExtraEntity extraEntity, Card card){
        card.setCardId(extraEntity.getCardId());
        card.setEntityId(extraEntity.getEntityId());
        card.setEntityName(extraEntity.getEntityName());
        card.setCardType(extraEntity.getExtraCard().getCardType());
        card.setCost(extraEntity.getExtraCard().getCost());
        card.setAtc(extraEntity.getExtraCard().getAtc());
        card.setHealth(extraEntity.getExtraCard().getHealth());
        card.setAdjacentBuff(extraEntity.getExtraCard().isAdjacentBuff());
        card.setPoisonous(extraEntity.getExtraCard().isPoisonous());
        card.setDeathRattle(extraEntity.getExtraCard().isDeathRattle());
        card.setCreatorEntityId(extraEntity.getExtraCard().getCreatorEntityId());
        card.setFrozen(extraEntity.getExtraCard().isFrozen());
        card.setExhausted(extraEntity.getExtraCard().isExhausted());
        card.setTaunt(extraEntity.getExtraCard().isTaunt());
    }

    public static Card commonEntityBecomeCard(CommonEntity commonEntity){
        Card card = new Card();
        card.setEntityId(commonEntity.getEntityId());
        card.setEntityName(commonEntity.getEntityName());
        card.setCardId(commonEntity.getCardId());
        return card;
    }
}
