package club.xiaojiawei.bean.entity;

import club.xiaojiawei.custom.CustomToStringGenerator;
import club.xiaojiawei.enums.CardRaceEnum;
import club.xiaojiawei.enums.CardTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import static club.xiaojiawei.data.ScriptStaticData.ROBOT;

/**
 * 属性来源于{@link club.xiaojiawei.enums.TagEnum}
 * @author 肖嘉威
 * @date 2022/11/27 14:56
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Card extends Entity implements Cloneable{

    private volatile CardTypeEnum cardType;
    private volatile int cost;
    private volatile int atc;
    private volatile int health;
    private volatile int armor;
    private volatile int damage;
    /**
     * 相邻增益
     */
    private volatile boolean adjacentBuff;
    /**
     * 剧毒
     */
    private volatile boolean poisonous;
    /**
     * 亡语
     */
    private volatile boolean deathRattle;
    /**
     * 创建者id
     */
    private volatile String creatorEntityId;
    /**
     * 嘲讽
     */
    private volatile boolean taunt;
    /**
     * 圣盾
     */
    private volatile boolean divineShield;
    /**
     * 光环
     */
    private volatile boolean aura;
    /**
     * 潜行
     */
    private volatile boolean stealth;
    /**
     * 冰冻
     */
    private volatile boolean frozen;
    /**
     * 疲劳
     */
    private volatile boolean exhausted;
    /**
     * 风怒
     */
    private volatile boolean windFury;
    /**
     * 战吼
     */
    private volatile boolean battlecry;
    /**
     * 发现
     */
    private volatile boolean discover;
    /**
     * 不能被法术指向
     */
    private volatile boolean cantBeTargetedBySpells;
    /**
     * 不能被英雄技能指向
     */
    private volatile boolean cantBeTargetedByHeroPowers;
    /**
     * 刷出时间计数
     */
    private volatile boolean spawnTimeCount;
    /**
     * 休眠
     */
    private volatile boolean dormantAwakenConditionEnchant;
    /**
     * 免疫
     */
    private volatile boolean immune;
    /**
     * 种族
     */
    private volatile CardRaceEnum cardRace;
    /**
     * 磁力
     */
    private volatile boolean modular;
    private volatile String creator;
    /**
     * 衍生物
     */
    private volatile boolean premium;

    private volatile String controller;
    /**
     * 泰坦
     */
    private volatile boolean titan;
    private volatile int spellPower;
    private volatile boolean dormant;
    public Card() {
    }
    public Card(CommonEntity commonEntity) {
        entityId = commonEntity.getEntityId();
        entityName = commonEntity.getEntityName();
        cardId = commonEntity.getCardId();
    }

    public void updateByExtraEntity(ExtraEntity extraEntity){
        Card card = extraEntity.getExtraCard().getCard();
        cardId = extraEntity.cardId;
        entityId = extraEntity.entityId;
        entityName = extraEntity.entityName;

        cardType = card.getCardType();
        cost = card.getCost();
        atc = card.getAtc();
        health = card.getHealth();
        adjacentBuff = card.isAdjacentBuff();
        poisonous = card.isPoisonous();
        deathRattle = card.isDeathRattle();
        creatorEntityId = card.getCreatorEntityId();
        frozen = card.isFrozen();
        exhausted = card.isExhausted();
        taunt = card.isTaunt();
        armor = card.getArmor();
        divineShield = card.isDivineShield();
        aura = card.isAura();
        stealth = card.isStealth();
        frozen = card.isFrozen();
        exhausted = card.isExhausted();
        windFury = card.isWindFury();
        battlecry = card.isBattlecry();
        discover = card.isDiscover();
        cantBeTargetedBySpells = card.isCantBeTargetedBySpells();
        cantBeTargetedByHeroPowers = card.isCantBeTargetedByHeroPowers();
        spawnTimeCount = card.isSpawnTimeCount();
        dormantAwakenConditionEnchant = card.isDormantAwakenConditionEnchant();
        immune = card.isImmune();
        cardRace = card.getCardRace();
        premium = card.isPremium();
        modular = card.isModular();
        controller = card.getController();
        creator = card.getCreator();
        titan = card.isTitan();
        spellPower = card.getSpellPower();
        dormant = card.isDormant();
    }

    @Override
    public Card clone() {
        try {
            Card clone = (Card) super.clone();
            ExtraEntity extraEntity = new ExtraEntity();
            ExtraCard extraCard = new ExtraCard();
            extraCard.setCard(this);
            extraEntity.setExtraCard(extraCard);
            extraEntity.setCardId(this.getCardId());
            extraEntity.setEntityId(this.getEntityId());
            extraEntity.setEntityName(this.getEntityName());
            clone.updateByExtraEntity(extraEntity);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public String toString() {
        return CustomToStringGenerator.generateToString(this);
    }

}
