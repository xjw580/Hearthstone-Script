package club.xiaojiawei.hearthstone.entity;

import lombok.Data;

/**
 * @author 肖嘉威
 * @date 2022/12/1 22:30
 */
@Data
public class CardMes {

    private String cardName;
    private String cardId;
    private int cost;
    private int priority;

    private int additionalPriority;

    public CardMes() {
    }

    public CardMes(String cardName, String cardId, int priority) {
        this.cardName = cardName;
        this.cardId = cardId;
        this.priority = priority;
    }

    public CardMes(String cardName, String cardId, int cost, int priority) {
        this.cardName = cardName;
        this.cardId = cardId;
        this.cost = cost;
        this.priority = priority;
    }

}
