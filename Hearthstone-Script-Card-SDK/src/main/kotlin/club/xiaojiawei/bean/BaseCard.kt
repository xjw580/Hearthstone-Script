package club.xiaojiawei.bean

import club.xiaojiawei.custom.CustomToStringGenerator.generateToString
import club.xiaojiawei.enums.CardRaceEnum
import club.xiaojiawei.enums.CardTypeEnum
import kotlin.concurrent.Volatile

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/8/29 10:25
 */
@Suppress("unused")
open class BaseCard : Entity() {

    @Volatile
    var cardType: CardTypeEnum = CardTypeEnum.UNKNOWN

    @Volatile
    var cost = 0

    @Volatile
    var atc = 0

    @Volatile
    var health = 0

    @Volatile
    var armor = 0

    @Volatile
    var damage = 0

    /**
     * 相邻增益
     */
    @Volatile
    var isAdjacentBuff = false

    /**
     * 剧毒
     */
    @Volatile
    var isPoisonous = false

    /**
     * 亡语
     */
    @Volatile
    var isDeathRattle = false

    /**
     * 创建者id
     */
    @Volatile
    var creatorEntityId: String = ""

    /**
     * 嘲讽
     */
    @Volatile
    var isTaunt = false

    /**
     * 圣盾
     */
    @Volatile
    var isDivineShield = false

    /**
     * 光环
     */
    @Volatile
    var isAura = false

    /**
     * 潜行
     */
    @Volatile
    var isStealth = false

    /**
     * 冰冻
     */
    @Volatile
    var isFrozen = false

    /**
     * 疲劳
     */
    @Volatile
    var isExhausted = false

    /**
     * 风怒
     */
    @Volatile
    var isWindFury = false

    /**
     * 战吼
     */
    @Volatile
    var isBattlecry = false

    /**
     * 发现
     */
    @Volatile
    var isDiscover = false

    /**
     * 不能被法术指向
     */
    @Volatile
    var isCantBeTargetedBySpells = false

    /**
     * 不能被英雄技能指向
     */
    @Volatile
    var isCantBeTargetedByHeroPowers = false

    /**
     * 不能被对手指向
     */
    @Volatile
    var isCantBeTargetedByOpponents = false

    /**
     * 无法被法术和英雄技能指向
     */
    @Volatile
    var isElusive = false

    /**
     * 刷出时间计数
     */
    @Volatile
    var isSpawnTimeCount = false

    /**
     * 休眠状态
     */
    @Volatile
    var isDormantAwakenConditionEnchant = false

    /**
     * 免疫
     */
    @Volatile
    var isImmune = false

    /**
     * 种族
     */
    @Volatile
    var cardRace: CardRaceEnum = CardRaceEnum.UNKNOWN

    /**
     * 磁力
     */
    @Volatile
    var isModular = false

    @Volatile
    var creator: String = ""

    /**
     * 衍生物
     */
    @Volatile
    var isPremium = false

    @Volatile
    var controller: String = ""

    /**
     * 泰坦
     */
    @Volatile
    var isTitan = false

    @Volatile
    var spellPower = 0

    @Volatile
    var isDormant = false

    fun minusHealth(health: Int) {
        this.health -= health
    }

    /**
     * 是否包含cardId
     */
    fun cardContains(baseCard: BaseCard): Boolean {
        return cardContains(baseCard.cardId)
    }

    fun cardContains(cardId: String): Boolean {
        return this.cardId.contains(cardId)
    }

    /**
     * cardId是否相同
     */
    fun cardEquals(baseCard: BaseCard): Boolean {
        return cardEquals(baseCard.cardId)
    }

    fun cardEquals(cardId: String): Boolean {
        return this.cardId == cardId
    }

    /**
     * 能不能被敌方法术指向
     */
    fun canBeTargetedByRivalSpells(): Boolean {
        return !(isElusive || isCantBeTargetedBySpells || !canBeTargetedByRival())
    }

    /**
     * 能不能被我方法术指向
     */
    fun canBeTargetedByMySpells(): Boolean {
        return !(isElusive || isCantBeTargetedBySpells || !canBeTargetedByMe())
    }

    /**
     * 能不能被敌方英雄技能指向
     */
    fun canBeTargetedByRivalHeroPowers(): Boolean {
        return canBeTargetedByRivalSpells()
    }

    /**
     * 能不能被我方英雄技能指向
     */
    fun canBeTargetedByMyHeroPowers(): Boolean {
        return canBeTargetedByMySpells()
    }

    /**
     * 能不能被敌方指向
     */
    fun canBeTargetedByRival(): Boolean {
        return !(isImmune || isStealth || isDormantAwakenConditionEnchant)
    }

    /**
     * 能不能被我方指向
     */
    fun canBeTargetedByMe(): Boolean {
        return !(isImmune || isDormantAwakenConditionEnchant)
    }

    /**
     * 能不能被攻击
     */
    fun canBeAttacked(): Boolean {
        return (cardType === CardTypeEnum.MINION || cardType === CardTypeEnum.HERO) && canBeTargetedByRival()
    }

    /**
     * 能不能动
     */
    fun canMove(): Boolean {
        return (cardType === CardTypeEnum.MINION || cardType === CardTypeEnum.HERO)
                && !(isExhausted || isFrozen || isDormantAwakenConditionEnchant || atc <= 0)
    }

    /**
     * 是不是魔免
     */
    fun isImmunityMagic(): Boolean {
        return (isCantBeTargetedByHeroPowers && isCantBeTargetedBySpells) || isElusive
    }

    override fun toString(): String {
        return generateToString(this, true)
    }

    fun toSimpleString(): String {
        return super.toString()
    }

}
