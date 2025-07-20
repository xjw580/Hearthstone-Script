package club.xiaojiawei.bean

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.area.Area
import club.xiaojiawei.enums.CardTypeEnum
import club.xiaojiawei.enums.TargetEnum
import club.xiaojiawei.mapper.BaseCardMapper
import club.xiaojiawei.status.WAR
import java.util.function.BiConsumer
import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis

/**
 * @author 肖嘉威
 * @date 2022/11/27 14:56
 */

@Suppress("ktlint:standard:no-consecutive-comments")
/**
 * 发射星舰按钮的卡牌id
 */
private const val LAUNCH_CARD_ID = "GDB_905"

/**
 * 终止发射按钮的卡牌id
 */
private const val CANCEL_LAUNCH_CARD_ID = "GDB_906"

class Card(
    var action: CardAction,
) : BaseCard(),
    Cloneable {
    val child: MutableList<Card> by lazy { mutableListOf() }

    /**
     * 供mcts策略模拟用
     */
    var attackCount: Int = 0

    /**
     * 卡牌所在区域：手牌区、战场区等
     */
    var area: Area = Area.UNKNOWN_AREA

    var isUncertain: Boolean = false

    fun resetExhausted() {
        isExhausted = false
        attackCount = 0
    }

    fun plusAtc(atc: Int) {
        this.atc += atc
    }

    fun plusHealth(health: Int) {
        this.health += health
    }

    fun minusHealth(health: Int) {
        this.health -= health
    }

    /**
     * [cardId]改变监听器
     */
    var cardIdChangeListener: BiConsumer<String, String>? = null

    /**
     * [damage]改变监听器
     */
    var damageChangeListener: BiConsumer<Int, Int>? = null

    override var damage
        get() = super.damage
        set(value) {
            val oldDamage = super.damage
            if (oldDamage != value) {
                super.damage = value
                val war = area.player.war
                if (war !== WAR) {
                    val damageDiff = value - oldDamage
                    damageChangeListener?.accept(oldDamage, value)
                    if (damageDiff > 0) {
                        val myPlayCard = war.me.playArea.cards
                        if (myPlayCard.isNotEmpty()) {
                            val copyMyPlayCards = myPlayCard.toList()
                            for (card in copyMyPlayCards) {
                                card.action.triggerPlayCardInjured(war, this, damageDiff)
                            }
                        }
                        val rivalPlayCards = war.rival.playArea.cards
                        if (rivalPlayCards.isNotEmpty()) {
                            val copyRivalPlayCards = rivalPlayCards.toList()
                            for (card in copyRivalPlayCards) {
                                card.action.triggerPlayCardInjured(war, this, damageDiff)
                            }
                        }
                        val myHandCards = war.me.handArea.cards
                        if (myHandCards.isNotEmpty()) {
                            val copyMyHandCards = myHandCards.toList()
                            for (card in copyMyHandCards) {
                                card.action.triggerPlayCardInjured(war, this, damageDiff)
                            }
                        }
                        val rivalHandCards = war.rival.handArea.cards
                        if (rivalHandCards.isNotEmpty()) {
                            val copyRivalHandCards = rivalHandCards.toList()
                            for (card in copyRivalHandCards) {
                                card.action.triggerPlayCardInjured(war, this, damageDiff)
                            }
                        }
                    }
                }
            } else {
                super.damage = value
            }
        }

    override var cardId
        get() = super.cardId
        set(value) {
            val oldCardId = super.cardId
            if (value != oldCardId) {
                super.cardId = value
                cardIdChangeListener?.accept(oldCardId, value)
            } else {
                super.cardId = value
            }
        }

    /**
     * 受到伤害
     */
    fun injured(damage: Int) {
        if (!canHurt() || damage == 0) return
        if (isDivineShield) {
            isDivineShield = false
            return
        }
        var increaseInjury = 0
        if (cardType === CardTypeEnum.HERO) {
            increaseInjury = if (area.player === area.player.war.me) {
                area.player.war.myHeroIncreaseInjury
            } else {
                area.player.war.rivalHeroIncreaseInjury
            }
        }
        this.damage += damage + increaseInjury
    }

    /**
     * 能受到伤害
     */
    fun canHurt(): Boolean =
        (cardType === CardTypeEnum.MINION || cardType === CardTypeEnum.HERO) &&
                !(isDead() || isImmune || isDormantAwakenConditionEnchant)

    /**
     * 判断卡牌是否类似，同一张牌所属扩展包不同，[cardId]也不相同
     */
    fun cardSame(baseCard: BaseCard): Boolean = cardSame(baseCard.cardId)

    fun cardSame(cardId: String): Boolean = this.cardId.contains(cardId)

    /**
     * 判断卡牌是否相同，指的是[cardId]相同
     */
    fun cardEquals(baseCard: BaseCard): Boolean = cardEquals(baseCard.cardId)

    fun cardEquals(cardId: String): Boolean = this.cardId == cardId

    /**
     * 能被敌方法术指向
     * 适用的卡牌类型：[club.xiaojiawei.enums.CardTypeEnum.MINION], [club.xiaojiawei.enums.CardTypeEnum.HERO]
     */
    fun canBeTargetedByRivalSpells(): Boolean = !(isElusive || isCantBeTargetedBySpells || !canBeTargetedByRival())

    /**
     * 能被我方法术指向
     * 适用的卡牌类型：[club.xiaojiawei.enums.CardTypeEnum.MINION], [club.xiaojiawei.enums.CardTypeEnum.HERO]
     */
    fun canBeTargetedByMySpells(): Boolean = !(isElusive || isCantBeTargetedBySpells || !canBeTargetedByMe())

    /**
     * 能被敌方英雄技能指向
     * 适用的卡牌类型：[club.xiaojiawei.enums.CardTypeEnum.MINION], [club.xiaojiawei.enums.CardTypeEnum.HERO]
     */
    fun canBeTargetedByRivalHeroPowers(): Boolean = canBeTargetedByRivalSpells()

    /**
     * 能被我方英雄技能指向
     * 适用的卡牌类型：[club.xiaojiawei.enums.CardTypeEnum.MINION], [club.xiaojiawei.enums.CardTypeEnum.HERO]
     */
    fun canBeTargetedByMyHeroPowers(): Boolean = canBeTargetedByMySpells()

    /**
     * 能被敌方指向
     * 适用的卡牌类型：[club.xiaojiawei.enums.CardTypeEnum.MINION], [club.xiaojiawei.enums.CardTypeEnum.HERO]
     */
    fun canBeTargetedByRival(): Boolean = !(isStealth || isImmune || isDormantAwakenConditionEnchant || isUntouchable)

    /**
     * 能被我方指向
     * 适用的卡牌类型：[club.xiaojiawei.enums.CardTypeEnum.MINION], [club.xiaojiawei.enums.CardTypeEnum.HERO]
     */
    fun canBeTargetedByMe(): Boolean =
        (cardType === CardTypeEnum.MINION || cardType === CardTypeEnum.HERO) &&
                !(isImmune || isDormantAwakenConditionEnchant || isUntouchable)

    /**
     * 能被攻击
     * 适用的卡牌类型：[club.xiaojiawei.enums.CardTypeEnum.MINION], [club.xiaojiawei.enums.CardTypeEnum.HERO]
     */
    fun canBeAttacked(): Boolean =
        (cardType === CardTypeEnum.MINION || cardType === CardTypeEnum.HERO) && canBeTargetedByRival()

    /**
     * 能攻击
     * 适用的卡牌类型：[club.xiaojiawei.enums.CardTypeEnum.MINION], [club.xiaojiawei.enums.CardTypeEnum.HERO]
     * 对于地标和技能，参见[club.xiaojiawei.bean.Card.canPower]
     */
    fun canAttack(
        ignoreExhausted: Boolean = false,
        ignoreAtc: Boolean = false,
    ): Boolean = getAttackTarget(ignoreExhausted, ignoreAtc) !== TargetEnum.NONE

    /**
     * 无法攻击
     * 适用的卡牌类型：[club.xiaojiawei.enums.CardTypeEnum.MINION], [club.xiaojiawei.enums.CardTypeEnum.HERO]
     * 对于地标和技能，参见[club.xiaojiawei.bean.Card.canPower]
     */
    fun cannotAttack(
        ignoreExhausted: Boolean = false,
        ignoreAtc: Boolean = false,
    ): Boolean = getAttackTarget(ignoreExhausted, ignoreAtc) === TargetEnum.NONE

    /**
     * 获取能攻击的目标
     * 比如刚下场的突袭随从只能解场，此时返回[club.xiaojiawei.enums.TargetEnum.MINION]
     */
    private fun getAttackTarget(
        ignoreExhausted: Boolean = false,
        ignoreAtc: Boolean = false,
    ): TargetEnum {
        if (!(cardType === CardTypeEnum.MINION || cardType === CardTypeEnum.HERO) && isAlive()) return TargetEnum.NONE

        if ((isExhausted && !ignoreExhausted) ||
            isUntouchable ||
            isCantAttack ||
            isFrozen ||
            isDormantAwakenConditionEnchant ||
            (!ignoreAtc && atc <= 0)
        ) {
            return TargetEnum.NONE
        }

        if (isAttackableByRush) return TargetEnum.MINION

        return TargetEnum.HERO_MINION
    }

    /**
     * 能使用/激活
     * 适用的卡牌类型：[club.xiaojiawei.enums.CardTypeEnum.LOCATION], [club.xiaojiawei.enums.CardTypeEnum.HERO_POWER], [club.xiaojiawei.enums.CardTypeEnum.MINION]中的星舰
     */
    fun canPower(): Boolean =
        (cardType === CardTypeEnum.LOCATION && !isLocationActionCooldown && !isExhausted && isAlive()) ||
                (cardType === CardTypeEnum.HERO_POWER && !isExhausted) ||
                (cardType === CardTypeEnum.MINION && isLaunchpad)

    /**
     * 是否受伤
     * 适用的卡牌类型：[club.xiaojiawei.enums.CardTypeEnum.MINION], [club.xiaojiawei.enums.CardTypeEnum.HERO], [club.xiaojiawei.enums.CardTypeEnum.WEAPON], [club.xiaojiawei.enums.CardTypeEnum.LOCATION]
     */
    fun isInjured(): Boolean = damage > armor

    /**
     * 是不是魔免
     */
    fun isImmunityMagic(): Boolean = (isCantBeTargetedByHeroPowers && isCantBeTargetedBySpells) || isElusive

    /**
     * 获取血量（就是你在游戏中看到的血量）
     * 适用的卡牌类型：[club.xiaojiawei.enums.CardTypeEnum.MINION], [club.xiaojiawei.enums.CardTypeEnum.HERO], [club.xiaojiawei.enums.CardTypeEnum.WEAPON], [club.xiaojiawei.enums.CardTypeEnum.LOCATION]
     */
    fun blood(): Int = bloodLimit() - damage

    /**
     * 判断是否存活
     * 适用的卡牌类型：[club.xiaojiawei.enums.CardTypeEnum.MINION], [club.xiaojiawei.enums.CardTypeEnum.HERO], [club.xiaojiawei.enums.CardTypeEnum.WEAPON], [club.xiaojiawei.enums.CardTypeEnum.LOCATION]
     */
    fun isAlive(): Boolean = blood() > 0

    /**
     * 判断是否死亡
     * 适用的卡牌类型：[club.xiaojiawei.enums.CardTypeEnum.MINION], [club.xiaojiawei.enums.CardTypeEnum.HERO], [club.xiaojiawei.enums.CardTypeEnum.WEAPON], [club.xiaojiawei.enums.CardTypeEnum.LOCATION]
     */
    fun isDead(): Boolean = blood() <= 0

    /**
     * 获取发射星舰所需费用
     */
    fun launchCost(): Int {
        if (isLaunchpad) {
            for (card in child) {
                if (card.cardSame(LAUNCH_CARD_ID)) {
                    return card.cost
                }
            }
        }
        return 5
    }

    @Override
    public override fun clone(): Card {
        try {
            val card = Card(this.action.createNewInstance())
            BaseCardMapper.INSTANCE.update(this, card)
            card.attackCount = attackCount
            card.action.belongCard = card
            return card
        } catch (e: CloneNotSupportedException) {
            throw RuntimeException(e)
        }
    }

    /**
     * 获取血量上限
     * 适用的卡牌类型：[club.xiaojiawei.enums.CardTypeEnum.MINION], [club.xiaojiawei.enums.CardTypeEnum.HERO], [club.xiaojiawei.enums.CardTypeEnum.WEAPON], [club.xiaojiawei.enums.CardTypeEnum.LOCATION]
     */
    fun bloodLimit(): Int = (if (cardType === CardTypeEnum.WEAPON) durability else health) + armor
}