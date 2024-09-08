package club.xiaojiawei

import club.xiaojiawei.bean.Card

/**
 * @author 肖嘉威
 * @date 2024/9/8 18:42
 */
abstract class CardAction {

    companion object {
        var mouseActionInterval: Int = 3500
    }

    var belongCard: Card? = null

    abstract fun getCardId(): String

    protected fun delay(time: Int = mouseActionInterval) {
        try {
            Thread.sleep(time.toLong())
        } catch (e: InterruptedException) {
            log.warn(e) {}
        }
    }

    fun power(): Boolean {
        val result = execPower()
        delay()
        return result
    }

    fun power(card: Card?): Boolean {
        return card?.let {
            val result = execPower(it)
            delay()
            return result
        }?:false
    }

    fun power(index: Int): Boolean {
        val result = execPower(index)
        delay()
        return result
    }

    fun attackMinion(card: Card?): Boolean {
        return card?.let {
            val result = execAttackMinion(it)
            delay()
            result
        }?:false
    }

    fun attackHero(): Boolean {
        val result = execAttackHero()
        delay()
        return result
    }

    fun pointTo(card: Card?): Boolean {
        return card?.let {
            val result = execPointTo(card)
            delay(mouseActionInterval shr 1)
            result
        }?: false
    }


    protected abstract fun execPower(): Boolean

    protected abstract fun execPower(card: Card): Boolean

    protected abstract fun execPower(index: Int): Boolean

    protected abstract fun execAttackMinion(card: Card): Boolean

    protected abstract fun execAttackHero(): Boolean

    protected abstract fun execPointTo(card: Card): Boolean

    abstract fun createNewInstance(): CardAction
}