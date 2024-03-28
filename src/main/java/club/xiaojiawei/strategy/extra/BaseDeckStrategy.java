package club.xiaojiawei.strategy.extra;

import club.xiaojiawei.bean.BaseCard;
import club.xiaojiawei.bean.Player;
import club.xiaojiawei.bean.area.HandArea;
import club.xiaojiawei.bean.area.PlayArea;
import club.xiaojiawei.bean.entity.Card;
import club.xiaojiawei.enums.CardTypeEnum;
import club.xiaojiawei.status.War;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

import static club.xiaojiawei.enums.CardTypeEnum.MINION;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/11/5 22:09
 */
@Slf4j
public class BaseDeckStrategy {

    protected PlayArea myPlayArea;
    protected PlayArea rivalPlayArea;
    protected HandArea myHandArea;
    protected HandArea rivalHandArea;
    protected Player me;
    protected Player rival;
    protected List<Card> myHandCards;
    protected List<Card> myPlayCards;
    protected List<Card> rivalHandCards;
    protected List<Card> rivalPlayCards;

    protected void assign() {
        this.me = War.getMe();
        this.rival = War.getRival();
        this.myHandArea = me.getHandArea();
        this.myPlayArea = me.getPlayArea();
        this.rivalHandArea = rival.getHandArea();
        this.rivalPlayArea = rival.getPlayArea();
        this.myHandCards = myHandArea.getCards();
        this.myPlayCards = myPlayArea.getCards();
        this.rivalHandCards = rivalHandArea.getCards();
        this.rivalPlayCards = rivalPlayArea.getCards();
        if (log.isDebugEnabled()){
            log.debug("我方手牌：" + myHandCards);
        }
    }

    /**
     * 卡id是否相同包含
     * @param longCard
     * @param baseCard
     * @return
     */
    protected boolean cardContains(Card longCard, BaseCard baseCard){
        return cardContains(longCard, baseCard.cardId());
    }
    protected boolean cardContains(Card longCard, String baseCardId){
        return longCard != null && longCard.getCardId() != null && longCard.getCardId().contains(baseCardId);
    }
    /**
     * 卡id是否相同
     * @param longCard
     * @param baseCard
     * @return
     */
    protected boolean cardEquals(Card longCard, BaseCard baseCard){
        return cardEquals(longCard, baseCard.cardId());
    }
    protected boolean cardEquals(Card longCard, String baseCardId){
        return longCard != null && Objects.equals(longCard.getCardId(), baseCardId);
    }

    /**
     * 能不能被对方法术指向
     * @param card
     * @return
     */
    protected boolean canSpellPointedByRival(Card card){
        return canPointedByRival(card) && !isImmunityMagic(card);
    }

    /**
     * 能不能被本方法术指向
     * @param card
     * @return
     */
    protected boolean canSpellPointedByMe(Card card){
        return canPointedByMe(card) && !isImmunityMagic(card);
    }

    /**
     * 能不能被对方指向
     * @param card
     * @return
     */
    protected boolean canPointedByRival(Card card){
        return card.getCardType() == MINION && !(card.isImmune() || card.isStealth() || card.isDormantAwakenConditionEnchant());
    }

    /**
     * 能不能被本方指向
     * @param card
     * @return
     */
    protected boolean canPointedByMe(Card card){
        return card.getCardType() == MINION && !(card.isImmune() || card.isDormantAwakenConditionEnchant() || isImmunityMagic(card));
    }

    /**
     * 能不能动
     * @param card
     * @return
     */
    protected boolean canMove(Card card){
        return card.getCardType() == CardTypeEnum.MINION && !(card.isExhausted() || card.isFrozen() || card.isDormantAwakenConditionEnchant() || card.getAtc() <= 0);
    }

    /**
     * 是不是魔免
     * @param card
     * @return
     */
    protected boolean isImmunityMagic(Card card){
        return card.isCantBeTargetedByHeroPowers() && card.isCantBeTargetedBySpells();
    }
}
