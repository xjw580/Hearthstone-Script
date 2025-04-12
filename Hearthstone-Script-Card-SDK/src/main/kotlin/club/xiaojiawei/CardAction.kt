package club.xiaojiawei

import club.xiaojiawei.bean.*
import club.xiaojiawei.bean.area.HandArea
import club.xiaojiawei.bean.area.isValid
import club.xiaojiawei.config.log
import club.xiaojiawei.enums.CardTypeEnum
import club.xiaojiawei.util.CardUtil
import club.xiaojiawei.util.isTrue
import java.util.concurrent.atomic.AtomicLong
import java.util.function.Supplier

/**
 * @author 肖嘉威
 * @date 2024/9/8 18:42
 */

private const val MAX_ERROR_LOG_COUNT = 100L

private val errorLogCount = AtomicLong()

abstract class CardAction(
    createDefaultAction: Boolean = true,
) {
    protected var depth = 0

    /**
     * 是否尝试打出过
     */
    var executedPower = false

    protected var commonAction: CardAction? = null

    /**
     * 所属卡牌
     */
    var belongCard: Card? = null
        set(value) {
            field = value
            commonAction?.belongCard = belongCard
        }

    init {
        if (createDefaultAction) {
            this.commonAction = commonActionFactory?.get()
            this.commonAction?.belongCard = belongCard
        }
    }

    /**
     * [belongCard]被使用的所有可能动作
     * 例如：[belongCard]为萨满基础技能时，那么此时至多可以返回一个[Action]，即在战场未满的情况下生成图腾至战场这个[Action]
     * 对于战场上的技能，地标使用都是调用此方法生成动作
     * @param war 此卡牌所处的战局，注意：生成的[Action]中应使用[Action]内部传递的war，而不是此处的war
     * @param player 此卡牌所处的玩家
     */
    open fun generatePowerActions(
        war: War,
        player: Player,
    ): List<PowerAction> =
        belongCard?.let { card: Card ->
            if (card.isLaunchpad && player.usableResource >= card.launchCost()) {
                listOf(
                    PowerAction({ newWar ->
                        findSelf(newWar)?.action?.launch()
                    }, { newWar ->
//                        模拟发射
                        findSelf(newWar)?.let { card ->
                            card.area.player.usedResources += card.launchCost()
                            card.isLaunchpad = false
                            card.isHideStats = false
                            CardUtil.handleCardExhaustedWhenIntoPlayArea(card)
                            card.atc = card.area.player.starshipAtc
                            card.health = card.area.player.starshipHealth
                            card.area.player.starshipAtc = 0
                            card.area.player.starshipHealth = 0
                        }
                    }),
                )
            } else {
                emptyList()
            }
        } ?: emptyList()

    /**
     * [belongCard]从手牌中打出的所有可能动作（包含战吼效果）
     * 例如：[belongCard]为老红龙：血变成15，那么此时可以返回两个[Action]，一个[Action]作用于敌方英雄，一个[Action]作用于己方英雄
     * @param war 此卡牌所处的战局，注意：生成的[Action]中应使用[Action]内部传递的war，而不是此处的war
     * @param player 此卡牌所处的玩家
     */
    open fun generatePlayActions(
        war: War,
        player: Player,
    ): List<PlayAction> {
        return belongCard?.let { card ->
            val entityId = card.entityId
            if (entityId.isBlank()) {
                log.warn { "entityId为空，belongCard：$belongCard" }
                return emptyList()
            }
//            由于不知道法术的效果，所以不生成action
            if (card.cardType === CardTypeEnum.SPELL) {
                return emptyList()
            }
            listOf(
                PlayAction({ newWar ->
                    for ((index, c) in newWar.rival.playArea.cards
                        .withIndex()) {
                        findSelf(newWar)?.action?.power(false)?.let {
                            it.pointTo(index, true) ?: delay()
                        }
                        return@PlayAction
                    }
                    for ((index, c) in newWar.me.playArea.cards
                        .withIndex()) {
                        findSelf(newWar)?.action?.power(false)?.let {
                            it.pointTo(index, true) ?: delay()
                        }
                        return@PlayAction
                    }
                    newWar.rival.playArea.hero?.let { hero ->
                        findSelf(newWar)?.action?.power(false)?.let {
                            it.pointTo(hero, true) ?: delay()
                        }
                        return@PlayAction
                    }
                    newWar.me.playArea.hero?.let { hero ->
                        findSelf(newWar)?.action?.power(false)?.let {
                            it.pointTo(hero, true) ?: delay()
                        }
                        return@PlayAction
                    }
                    findSelf(newWar)?.action?.power()
                }, { newWar ->
                    spendSelfCost(newWar)
                    val me = newWar.me
                    removeSelf(newWar)?.let { card ->
                        CardUtil.handleCardExhaustedWhenIntoPlayArea(card)
                        me.playArea.safeAdd(card)
                    }
                }),
            )
        } ?: emptyList()
    }

    /**
     * belongCard攻击的所有可能动作
     * 例如：敌方战场有2个可被攻击的非嘲讽随从，此时可以返回三个action，即攻击两个随从的action和攻击敌方英雄的action
     * @param war 此卡牌所处的战局，注意：生成的[Action]中应使用[Action]内部传递的war，而不是此处的war
     * @param player 此卡牌所处的玩家
     */
    open fun generateAttackActions(
        war: War,
        player: Player,
    ): List<AttackAction> {
        return belongCard?.let { card ->
            val entityId = card.entityId
            if (entityId.isBlank()) {
                log.warn { "entityId为空，belongCard：$belongCard" }
                return emptyList()
            }
            val result = mutableListOf<AttackAction>()
            val rivalTauntCards = CardUtil.getTauntCards(war.rival.playArea.cards, true)
            val rivalPlayCards = if (rivalTauntCards.isEmpty()) war.rival.playArea.cards else rivalTauntCards
            for (rivalPlayCard in rivalPlayCards) {
                if (rivalPlayCard.canBeAttacked()) {
                    result.add(
                        AttackAction({ newWar ->
                            findSelf(newWar)?.let { myCard ->
                                rivalPlayCard.action.findSelf(newWar)?.let { rivalCard ->
                                    myCard.action.attack(rivalCard)
                                }
                            }
                        }, { newWar ->
                            findSelf(newWar)?.let { myCard ->
                                rivalPlayCard.action.findSelf(newWar)?.let { rivalCard ->
                                    CardUtil.simulateAttack(newWar, myCard, rivalCard)
                                }
                            }
                        }),
                    )
                }
            }
//            无嘲讽且不是刚下场的突袭随从才可以打脸
            if (rivalTauntCards.isEmpty() && !card.isAttackableByRush) {
                war.rival.playArea.hero?.let { rivalHero ->
                    rivalHero.canBeAttacked().isTrue {
                        result.add(
                            AttackAction({ newWar ->
                                findSelf(newWar)?.action?.attack(newWar.rival.playArea.hero)
                            }, { newWar ->
                                newWar.rival.playArea.hero?.let { rivalHero ->
                                    findSelf(newWar)?.let { myCard ->
                                        CardUtil.simulateAttack(newWar, myCard, rivalHero)
                                    }
                                }
                            }),
                        )
                    }
                }
            }
            result
        } ?: emptyList()
    }

    /**
     * 触发受到伤害
     * @param war 此卡牌所处的战局
     * @param player 此卡牌所处的玩家
     */
    open fun triggerDamage(war: War) {}

    /**
     * 触发亡语
     * @param war 此卡牌所处的战局
     * @param player 此卡牌所处的玩家
     */
    open fun triggerDeathRattle(war: War) {}

    /**
     * 触发回合结束
     * @param war 此卡牌所处的战局
     */
    open fun triggerTurnEnd(war: War) {}

    /**
     * 触发回合开始
     * @param war 此卡牌所处的战局
     */
    open fun triggerTurnStart(war: War) {}

    /**
     * 触发死亡
     * @param war 此卡牌所处的战局
     * @param player 此卡牌所处的玩家
     */
    fun triggerDeath(war: War) {
//        亡语大于复生，所以先触发亡语再触发复生
        findSelf(war)?.let { card: Card ->
            if (card.isDeathRattle) {
                triggerDeathRattle(war)
            }
            if (card.isReborn) {
                triggerReborn(war)
            }
        }
        removeSelf(war)
    }

    /**
     * 触发复生
     */
    fun triggerReborn(war: War) {
        findSelf(war)?.let { card ->
            val area = card.area
            if (area.isValid()) {
                val index = area.indexOfCard(card)
                val newCard =
                    card.clone().apply {
                        damage = health - 1
                        armor = 0
                        isExhausted = true
                        isReborn = false
                    }
                if (index < 0) {
                    if (area.isFull) return
                    war.addCard(newCard, card.area.player.playArea)
                } else { // 说明原卡牌还未从area中移除
                    if (area.cardSize() > area.maxSize) return
//                添加至原卡牌位置的右侧
                    war.addCard(newCard, card.area.player.playArea, index + 2)
                }
            }
        }
    }

    /**
     * 使用卡牌
     * @return 为null表示执行失败
     */
    fun power(isPause: Boolean = true): CardAction? {
        if (isStop()) return null
        val result = execPower()
        if (result) {
            executedPower = true
            if (isPause) {
                this.delay()
            } else {
                delay(SHORT_PAUSE_TIME)
            }
            return this
        }
        return null
    }

    /**
     * 安全的使用卡牌
     * @return 为null表示执行失败
     */
    fun safePower(isPause: Boolean = true): CardAction? {
        val card = belongCard ?: return null
        if (card.area.player.usableResource >= card.cost) {
            if (card.area::class.java === HandArea::class.java) {
                return power(isPause)
            } else if (card.canPower()) {
                return power(isPause)
            }
        }
        return null
    }

    /**
     * 使用卡牌至指定card
     * @return 为null表示执行失败
     */
    fun power(
        card: Card?,
        isPause: Boolean = true,
    ): CardAction? {
        if (isStop()) return null
        return card?.let {
            val result = execPower(it)
            if (result) {
                executedPower = true
                if (isPause) {
                    this.delay()
                } else {
                    delay(SHORT_PAUSE_TIME)
                }
                return this
            } else {
                return null
            }
        }
    }

    /**
     * 使用卡牌至指定下标
     * @return 为null表示执行失败
     */
    fun power(
        index: Int,
        isPause: Boolean = true,
    ): CardAction? {
        if (isStop()) return null
        val result = execPower(index)
        if (result) {
            executedPower = true
            if (isPause) {
                this.delay()
            } else {
                delay(SHORT_PAUSE_TIME)
            }
            return this
        }
        return null
    }

    /**
     * 攻击指定card
     * @return 为null表示执行失败
     */
    fun attack(
        card: Card?,
        isPause: Boolean = true,
    ): CardAction? {
        if (isStop()) return null
        return card?.let {
            val result = execAttack(it)
            if (result) {
                if (isPause) {
                    if (card.cardType === CardTypeEnum.HERO) {
                        this.delay(SHORT_PAUSE_TIME)
                    } else {
                        this.delay()
                    }
                } else {
                    delay(SHORT_PAUSE_TIME)
                }
                return this
            }
            return null
        }
    }

    /**
     * 攻击敌方英雄
     * @return 为null表示执行失败
     */
    fun attackHero(isPause: Boolean = true): CardAction? {
        if (isStop()) return null
        val result = execAttackHero()
        if (result) {
            if (isPause) {
                this.delay()
            } else {
                delay(SHORT_PAUSE_TIME)
            }
            return this
        }
        return null
    }

    /**
     * 将鼠标移向指定card
     * @return 为null表示执行失败
     * @param click 是否左击
     */
    fun pointTo(
        card: Card?,
        click: Boolean = true,
        isPause: Boolean = true,
    ): CardAction? {
        if (isStop()) return null
        return card?.let {
            val result = execPointTo(card, click)
            if (result) {
                if (isPause) {
                    this.delay()
                } else {
                    delay(SHORT_PAUSE_TIME)
                }
                return this
            }
            return null
        }
    }

    /**
     * 将鼠标移向我方战场指定下标（优先使用此方法代替pointTo(card: Card?, isPause: Boolean = true)）
     * @param click 是否左击
     * @return 为null表示执行失败
     */
    fun pointTo(
        index: Int,
        click: Boolean = true,
        isPause: Boolean = true,
    ): CardAction? {
        if (isStop() || index == -1) return null
        val result = execPointTo(index, click)
        if (result) {
            if (isPause) {
                this.delay()
            } else {
                delay(SHORT_PAUSE_TIME)
            }
            return this
        }
        return null
    }

    /**
     * 左键点击
     * @return 为null表示执行失败
     */
    fun lClick(isPause: Boolean = false): CardAction? {
        if (isStop()) return null
        if (execLClick()) {
            if (isPause) {
                this.delay()
            } else {
                delay(SHORT_PAUSE_TIME)
            }
            return this
        }
        return null
    }

    /**
     * 发射星舰
     * @return 为null表示执行失败
     */
    fun launch(isPause: Boolean = false): CardAction? {
        if (isStop()) return null
        if (execLaunch()) {
            belongCard?.let {
                it.area.player.starshipAtc = 0
                it.area.player.starshipHealth = 0
            }
            if (isPause) {
                this.delay()
            } else {
                delay(SHORT_PAUSE_TIME)
            }
            return this
        }
        return null
    }

    /**
     * 交易
     */
    fun trade(isPause: Boolean = false): CardAction? {
        if (isStop()) return null
        if (execTrade()) {
            if (isPause) {
                this.delay()
            } else {
                delay(SHORT_PAUSE_TIME)
            }
            return this
        }
        return null
    }

    private fun delay(time: Int = mouseActionInterval) {
        if (isStop() || time <= 0) return
        if (time == mouseActionInterval) {
            depth = 0
        } else {
            depth++
        }
        Thread.sleep(time.toLong())
    }

    private fun isStop(): Boolean = Thread.currentThread().isInterrupted

    /**
     * 移除与[belongCard]相同的卡牌
     */
    fun removeSelf(war: War): Card? {
        val entityId = belongCard?.entityId ?: return null
        return war.cardMap[entityId]?.area?.removeByEntityId(entityId) ?: let {
            if (errorLogCount.incrementAndGet() <= MAX_ERROR_LOG_COUNT) {
                log.warn { "移除卡牌失败,entityId:$entityId,className:${this::class.qualifiedName},action:${this::class.qualifiedName}" }
            }
            null
        }
    }

    /**
     * 查找与[belongCard]相同的卡牌
     */
    fun findSelf(war: War): Card? {
        val entityId = belongCard?.entityId ?: return null
        return war.cardMap[entityId] ?: let {
            if (errorLogCount.incrementAndGet() <= MAX_ERROR_LOG_COUNT) {
                log.warn { "查找卡牌失败,entityId:$entityId,className:${this::class.qualifiedName},action:${this::class.qualifiedName}" }
            }
            null
        }
    }

    /**
     * 使用掉[belongCard]的费用
     */
    fun spendSelfCost(war: War): Card? {
        val entityId = belongCard?.entityId ?: return null
        return war.cardMap[entityId]?.let { card ->
            card.area.player.usedResources += card.cost
            card
        } ?: let {
            if (errorLogCount.incrementAndGet() <= MAX_ERROR_LOG_COUNT) {
                log.warn { "查找卡牌失败,entityId:$entityId,className:${this::class.qualifiedName},action:${this::class.qualifiedName}" }
            }
            null
        }
    }

    protected abstract fun execPower(): Boolean

    protected abstract fun execPower(card: Card): Boolean

    protected abstract fun execPower(index: Int): Boolean

    protected abstract fun execAttack(card: Card): Boolean

    protected abstract fun execAttackHero(): Boolean

    /**
     * 移向card，然后左击
     * @param click 是否左击
     */
    protected abstract fun execPointTo(
        card: Card,
        click: Boolean,
    ): Boolean

    /**
     * 移向我方战场指定下标处，然后左击（优先使用此方法代替execPointTo(card: Card)）
     */
    protected abstract fun execPointTo(
        index: Int,
        click: Boolean,
    ): Boolean

    /**
     * 左键点击
     */
    protected abstract fun execLClick(): Boolean

    /**
     * 执行发射星舰
     */
    protected abstract fun execLaunch(): Boolean

    /**
     * 执行交易
     */
    protected abstract fun execTrade(): Boolean

    abstract fun createNewInstance(): CardAction

    /**
     * 适配与cardId相匹配的card，支持通配符% 匹配任意个字符（为了性能考虑，每次返回的对象应该一样）
     */
    abstract fun getCardId(): Array<String>

    companion object {
        var mouseActionInterval: Int = 3600

        private const val SHORT_PAUSE_TIME = 150

        var commonActionFactory: Supplier<CardAction>? = null
    }

    abstract class DefaultCardAction : CardAction() {
        override fun execPower(): Boolean = commonAction?.execPower() == true

        override fun execPower(card: Card): Boolean = commonAction?.execPower(card) == true

        override fun execPower(index: Int): Boolean = commonAction?.execPower(index) == true

        override fun execAttack(card: Card): Boolean = commonAction?.execAttack(card) == true

        override fun execAttackHero(): Boolean = commonAction?.execAttackHero() == true

        override fun execPointTo(
            card: Card,
            click: Boolean,
        ): Boolean = commonAction?.execPointTo(card, click) == true

        override fun execPointTo(
            index: Int,
            click: Boolean,
        ): Boolean = commonAction?.execPointTo(index, click) == true

        override fun execLClick(): Boolean = commonAction?.execLClick() == true

        override fun execLaunch(): Boolean = commonAction?.execLClick() == true

        override fun execTrade(): Boolean = commonAction?.execTrade() == true
    }
}
