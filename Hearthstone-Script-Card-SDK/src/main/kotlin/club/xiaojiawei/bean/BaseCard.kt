package club.xiaojiawei.bean

import club.xiaojiawei.enums.CardRaceEnum
import club.xiaojiawei.enums.CardTypeEnum

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/8/29 10:25
 */
@Suppress("unused")
open class BaseCard : Entity() {

    /**
     * 卡牌类型：随从、法术等
     */
    @Volatile
    var cardType: CardTypeEnum = CardTypeEnum.UNKNOWN

    /**
     * 费用
     */
    @Volatile
    var cost = 0

    /**
     * 攻击力
     */
    @Volatile
    var atc = 0

    /**
     * 生命值（上限）
     */
    @Volatile
    var health = 0

    /**
     * 耐久（针对武器取代health）
     */
    @Volatile
    var durability = 0

    /**
     * 护甲
     */
    @Volatile
    var armor = 0

    /**
     * 受到的所有伤害
     */
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
     * 超级风怒
     */
    var isMegaWindfury = false

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
     * 扰魔（无法被法术和英雄技能指向）
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
     * 种族：恶魔、鱼人等
     */
    @Volatile
    var cardRace: CardRaceEnum = CardRaceEnum.UNKNOWN

    /**
     * 磁力
     */
    @Volatile
    var isModular = false

    /**
     * 创建者的entityId
     */
    @Volatile
    var creator: String = ""

    /**
     * 衍生物
     */
    @Volatile
    var isPremium = false

    /**
     * 所有者的entityId
     */
    @Volatile
    var controller: String = ""

    /**
     * 泰坦
     */
    @Volatile
    var isTitan = false

    /**
     * 法强
     */
    @Volatile
    var spellPower = 0

    /**
     * 休眠
     */
    @Volatile
    var isDormant = false

    /**
     * 具有突袭词条的随从进入战场时此值改为true，回合结束变为false，游戏日志对该tag的改变打印有2秒左右延迟，建议在打出突袭随从后多停顿一会
     */
    @Volatile
    var isAttackableByRush = false

    /**
     * 攻击时免疫
     */
    @Volatile
    var isImmuneWhileAttacking = false

    /**
     * 复生
     */
    @Volatile
    var isReborn = false

    /**
     * 视觉触发（闪电标志）
     */
    @Volatile
    var isTriggerVisual = false

    /**
     * 吸血
     */
    @Volatile
    var isLifesteal = false

    /**
     * 硬币
     */
    @Volatile
    var isCoinCard = false

    /**
     * 不可触摸（例：萨格拉斯召唤的传送门）
     */
    @Volatile
    var isUntouchable = false

    /**
     * 地标冷却期
     * 当cardType为LOCATION时有效，为true表明地标无法使用
     */
    @Volatile
    var isLocationActionCooldown = false

    /**
     * 突袭
     */
    @Volatile
    var isRush = false

    /**
     * 无法攻击（例：威严的阿努比萨斯）
     */
    @Volatile
    var isCantAttack = false

    /**
     * 过载
     */
    @Volatile
    var overload = 0

    /**
     * 在场上的回合数，双方MAIN_READY阶段更新，首次进入战场该值为0
     */
    @Volatile
    var numTurnsInPlay = 0

    /**
     * 在手上的回合数，我方MAIN_START和MAIN_NEXT阶段更新，首次进入手中该值为0
     */
    @Volatile
    var numTurnsInHand = 0


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
     * 判断卡牌是否相同，指的是cardId相同
     */
    fun cardEquals(baseCard: BaseCard): Boolean {
        return cardEquals(baseCard.cardId)
    }

    fun cardEquals(cardId: String): Boolean {
        return this.cardId == cardId
    }

    /**
     * 能不能被敌方法术指向
     * 适用的卡牌类型：[club.xiaojiawei.enums.CardTypeEnum.MINION],[club.xiaojiawei.enums.CardTypeEnum.HERO]
     */
    fun canBeTargetedByRivalSpells(): Boolean {
        return !(isElusive || isCantBeTargetedBySpells || !canBeTargetedByRival())
    }

    /**
     * 能不能被我方法术指向
     * 适用的卡牌类型：[club.xiaojiawei.enums.CardTypeEnum.MINION],[club.xiaojiawei.enums.CardTypeEnum.HERO]
     */
    fun canBeTargetedByMySpells(): Boolean {
        return !(isElusive || isCantBeTargetedBySpells || !canBeTargetedByMe())
    }

    /**
     * 能不能被敌方英雄技能指向
     * 适用的卡牌类型：[club.xiaojiawei.enums.CardTypeEnum.MINION],[club.xiaojiawei.enums.CardTypeEnum.HERO]
     */
    fun canBeTargetedByRivalHeroPowers(): Boolean {
        return canBeTargetedByRivalSpells()
    }

    /**
     * 能不能被我方英雄技能指向
     * 适用的卡牌类型：[club.xiaojiawei.enums.CardTypeEnum.MINION],[club.xiaojiawei.enums.CardTypeEnum.HERO]
     */
    fun canBeTargetedByMyHeroPowers(): Boolean {
        return canBeTargetedByMySpells()
    }

    /**
     * 能不能被敌方指向
     * 适用的卡牌类型：[club.xiaojiawei.enums.CardTypeEnum.MINION],[club.xiaojiawei.enums.CardTypeEnum.HERO]
     */
    fun canBeTargetedByRival(): Boolean {
        return !(isStealth || isImmune || isDormantAwakenConditionEnchant || isUntouchable)
    }

    /**
     * 能不能被我方指向
     * 适用的卡牌类型：[club.xiaojiawei.enums.CardTypeEnum.MINION],[club.xiaojiawei.enums.CardTypeEnum.HERO]
     */
    fun canBeTargetedByMe(): Boolean {
        return (cardType === CardTypeEnum.MINION || cardType === CardTypeEnum.HERO) && !(isImmune || isDormantAwakenConditionEnchant || isUntouchable)
    }

    /**
     * 能不能被攻击
     * 适用的卡牌类型：[club.xiaojiawei.enums.CardTypeEnum.MINION],[club.xiaojiawei.enums.CardTypeEnum.HERO]
     */
    fun canBeAttacked(): Boolean {
        return (cardType === CardTypeEnum.MINION || cardType === CardTypeEnum.HERO) && canBeTargetedByRival()
    }

    /**
     * 能不能攻击
     * 适用的卡牌类型：[club.xiaojiawei.enums.CardTypeEnum.MINION],[club.xiaojiawei.enums.CardTypeEnum.HERO],[club.xiaojiawei.enums.CardTypeEnum.WEAPON]
     * 对于地标能否使用，参见[club.xiaojiawei.bean.BaseCard.isLocationActionCooldown]
     */
    fun canAttack(ignoreExhausted: Boolean = false, ignoreAtc: Boolean = false): Boolean {
        return (cardType === CardTypeEnum.MINION || cardType === CardTypeEnum.HERO || cardType === CardTypeEnum.WEAPON) && isSurvival()
                && !((isExhausted && !ignoreExhausted) || isCantAttack || isFrozen || isDormantAwakenConditionEnchant || (!ignoreAtc && atc <= 0))
    }

    /**
     * 能否使用
     * 适用的卡牌类型：[club.xiaojiawei.enums.CardTypeEnum.LOCATION],[club.xiaojiawei.enums.CardTypeEnum.HERO_POWER]
     */
    fun canPower(): Boolean {
        return (cardType === CardTypeEnum.LOCATION && !isLocationActionCooldown && isSurvival()) || (cardType === CardTypeEnum.HERO_POWER && !isExhausted)
    }

    /**
     * 是不是魔免
     */
    fun isImmunityMagic(): Boolean {
        return (isCantBeTargetedByHeroPowers && isCantBeTargetedBySpells) || isElusive
    }

    /**
     * 获取血量
     * 适用的卡牌类型：[club.xiaojiawei.enums.CardTypeEnum.MINION],[club.xiaojiawei.enums.CardTypeEnum.HERO],[club.xiaojiawei.enums.CardTypeEnum.WEAPON],[club.xiaojiawei.enums.CardTypeEnum.LOCATION]
     */
    fun blood(): Int {
        return (if (cardType === CardTypeEnum.WEAPON) durability else health) + armor - damage
    }

    /**
     * 判断是否存活
     * 适用的卡牌类型：[club.xiaojiawei.enums.CardTypeEnum.MINION],[club.xiaojiawei.enums.CardTypeEnum.HERO],[club.xiaojiawei.enums.CardTypeEnum.WEAPON],[club.xiaojiawei.enums.CardTypeEnum.LOCATION]
     */
    fun isSurvival(): Boolean {
        return blood() > 0
    }

//    override fun toString(): String {
//        return generateToString(this, true)
//    }

    fun toSimpleString(): String {
        return super.toString()
    }

}
