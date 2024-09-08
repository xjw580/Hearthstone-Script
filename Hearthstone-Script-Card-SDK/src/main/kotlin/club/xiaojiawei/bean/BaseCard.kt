package club.xiaojiawei.bean;

import club.xiaojiawei.custom.CustomToStringGenerator;
import club.xiaojiawei.enums.CardRaceEnum;
import club.xiaojiawei.enums.CardTypeEnum;
import club.xiaojiawei.mapper.BaseCardMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/8/29 10:25
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class BaseCard extends Entity implements Cloneable{

    public BaseCard() {
    }

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

    @Override
    public BaseCard clone() {
        try {
            BaseCard card = (BaseCard) super.clone();
            BaseCardMapper.INSTANCE.update(this, card);
            return card;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return CustomToStringGenerator.generateToString(this, true);
    }

    public String toSimpleString(){
        return "【entityId:" + getEntityId() + "，entityName:" + getEntityName() + "，cardId:" + getCardId() + "】";
    }
}
