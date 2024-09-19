package club.xiaojiawei.test

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.Card
import club.xiaojiawei.enums.CardTypeEnum
import club.xiaojiawei.status.War
import club.xiaojiawei.util.DeckStrategyUtil

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/9/18 17:44
 */

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

fun main() {
    War.reset()
    War.me = War.player1
    War.rival = War.player2

//    test1()
//    test2()
//    test3()
    test4()

    DeckStrategyUtil.cleanNormal()
//    DeckStrategyUtil.cleanTaunt()
}

fun test1(){
    War.me?.let {
        var card = Card(MyCardAction())
        card.apply {
            entityName = "m1"
            atc = 3
            health = 5
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)

        val card1 = Card(MyCardAction())
        card1.apply {
            entityName = "m2"
            atc = 6
            health = 3
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card1)

        val card2 = Card(MyCardAction())
        card2.apply {
            entityName = "m3"
            atc = 2
            health = 10
            isWindFury = true
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card2)

        card = Card(MyCardAction())
        card.apply {
            entityName = "m4"
            atc = 3
            health = 2
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)

        card = Card(MyCardAction())
        card.apply {
            entityName = "m5"
            atc = 4
            health = 4
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)

        card = Card(MyCardAction())
        card.apply {
            entityName = "m6"
            atc = 5
            health = 2
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)

        card = Card(MyCardAction())
        card.apply {
            entityName = "m7"
            atc = 2
            health = 1
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)
    }

    War.rival?.let {
        var card = Card(MyCardAction())
        card.apply {
            entityName = "r1"
            atc = 3
            health = 5
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)

        card = Card(MyCardAction())
        card.apply {
            entityName = "r2"
            atc = 3
            health = 3
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)

        card = Card(MyCardAction())
        card.apply {
            entityName = "r3"
            atc = 1
            health = 2
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)

        card = Card(MyCardAction())
        card.apply {
            entityName = "r4"
            atc = 5
            health = 3
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)

        card = Card(MyCardAction())
        card.apply {
            entityName = "r5"
            atc = 4
            health = 2
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)

        card = Card(MyCardAction())
        card.apply {
            entityName = "r6"
            atc = 8
            health = 5
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)

        card = Card(MyCardAction())
        card.apply {
            entityName = "r7"
            atc = 3
            health = 5
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)
    }
}

fun test2(){
    War.me?.let {
        val card = Card(MyCardAction())
        card.apply {
            entityName = "m1"
            atc = 5
            health = 5
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)
    }

    War.rival?.let {
        var card = Card(MyCardAction())
        card.apply {
            entityName = "r1"
            atc = 5
            health = 5
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)
    }
}

fun test3(){
    War.me?.let {
        var card = Card(MyCardAction())
        card.apply {
            entityName = "m1"
            atc = 7
            health = 6
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)

        card = Card(MyCardAction())
        card.apply {
            entityName = "m2"
            atc = 2
            health = 2
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)
    }

    War.rival?.let {
        var card = Card(MyCardAction())
        card.apply {
            entityName = "r1"
            atc = 5
            health = 7
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)

        card = Card(MyCardAction())
        card.apply {
            entityName = "r2"
            atc = 1
            health = 1
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)

        card = Card(MyCardAction())
        card.apply {
            entityName = "r4"
            isTaunt = true
            atc = 8
            health = 9
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)
    }
}

fun test4(){
    War.me?.let {
        var card = Card(MyCardAction())
        card.apply {
            entityName = "m1"
            atc = 7
            health = 7
//            isDivineShield = true
            isWindFury = true
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)
    }

    War.rival?.let {
        var card = Card(MyCardAction())
        card.apply {
            entityName = "r1"
            atc = 4
            health = 5
            isTaunt = true
            isDeathRattle = true
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)

        card = Card(MyCardAction())
        card.apply {
            entityName = "r2"
            atc = 1
            health = 1
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)

        card = Card(MyCardAction())
        card.apply {
            entityName = "r4"
            atc = 6
            health = 7
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)
    }
}