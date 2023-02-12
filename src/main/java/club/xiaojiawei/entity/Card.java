package club.xiaojiawei.entity;

import club.xiaojiawei.enums.CardTypeEnum;
import lombok.Data;
import lombok.ToString;

/**
 * @author 肖嘉威
 * @date 2022/11/27 14:56
 */
@Data
@ToString(callSuper = true)
public class Card extends Entity{

    private CardTypeEnum cardType;

    private int cost;

    private int atc;

    private int health;
    private int armor;
    private int damage;
    /**
     * 相邻增益
     */
    private boolean adjacentBuff;

    /**
     * 剧毒
     */
    private boolean poisonous;

    /**
     * 亡语
     */
    private boolean deathRattle;

    /**
     * 创建者id
     */
    private String creatorEntityId;

    /**
     * 嘲讽
     */
    private boolean taunt;
    /**
     * 圣盾
     */
    private boolean divineShield;
    /**
     * 光环
     */
    private boolean aura;
    /**
     * 潜行
     */
    private boolean stealth;
    /**
     * 冰冻
     */
    private boolean frozen;

    private boolean exhausted;

    public Card() {
    }
    public Card(CommonEntity commonEntity) {
        entityId = commonEntity.getEntityId();
        entityName = commonEntity.getEntityName();
        cardId = commonEntity.getCardId();
    }

    public void extraEntityToCard(ExtraEntity extraEntity){
        cardId = extraEntity.cardId;
        entityId = extraEntity.entityId;
        entityName = extraEntity.entityName;
        cardType = extraEntity.getExtraCard().getCardType();
        cost = extraEntity.getExtraCard().getCost();
        atc = extraEntity.getExtraCard().getAtc();
        health = extraEntity.getExtraCard().getHealth();
        adjacentBuff = extraEntity.getExtraCard().isAdjacentBuff();
        poisonous = extraEntity.getExtraCard().isPoisonous();
        deathRattle = extraEntity.getExtraCard().isDeathRattle();
        creatorEntityId = extraEntity.getExtraCard().getCreatorEntityId();
        frozen = extraEntity.getExtraCard().isFrozen();
        exhausted = extraEntity.getExtraCard().isExhausted();
        taunt = extraEntity.getExtraCard().isTaunt();
        armor = extraEntity.getExtraCard().getArmor();
        divineShield = extraEntity.getExtraCard().isDivineShield();
        aura = extraEntity.getExtraCard().isAura();
        stealth = extraEntity.getExtraCard().isStealth();
    }

}
