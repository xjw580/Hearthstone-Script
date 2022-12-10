package club.xiaojiawei.hearthstone.entity.area;

import club.xiaojiawei.hearthstone.entity.Card;
import club.xiaojiawei.hearthstone.entity.Player;
import club.xiaojiawei.hearthstone.status.War;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static club.xiaojiawei.hearthstone.constant.GameConst.CARD_AREA_MAP;

/**
 * @author 肖嘉威
 * @date 2022/11/28 19:48
 */
@Slf4j
public abstract class Area {

    protected List<Card> cards;

    protected final Map<String, Card> zeroArea;

    private int maxSize;

    public Area(int maxSize) {
        this.cards = new ArrayList<>();
        zeroArea = new HashMap<>();
        this.maxSize = maxSize;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void putZeroAreaCard(Card card){
        addZone(card);
        Player player = War.testArea(this);
//        log.info("向玩家 " + (player == War.getPlayer1()? 1 : 2) + "-" + player.getGameId() + "的" + this.getClass().getSimpleName() + " 的zeroArea 添加卡牌， entityId:" + card.getEntityId());
        zeroArea.put(card.getEntityId(), card);
    }

    protected void addZone(Card card){
        CARD_AREA_MAP.put(card.getEntityId(), this);
    };

    public int maxCardSize() {
        return maxSize;
    }

    public void setMaxSize(byte maxSize) {
        this.maxSize = maxSize;
    }

    public int size(){
        return cards.size();
    }

    public boolean add(Card card){
        if (card == null || isFull()){
            return false;
        }
        Player player = War.testArea(this);
        log.info("向玩家 " + (player == War.getPlayer1()? 1 : 2) + "-" + player.getGameId() + "的" + this.getClass().getSimpleName() + " 添加卡牌， entityId:" + card.getEntityId());
        cards.add(card);
        addZone(card);
        return true;
    }

    public boolean add(Card card, int pos){
        if (card == null){
            return false;
        }
        if (pos-- <= 0){
            putZeroAreaCard(card);
            return true;
        }
        if (isFull()){
            return false;
        }
        Player player = War.testArea(this);
        log.info("向玩家 " + (player == War.getPlayer1()? 1 : 2) + "-" + player.getGameId() + "的" + this.getClass().getSimpleName() + " 添加卡牌， entityId:" + card.getEntityId());
        if (cards.size() <= pos){
            cards.add(card);
        }else {
            cards.add(pos, card);
        }
        addZone(card);
        return true;
    }

    public Card getByCardId(String cardId){
        for (Card card : cards) {
            if (Objects.equals(cardId, card.getCardId())){
                return card;
            }
        }
        return null;
    }

    public Card getByEntityId(String entityId){
        Card c = zeroArea.get(entityId);
        if (c != null){
            return c;
        }
        for (Card card : cards) {
            if (Objects.equals(entityId, card.getEntityId())){
                return card;
            }
        }
        return null;
    }

    public Card removeByCardId(String cardId){
        for (int i = 0; i < cards.size(); i++) {
            if (Objects.equals(cardId, cards.get(i).getCardId())){
                return cards.remove(i);
            }
        }
        return null;
    }

    public Card removeByEntityId(String entityId){
        Card card = zeroArea.remove(entityId);
        if (card != null){
            return card;
        }
        for (int i = 0; i < cards.size(); i++) {
            if (Objects.equals(entityId, cards.get(i).getEntityId())){
                return cards.remove(i);
            }
        }
        return null;
    }

    public Card removeByPosition(int position){
        return --position < size()? cards.remove(position) : null;
    }

    public boolean isFull(){
        return cards.size() >= maxSize;
    }

    public boolean isEmpty(){
        return cards.size() == 0;
    }

    @Override
    public String toString() {
        return "Area{" +
                "cards=" + cards +
                ", maxSize=" + maxSize +
                '}';
    }
}
