package club.xiaojiawei

import club.xiaojiawei.bean.*
import club.xiaojiawei.config.log
import club.xiaojiawei.enums.CardTypeEnum
import club.xiaojiawei.status.War
import club.xiaojiawei.util.CardUtil
import club.xiaojiawei.util.isFalse
import club.xiaojiawei.util.isTrue
import java.util.function.Supplier

/**
 * @author 肖嘉威
 * @date 2024/9/8 18:42
 */
abstract class CardAction(createDefaultAction: Boolean = true) {

    protected var depth = 0

    /**
     * 是否尝试打出过
     */
    var executedPower = false

    protected var commonAction: CardAction? = null

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
     * belongCard被使用的所有可能动作
     * 例如：belongCard为萨满基础技能时，那么此时至多可以返回一个action，即在战场未满的情况下生成图腾至战场这个action
     * 对于战场上的技能，地标使用都是调用此方法生成动作
     * @param war 此卡牌所处的战局
     * @param player 此卡牌所处的玩家
     */
    open fun generatePowerActions(war: War, player: Player): List<PowerAction> {
        return emptyList()
    }

    /**
     * belongCard从手牌中打出的所有可能动作（包含战吼效果）
     * 例如：belongCard为老红龙：血变成15，那么此时可以返回两个action，一个action作用于敌方英雄，一个action作用于己方英雄
     * @param war 此卡牌所处的战局
     * @param player 此卡牌所处的玩家
     */
    open fun generatePlayActions(war: War, player: Player): List<PlayAction> {
        return belongCard?.let { card ->
            val entityId = card.entityId
            if (entityId.isBlank()) {
                log.warn { "entityId为空，belongCard：${belongCard}" }
                return emptyList()
            }
//            由于不知道法术的效果，所有不生成action
            if (card.cardType === CardTypeEnum.SPELL) {
                return emptyList()
            }
            listOf(
                PlayAction({ war ->
                    war.me.handArea.findByEntityId(entityId)?.let { card ->
                        log.info { "打出$card" }
                        card.action.power()
                    } ?: let {
                        log.warn { "PlayAction查询手中卡牌失败,entityId:${entityId}" }
                    }
                }, { war ->
                    val me = war.me
                    me.handArea.removeByEntityId(entityId)?.let { card ->
                        card.isExhausted = true
                        me.playArea.add(card).isFalse {
                            log.warn { "PlayAction添加战场卡牌失败,entityId:${entityId}" }
                        }
                        me.resourcesUsed += card.cost
                    } ?: let {
                        log.warn { "PlayAction移除手中卡牌失败,entityId:${entityId}" }
                    }
                })
            )
        } ?: emptyList()
    }

    /**
     * belongCard攻击的所有可能动作
     * 例如：敌方战场有2个可被攻击的非嘲讽随从，此时可以返回三个action，即攻击两个随从的action和攻击敌方英雄的action
     * @param war 此卡牌所处的战局
     * @param player 此卡牌所处的玩家
     */
    open fun generateAttackActions(war: War, player: Player): List<AttackAction> {
        return belongCard?.let { card ->
            val entityId = card.entityId
            if (entityId.isBlank()) {
                log.warn { "entityId为空，belongCard：${belongCard}" }
                return emptyList()
            }
            val result = mutableListOf<AttackAction>()
            val tauntCard = CardUtil.getTauntCards(war.rival.playArea.cards, true)
            val rivalPlayCards = if (tauntCard.isEmpty()) war.rival.playArea.cards else tauntCard
            for (rivalPlayCard in rivalPlayCards) {
                if (rivalPlayCard.canBeAttacked()) {
                    result.add(
                        AttackAction({ war ->
                            war.me.playArea.findByEntityId(entityId)?.let { myCard ->
                                war.rival.playArea.findByEntityId(rivalPlayCard.entityId)?.let { rivalCard ->
                                    log.info { "${myCard}攻击${rivalCard}" }
                                    myCard.action.attack(rivalCard)
                                } ?: let {
                                    log.warn { "AttackAction查找敌方战场卡牌失败,entityId:${entityId}" }
                                }
                            } ?: let {
                                log.warn { "AttackAction查找战场卡牌失败,entityId:${entityId}" }
                            }
                        }, { war ->
                            war.me.playArea.findByEntityId(entityId)?.let { myCard ->
                                war.rival.playArea.findByEntityId(rivalPlayCard.entityId)?.let { rivalCard ->
                                    CardUtil.simulateAttack(war, myCard, rivalCard, true)
                                }
                            } ?: let {
                                log.warn { "AttackAction查找战场卡牌失败,entityId:${entityId}" }
                            }
                        })
                    )
                }
            }
            if (tauntCard.isEmpty()) {
                war.rival.playArea.hero?.let { rivalHero ->
                    rivalHero.canBeAttacked().isTrue {
                        result.add(
                            AttackAction({ war ->
                                war.me.playArea.findByEntityId(entityId)?.let { myCard ->
                                    log.info { "${myCard}攻击${war.rival.playArea.hero}" }
                                    myCard.action.attack(war.rival.playArea.hero)
                                } ?: let {
                                }
                            }, { war ->
                                war.rival.playArea.hero?.let { rivalHero ->
                                    war.me.playArea.findByEntityId(entityId)?.let { myCard ->
                                        CardUtil.simulateAttack(war, myCard, rivalHero, true)
                                    } ?: let {
                                        log.warn { "AttackAction查找战场卡牌失败,entityId:${entityId}" }
                                    }
                                }
                            })
                        )
                    }
                }
            }
            result
        } ?: emptyList()
    }

    /**
     * 亡语结算
     * @param war 此卡牌所处的战局
     * @param player 此卡牌所处的玩家
     */
    open fun deathRattleSettlement(war: War, player: Player) {}

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
     * 使用卡牌至指定card
     * @return 为null表示执行失败
     */
    fun power(card: Card?, isPause: Boolean = true): CardAction? {
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
    fun power(index: Int, isPause: Boolean = true): CardAction? {
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
    fun attack(card: Card?, isPause: Boolean = true): CardAction? {
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
    fun pointTo(card: Card?, click: Boolean = true, isPause: Boolean = true): CardAction? {
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
    fun pointTo(index: Int, click: Boolean = true, isPause: Boolean = true): CardAction? {
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

    fun delay(time: Int = mouseActionInterval) {
        if (isStop() || time < 0) return
        if (time == mouseActionInterval) {
            depth = 0
        } else {
            depth++
        }
        Thread.sleep(time.toLong())
    }

    private fun isStop(): Boolean {
        return Thread.currentThread().isInterrupted
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
    protected abstract fun execPointTo(card: Card, click: Boolean): Boolean

    /**
     * 移向我方战场指定下标处，然后左击（优先使用此方法代替execPointTo(card: Card)）
     */
    protected abstract fun execPointTo(index: Int, click: Boolean): Boolean

    /**
     * 左键点击
     */
    protected abstract fun execLClick(): Boolean

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

        override fun execPower(): Boolean {
            return commonAction?.execPower() == true
        }

        override fun execPower(card: Card): Boolean {
            return commonAction?.execPower(card) == true
        }

        override fun execPower(index: Int): Boolean {
            return commonAction?.execPower(index) == true
        }

        override fun execAttack(card: Card): Boolean {
            return commonAction?.execAttack(card) == true
        }

        override fun execAttackHero(): Boolean {
            return commonAction?.execAttackHero() == true
        }

        override fun execPointTo(card: Card, click: Boolean): Boolean {
            return commonAction?.execPointTo(card, click) == true
        }

        override fun execPointTo(index: Int, click: Boolean): Boolean {
            return commonAction?.execPointTo(index, click) == true
        }

        override fun execLClick(): Boolean {
            return commonAction?.execLClick() == true
        }
    }

}
