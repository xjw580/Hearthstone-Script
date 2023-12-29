package club.xiaojiawei.bean.entity;

import club.xiaojiawei.custom.CustomToStringGenerator;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.enums.CardRaceEnum;
import club.xiaojiawei.enums.CardTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * 属性来源于{@link club.xiaojiawei.enums.TagEnum}
 * @author 肖嘉威
 * @date 2022/11/27 14:56
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
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
        updateFields(card, this);
    }
    private void updateFields(Card source, Card destination){
        Class<? extends Card> sourceClass = source.getClass();
        Class<? extends Card> destinationClass = destination.getClass();
        Field[] sourceFields = sourceClass.getDeclaredFields();
        for (Field sourceField : sourceFields) {
            if (Objects.equals(sourceField.getName(), ScriptStaticData.LOG_FIELD_NAME)){
                continue;
            }
            sourceField.setAccessible(true);
            Field destinationField;
            try {
                destinationField = destinationClass.getDeclaredField(sourceField.getName());
                destinationField.setAccessible(true);
                // 更新目标对象的字段值
                destinationField.set(destination, sourceField.get(source));
            } catch (NoSuchFieldException e) {
                // 如果目标对象没有对应的字段，则忽略
                log.warn(sourceField.getName() + "字段未找到", e);
            } catch (IllegalAccessException e) {
                log.error(sourceField.getName() + "字段非法访问", e);
            }
        }
    }

    @Override
    public Card clone() {
        try {
            Card cloneCard = (Card) super.clone();
            updateFields(this, cloneCard);
            return cloneCard;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public String toString() {
        return CustomToStringGenerator.generateToString(this);
    }

}
