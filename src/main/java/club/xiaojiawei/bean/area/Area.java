package club.xiaojiawei.bean.area;

import club.xiaojiawei.bean.entity.Card;
import club.xiaojiawei.bean.Player;
import club.xiaojiawei.enums.ZoneEnum;
import club.xiaojiawei.status.War;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.util.*;

import static club.xiaojiawei.data.ScriptStaticData.CARD_AREA_MAP;
import static club.xiaojiawei.data.ScriptStaticData.UNKNOWN;

/**
 * @author 肖嘉威
 * @date 2022/11/28 19:48
 */
@Slf4j
public abstract class Area {

    @Getter
    protected volatile List<Card> cards;

    protected final Map<String, Card> zeroCards;

    @Setter
    @Getter
    private int maxSize;

    public Area(int maxSize) {
        this.cards = new ArrayList<>();
        this.zeroCards = new HashMap<>();
        this.maxSize = maxSize;
    }

    protected void addZone(Card card){
        CARD_AREA_MAP.put(card.getEntityId(), this);
    }

    protected void addZeroCard(Card card){
        zeroCards.put(card.getEntityId(), card);
        addZone(card);
        if (log.isDebugEnabled()){
            logDebug(card, "zeroArea");
        }
    }
    protected void addCard(Card card, int pos){
        if (pos >= cards.size()){
            cards.add(card);
        }else {
            cards.add(pos, card);
        }
        addZone(card);
        logInfo(card, "");
    }
    protected void logInfo(Card card, String name){
        Player player = War.getPlayerByArea(this);
        if (Strings.isNotEmpty(name)){
            name = "的【" + name + "】";
        }
        log.info("向玩家" + player.getPlayerId() + "【" + player.getGameId() + "】的【" + ZoneEnum.valueOf(this.getClass().getSimpleName().substring(0, this.getClass().getSimpleName().length() - 4).toUpperCase()).getComment() + "】" + name + "添加卡牌，entityId:" + card.getEntityId() + "，entityName:" + (Objects.equals(card.getEntityName(), UNKNOWN)? "" : card.getEntityName()) + "，cardId:" + card.getCardId() + "，size:" + cards.size());
    }
    protected void logDebug(Card card, String name){
        Player player = War.getPlayerByArea(this);
        if (Strings.isNotEmpty(name)){
            name = "的【" + name + "】";
        }
        log.debug("向玩家" + player.getPlayerId() + "【" + player.getGameId() + "】的【" + ZoneEnum.valueOf(this.getClass().getSimpleName().substring(0, this.getClass().getSimpleName().length() - 4).toUpperCase()).getComment() + "】" + name + "添加卡牌，entityId:" + card.getEntityId() + "，entityName:" + (Objects.equals(card.getEntityName(), UNKNOWN)? "" : card.getEntityName()) + "，cardId:" + card.getCardId() + "，size:" + cards.size());
    }

    /**
     * 向末尾添加
     * @param card
     * @return
     */
    public boolean add(Card card){
        return add(card, cards.size() + 1);
    }

    /**
     * @param card
     * @param pos 注意！不是index
     * @return
     */
    public boolean add(Card card, int pos){
        boolean result = true;
        if (card == null){
            result = false;
        }else {
            if (pos-- <= 0){
                addZeroCard(card);
            }else {
                addCard(card, pos);
            }
        }
        return result;
    }

    public int size(){
        return cards.size();
    }

    public Card findByEntityId(String entityId){
        Card card = zeroCards.get(entityId);
        if (card == null){
            for (Card c : cards) {
                if (Objects.equals(entityId, c.getEntityId())){
                    card = c;
                    break;
                }
            }
        }
        return card;
    }

    public Card removeByEntityId(String entityId){
        Card card = removeByEntityIdInZeroArea(entityId);
        if (card == null){
            for (int i = 0; i < cards.size(); i++) {
                if (Objects.equals(entityId, cards.get(i).getEntityId())){
                    card =  cards.remove(i);
                    break;
                }
            }
        }
        return card;
    }
    public Card removeByEntityIdInZeroArea(String entityId){
        return zeroCards.remove(entityId);
    }

    public boolean isFull(){
        return cards.size() >= maxSize;
    }

    public boolean isEmpty(){
        return cards.isEmpty();
    }

    @Override
    public String toString() {
        return "Area{" +
                "cards=" + cards +
                ", maxSize=" + maxSize +
                '}';
    }
}
