package club.xiaojiawei

import club.xiaojiawei.bean.Card
import club.xiaojiawei.status.War

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/9/18 17:44
 */
fun main() {

    class MyCardAction: CardAction() {
        override fun getCardId(): String {
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

        override fun execPointTo(card: Card): Boolean {
            TODO("Not yet implemented")
        }

        override fun createNewInstance(): CardAction {
            TODO("Not yet implemented")
        }

    }
    War.reset()
    War.me = War.player1
    War.rival = War.player2

    War.me?.let {
        for (i in 0 until 5) {
            val card = Card(MyCardAction())
            card.apply {
                entityId = "0"
                atc = i + 3
                health = i * 2
            }
            it.playArea.add(card)
        }
    }

    War.rival?.let {
        for (i in 0 until 3) {
            val card = Card(MyCardAction())
            card.apply {
                entityId = "0"
                atc = i + 4
                health = i * 2 + 1
            }
            it.playArea.add(card)
        }
    }

    DeckStrategyUtil.cleanNormal()
}