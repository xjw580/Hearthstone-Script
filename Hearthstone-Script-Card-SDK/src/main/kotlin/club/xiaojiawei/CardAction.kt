package club.xiaojiawei

import club.xiaojiawei.bean.Card
import club.xiaojiawei.enums.CardTypeEnum
import java.util.function.Supplier

/**
 * @author 肖嘉威
 * @date 2024/9/8 18:42
 */
abstract class CardAction(createDefaultAction: Boolean = true) {

    var commonAction: CardAction? = null

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

    fun power(isPause: Boolean = true): CardAction? {
        if (isStop()) return null
        val result = execPower()
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

    fun power(card: Card?, isPause: Boolean = true): CardAction? {
        if (isStop()) return null
        return card?.let {
            val result = execPower(it)
            if (result) {
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

    fun power(index: Int, isPause: Boolean = true): CardAction? {
        if (isStop()) return null
        val result = execPower(index)
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

    fun attack(card: Card?, isPause: Boolean = true): CardAction? {
        if (isStop()) return null
        return card?.let {
            val result = execAttack(it)
            if (result) {
                if (isPause) {
                    if (card.cardType === CardTypeEnum.HERO){
                        this.delay((mouseActionInterval - 1000).coerceAtLeast(SHORT_PAUSE_TIME))
                    }else{
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

    fun pointTo(card: Card?, isPause: Boolean = true): CardAction? {
        if (isStop()) return null
        return card?.let {
            val result = execPointTo(card)
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

    fun delay(time: Int = mouseActionInterval) {
        if (isStop()) return
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
    protected abstract fun execPointTo(card: Card): Boolean

    abstract fun createNewInstance(): CardAction

    abstract fun lClick(): Boolean

    abstract fun getCardId(): String

    companion object {
        var mouseActionInterval: Int = 3500

        private const val SHORT_PAUSE_TIME = 200

        var commonActionFactory: Supplier<CardAction>? = null
    }

    abstract class DefaultCardAction : CardAction() {

        override fun execPower(): Boolean {
            return commonAction?.execPower() ?: false
        }

        override fun execPower(card: Card): Boolean {
            return commonAction?.execPower(card) ?: false
        }

        override fun execPower(index: Int): Boolean {
            return commonAction?.execPower(index) ?: false
        }

        override fun execAttack(card: Card): Boolean {
            return commonAction?.execAttack(card) ?: false
        }

        override fun execAttackHero(): Boolean {
            return commonAction?.execAttackHero() ?: false
        }

        override fun execPointTo(card: Card): Boolean {
            return commonAction?.execPointTo(card) ?: false
        }

        override fun lClick(): Boolean {
            return commonAction?.lClick() ?: false
        }
    }

}
