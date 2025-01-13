package club.xiaojiawei.test

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.Card

/**
 * @author 肖嘉威
 * @date 2025/1/13 18:21
 */
class TestCardAction : CardAction() {

    override fun getCardId(): Array<String> {
        TODO("Not yet implemented")
    }

    override fun execPower(): Boolean {
        TODO("Not yet implemented")
    }

    override fun execPower(card: Card): Boolean {
        TODO("Not yet implemented")
    }

    override fun execPower(index: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun execAttack(card: Card): Boolean {
        TODO("Not yet implemented")
    }

    override fun execAttackHero(): Boolean {
        TODO("Not yet implemented")
    }

    override fun execPointTo(card: Card, click: Boolean): Boolean {
        TODO("Not yet implemented")
    }

    override fun execPointTo(index: Int, click: Boolean): Boolean {
        TODO("Not yet implemented")
    }

    override fun createNewInstance(): CardAction {
        TODO("Not yet implemented")
    }

    override fun lClick(): Boolean {
        TODO("Not yet implemented")
    }
}