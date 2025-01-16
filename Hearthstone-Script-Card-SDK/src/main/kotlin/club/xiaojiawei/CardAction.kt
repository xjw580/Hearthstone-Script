package club.xiaojiawei

import club.xiaojiawei.bean.Action
import club.xiaojiawei.bean.AttackAction
import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.PlayAction
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
     * belongCard被打出或使用的所有可能动作
     * 例如：belongCard为老红龙，那么此时可以返回两个action，一个action为指定敌方英雄，一个action为指定己方英雄
     * 例如：belongCard为萨满基础技能时，那么此时至多可以返回一个action，即在战场未满的情况下生成图腾至战场这个action
     * @return 返回null表示
     */
    open fun generatePlayActions(war: War): List<Action> {
        return belongCard?.let { card ->
            val entityId = card.entityId
//            由于不知道技能和法术的效果，所有不生成action
            if (war.me.playArea.power == card || card.cardType === CardTypeEnum.SPELL) {
                return emptyList()
            }
            listOf(
                PlayAction({ war ->
                    war.me.handArea.findByEntityId(entityId)?.let { card ->
                        log.info { "打出$card" }
                        card.action.power()
                    } ?: let {
                        log.warn { "查询手中卡牌失败,entityId:${entityId}" }
                    }
                }, { war ->
                    val me = war.me
                    me.handArea.removeByEntityId(entityId)?.let { card ->
                        card.isExhausted = true
                        me.playArea.add(card).isFalse {
                            log.warn { "添加战场卡牌失败" }
                        }
                        me.resourcesUsed += card.cost
                    } ?: let {
                        log.warn { "移除手中卡牌失败,entityId:${entityId}" }
                    }
                })
            )
        } ?: emptyList()
    }

    /**
     * belongCard攻击的所有可能动作
     * 例如：敌方战场有2个可被攻击的非嘲讽随从，此时可以返回三个action，即攻击两个随从的action和攻击敌方英雄的action
     */
    open fun generateAttackActions(war: War): List<Action> {
        return belongCard?.let { card ->
            val entityId = card.entityId
            val result = mutableListOf<Action>()
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
                                    log.warn { "查找敌方战场卡牌失败" }
                                }
                            } ?: let {
                                log.warn { "查找战场卡牌失败,entityId:${entityId}" }
                            }
                        }, { war ->
                            war.me.playArea.findByEntityId(entityId)?.let { myCard ->
                                war.rival.playArea.findByEntityId(rivalPlayCard.entityId)?.let { rivalCard ->
                                    CardUtil.simulateAttack(myCard, rivalCard, true)
                                }
                            } ?: let {
                                log.warn { "查找战场卡牌失败,entityId:${entityId}" }
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
                                        CardUtil.simulateAttack(myCard, rivalHero, true)
                                    } ?: let {
                                        log.warn { "查找战场卡牌失败" }
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
     * 使用卡牌
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

    fun delay(time: Int = mouseActionInterval) {
        if (isStop()) return
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
     */
    protected abstract fun execPointTo(card: Card, click: Boolean): Boolean

    /**
     * 移向我方战场指定下标处，然后左击（优先使用此方法代替execPointTo(card: Card)）
     */
    protected abstract fun execPointTo(index: Int, click: Boolean): Boolean

    abstract fun createNewInstance(): CardAction

    /**
     * 左键点击
     */
    abstract fun lClick(): Boolean

    /**
     * 适配与cardId相匹配的card，支持通配符% 匹配任意个字符
     */
    abstract fun getCardId(): Array<String>

    companion object {
        var mouseActionInterval: Int = 3500

        private const val SHORT_PAUSE_TIME = 200

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

        override fun lClick(): Boolean {
            return commonAction?.lClick() == true
        }
    }

}
