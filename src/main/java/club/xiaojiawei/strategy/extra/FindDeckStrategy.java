package club.xiaojiawei.strategy.extra;

import club.xiaojiawei.bean.BaseCard;
import club.xiaojiawei.bean.entity.Card;
import club.xiaojiawei.enums.CardRaceEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static club.xiaojiawei.enums.CardTypeEnum.MINION;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/11/5 22:17
 */
public class FindDeckStrategy extends CalcDeckStrategy{

    /**
     * 寻找指定血量随从数量
     * @return
     */
    protected int findCountByBlood(List<Card> cards, int blood){
        int count = 0;
        for (Card card : cards) {
            if (calcCardBlood(card) == blood){
                count++;
            }
        }
        return count;
    }
    /**
     * 寻找在大于等于指定攻击力中攻击力最大的
     * @param cards
     * @param atk
     * @return
     */
    protected int findMaxAtcByGEAtk(List<Card> cards, int atk){
        int index = -1, attackNum = 0;
        for (int i = 0; i < cards.size(); i++) {
            int atc = cards.get(i).getAtc();
            if (atc >= atk && atc > attackNum){
                index = i;
                attackNum = atc;
            }
        }
        return index;
    }

    /**
     *
     * @param cards
     * @param atcLine
     * @return
     */
    protected int findMaxAtcByGEAtkNotWindFury(List<Card> cards, int atcLine){
        int index = -1, atcMax = 0;
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            int atc = card.getAtc();
            if ((atc > Math.max(atcMax, atcLine) || (atc == Math.max(atcMax, atcLine) && (index != -1 && calcCardBlood(card) > calcCardBlood(cards.get(index))))) && !card.isWindFury()){
                index = i;
                atcMax = atc;
            }
        }
        return index;
    }
    protected int findCardByCardRace(List<Card> cards, CardRaceEnum...cardRace){
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            for (CardRaceEnum cardRaceEnum : cardRace) {
                if (cardRaceEnum == card.getCardRace()){
                    return i;
                }
            }
        }
        return -1;
    }
    /**
     * 寻找在等于指定血量中攻击力最大的
     * @param cards
     * @param blood 生命值减去伤害等得出
     * @return
     */
    protected int findMaxAtcByBlood(List<Card> cards, int blood, int maxAtc, boolean canLTBlood){
        int atk = 0;
        int index = -1;
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            if (
                    (calcCardBlood(card) == blood || (canLTBlood && calcCardBlood(card) < blood))
                            && card.getAtc() > atk
                            && card.getAtc() <= maxAtc
            ){
                atk = card.getAtc();
                index = i;
            }
        }
        return index;
    }
    protected int findMaxAtcByBlood(List<Card> cards, int blood, boolean canLTBlood){
        return findMaxAtcByBlood(cards, blood, Integer.MAX_VALUE, canLTBlood);
    }
    /**
     * 寻找能动的怪
     * @param cards
     * @return
     */
    protected int findCanMove(List<Card> cards){
        for (int i = 0; i < cards.size(); i++) {
            if (canMove(cards.get(i))){
                return i;
            }
        }
        return -1;
    }
    /**
     * 寻找指定费用的卡牌
     * @param cards
     * @param cost
     * @return
     */
    protected int findByCost(List<Card> cards, int cost){
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i).getCost() == cost){
                return i;
            }
        }
        return -1;
    }
    /**
     * 寻找小于等于费用的卡牌
     * @param cards
     * @param cost
     * @return
     */
    protected int findByLECost(List<Card> cards, int cost){
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i).getCost() <= cost){
                return i;
            }
        }
        return -1;
    }

    /**
     * 寻找指定cardId的card
     * @param cards
     * @param cardId
     * @return
     */
    protected int findByCardId(List<Card> cards, String cardId){
        for (int i = cards.size() - 1; i >= 0; i--) {
            String id = cards.get(i).getCardId();
            if (id != null && id.contains(cardId)){
                return i;
            }
        }
        return -1;
    }
    protected int findByCardId(List<Card> cards, Card card){
        String cardId = card.getCardId();
        for (int i = cards.size() - 1; i >= 0; i--) {
            String id = cards.get(i).getCardId();
            if (id != null && id.contains(cardId)){
                return i;
            }
        }
        return -1;
    }
    protected int findByEntityId(List<Card> cards, Card card){
        String entityId = card.getEntityId();
        for (int i = cards.size() - 1; i >= 0; i--) {
            if (Objects.equals(entityId, cards.get(i).getEntityId())){
                return i;
            }
        }
        return -1;
    }
    protected int findByEntityId(List<Card> cards, String entityId){
        for (int i = cards.size() - 1; i >= 0; i--) {
            if (Objects.equals(entityId, cards.get(i).getEntityId())){
                return i;
            }
        }
        return -1;
    }
    protected int findByCardId(List<Card> cards, BaseCard baseCard){
        for (int i = cards.size() - 1; i >= 0; i--) {
            String id = cards.get(i).getCardId();
            if (id != null && id.contains(baseCard.cardId())){
                return i;
            }
        }
        return -1;
    }
    /**
     * 寻找所有指定费用的card
     * @param cards
     * @param cost
     * @return
     */
    protected List<Card> findAllByCost(List<Card> cards, int cost){
        ArrayList<Card> list = new ArrayList<>();
        for (Card card : cards) {
            if (card.getCost() == cost){
                list.add(card);
            }
        }
        return list;
    }
    /**
     * 寻找第一个有嘲讽的随从
     * @param cards
     * @return
     */
    protected int findTauntCard(List<Card> cards){
        for (int i = cards.size() - 1; i >= 0; i--) {
            if (cards.get(i).isTaunt() && canPointedToRival(cards.get(i)) && cards.get(i).getCardType() == MINION){
                return i;
            }
        }
        return -1;
    }
    /**
     * 寻找第一个非疲劳卡牌
     * @param cards
     * @return
     */
    protected int findNotExhaustedCard(List<Card> cards){
        for (int i = 0; i < cards.size(); i++) {
            if (!cards.get(i).isExhausted()){
                return i;
            }
        }
        return -1;
    }
    protected int findCanSpellPointedToMe(List<Card> cards){
        for (int i = 0; i < cards.size(); i++) {
            if (canSpellPointedToMe(cards.get(i))){
                return i;
            }
        }
        return -1;
    }
    /**
     * 是否存在指定费用的牌
     * @param cards
     * @param cost
     * @return
     */
    protected boolean existByCost(List<Card> cards, int cost){
        return findByCost(cards, cost) != -1;
    }
}
