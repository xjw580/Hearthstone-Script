package club.xiaojiawei.bean

import club.xiaojiawei.CardAction

/**
 * @author 肖嘉威
 * @date 2025/1/13 18:21
 */

val TEST_CARD_ACTION by lazy { TestCardAction() }

class TestCardAction : CardAction() {

    override fun getCardId(): Array<String> {
        return emptyArray()
    }

    override fun execPower(): Boolean {
        return true
    }

    override fun execPower(card: Card): Boolean {
        return true
    }

    override fun execPower(index: Int): Boolean {
        return true
    }

    override fun execAttack(card: Card): Boolean {
        return true
    }

    override fun execAttackHero(): Boolean {
        return true
    }

    override fun execPointTo(card: Card, click: Boolean): Boolean {
        return true
    }

    override fun execPointTo(index: Int, click: Boolean): Boolean {
        return true
    }

    override fun createNewInstance(): CardAction {
        return this
    }

    override fun execLClick(): Boolean {
        return true
    }

    override fun execLaunch(): Boolean {
        return true
    }

    override fun execTrade(): Boolean {
        return true
    }

    override fun execForge(): Boolean {
        return true
    }
}