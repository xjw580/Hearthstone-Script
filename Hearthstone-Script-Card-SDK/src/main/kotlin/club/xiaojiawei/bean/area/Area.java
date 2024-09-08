package club.xiaojiawei.bean.area;

import club.xiaojiawei.bean.Card;
import club.xiaojiawei.bean.Entity;
import club.xiaojiawei.bean.Player;
import club.xiaojiawei.enums.ZoneEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 区域抽象类
 * @author 肖嘉威
 * @date 2022/11/28 19:48
 */
@Slf4j
public abstract class Area {

    @Getter
    protected final List<Card> cards;

    private final Map<String, Card> zeroCards;

    @Setter
    @Getter
    private volatile int maxSize;

    @Setter
    @Getter
    private volatile int oldMaxSize;

    @Setter
    @Getter
    private volatile int defaultMaxSize;

    @Getter
    private final Player player;

    public Area(int maxSize) {
        this(maxSize, maxSize);
    }

    public Area(int maxSize, Player player) {
        this(maxSize, maxSize, player);
    }

    public Area(int maxSize, int defaultMaxSize) {
        this(maxSize, defaultMaxSize, Player.UNKNOWN_PLAYER);
    }

    public Area(int maxSize, int defaultMaxSize, Player player) {
        this.cards = new ArrayList<>();
        this.zeroCards = new HashMap<>();
        this.maxSize = maxSize;
        this.defaultMaxSize = defaultMaxSize;
        this.player = player;
    }

    protected void addZone(Card card){
        if (card != null) {
            card.setArea(this);
        }
    }
    protected void removeZone(Card card){
        if (card != null) {
            card.setArea(null);
        }
    }

    protected void addZeroCard(Card card){
        zeroCards.put(card.getEntityId(), card);
        addZone(card);
        if (log.isDebugEnabled()){
            log.debug(getLogText(card, "zeroArea"));
        }
    }
    protected void addCard(Card card, int pos){
        if (pos >= cards.size()){
            cards.add(card);
        }else {
            cards.add(pos, card);
        }
        addZone(card);
        log.info(getLogText(card, ""));
    }

    protected boolean removeCard(Card card){
        removeZone(card);
        return cards.remove(card);
    }
    protected Card removeCard(int index){
        Card remove = cards.remove(index);
        removeZone(remove);
        return remove;
    }
    protected Card removeZeroCard(String entityId){
        Card remove = zeroCards.remove(entityId);
        removeZone(remove);
        return remove;
    }

    protected String getLogText(Card card, String name){
        if (name != null && !name.isEmpty()){
            name = String.format("的【%s】", name);
        }
        return String.format("向玩家%s【%s】的【%s】%s添加卡牌，entityId:%s，entityName:%s，cardId:%s，size:%d",
                player.getPlayerId(),
                player.getGameId(),
                ZoneEnum.valueOf(this.getClass().getSimpleName().substring(0, this.getClass().getSimpleName().length() - 4).toUpperCase()).getComment(),
                name,
                card.getEntityId(),
                (Objects.equals(card.getEntityName(), Entity.UNKNOWN_ENTITY_NAME)? "" : card.getEntityName()),
                card.getCardId(),
                cards.size()
        );
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

    public int cardSize(){
        return cards.size();
    }

    public int indexOfCard(Card card){
        if (card == null){
            return -2;
        }
        return indexOfCard(card.getEntityId());
    }

    public int indexOfCard(String entityId){
        if (zeroCards.containsKey(entityId)){
            return -1;
        }
        for (int i = 0; i < cards.size(); i++) {
            if (Objects.equals(cards.get(i).getEntityId(), entityId)){
                return i;
            }
        }
        return -2;
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
                    card =  removeCard(i);
                    break;
                }
            }
        }
        return card;
    }
    public Card removeByEntityIdInZeroArea(String entityId){
        return removeZeroCard(entityId);
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
