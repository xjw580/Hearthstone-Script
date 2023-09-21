package club.xiaojiawei.bean.entity;

import club.xiaojiawei.enums.CardRaceEnum;
import club.xiaojiawei.enums.CardTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 属性来源于{@link club.xiaojiawei.enums.TagEnum}
 * @author 肖嘉威
 * @date 2022/11/27 14:56
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class Card extends Entity{

    private CardTypeEnum cardType;
    private volatile int cost;
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
    private volatile boolean taunt;
    /**
     * 圣盾
     */
    private volatile boolean divineShield;
    /**
     * 光环
     */
    private boolean aura;
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
    private boolean battlecry;
    /**
     * 发现
     */
    private boolean discover;
    /**
     * 不能被法术指向
     */
    private boolean cantBeTargetedBySpells;
    /**
     * 不能被英雄技能指向
     */
    private boolean cantBeTargetedByHeroPowers;
    /**
     * 刷出时间计数
     */
    private boolean spawnTimeCount;
    /**
     * 休眠
     */
    private boolean dormantAwakenConditionEnchant;
    /**
     * 免疫
     */
    private boolean immune;
    /**
     * 种族
     */
    private CardRaceEnum cardRace;
    /**
     * 磁力
     */
    private boolean modular;
    private String creator;
    /**
     * 衍生物
     */
    private boolean premium;

    private String controller;
    /**
     * 泰坦
     */
    private boolean titan;
    private int spellPower;
    public Card() {
    }
    public Card(CommonEntity commonEntity) {
        entityId = commonEntity.getEntityId();
        entityName = commonEntity.getEntityName();
        cardId = commonEntity.getCardId();
    }

    public void updateByExtraEntity(ExtraEntity extraEntity){
        ExtraCard extraCard = extraEntity.getExtraCard();
        cardId = extraEntity.cardId;
        entityId = extraEntity.entityId;
        entityName = extraEntity.entityName;
        cardType = extraCard.getCardType();
        cost = extraCard.getCost();
        atc = extraCard.getAtc();
        health = extraCard.getHealth();
        adjacentBuff = extraCard.isAdjacentBuff();
        poisonous = extraCard.isPoisonous();
        deathRattle = extraCard.isDeathRattle();
        creatorEntityId = extraCard.getCreatorEntityId();
        frozen = extraCard.isFrozen();
        exhausted = extraCard.isExhausted();
        taunt = extraCard.isTaunt();
        armor = extraCard.getArmor();
        divineShield = extraCard.isDivineShield();
        aura = extraCard.isAura();
        stealth = extraCard.isStealth();
        frozen = extraCard.isFrozen();
        exhausted = extraCard.isExhausted();
        windFury = extraCard.isWindFury();
        battlecry = extraCard.isBattlecry();
        discover = extraCard.isDiscover();
        cantBeTargetedBySpells = extraCard.isCantBeTargetedBySpells();
        cantBeTargetedByHeroPowers = extraCard.isCantBeTargetedByHeroPowers();
        spawnTimeCount = extraCard.isSpawnTimeCount();
        dormantAwakenConditionEnchant = extraCard.isDormantAwakenConditionEnchant();
        immune = extraCard.isImmune();
        cardRace = extraCard.getCardRace();
        premium = extraCard.isPremium();
        modular = extraCard.isModular();
        controller = extraCard.getController();
        creator = extraCard.getCreator();
        titan = extraCard.isTitan();
        spellPower = extraCard.getSpellPower();
    }

}
