package club.xiaojiawei

import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.Player
import club.xiaojiawei.enums.CardTypeEnum
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

    fun createPlayActions(my: Player, rival: Player): List<Action> {
        return emptyList()
    }

    fun createAttackActions(my: Player, rival: Player): List<Action> {
        return emptyList()
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
