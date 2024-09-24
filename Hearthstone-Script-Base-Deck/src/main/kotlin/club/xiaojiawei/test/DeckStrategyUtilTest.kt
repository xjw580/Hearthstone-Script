package club.xiaojiawei.test

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.Card
import club.xiaojiawei.enums.CardTypeEnum
import club.xiaojiawei.status.War
import club.xiaojiawei.util.DeckStrategyUtil
import kotlin.math.cos

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/9/18 17:44
 */

class MyCardAction : CardAction() {
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

    override fun lClick(): Boolean {
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
//    test4()
    testTaunt()
    val me = War.player1
    if (me == null){
        return
    }
    val cards = me.handArea.cards.toList()
    println(cards)
    val toMutableList = cards.toMutableList()
    toMutableList.removeAll { card -> card.cardType != CardTypeEnum.MINION || card.isBattlecry }
//    val (num, resultCards) = findClosestSum(toMutableList, 1)
    println(toMutableList)
    val (num, resultCards) = DeckStrategyUtil.calcPowerOrder(toMutableList, 0)
    println(resultCards)
    println(num)

//    DeckStrategyUtil.cleanPlay()
//    DeckStrategyUtil.cleanTaunt()
}

fun findClosestSum(numbers: List<Card>, target: Int): Pair<Int, List<Card>> {
    var closest = 0
    val bestCombination = mutableListOf<Card>()

    fun backtrack(start: Int, currentSum: Int, currentCombination: MutableList<Card>) {
        if (currentSum > target) return // 超过目标，不再继续

        if (currentSum > closest) {
            closest = currentSum // 更新最接近的和
            bestCombination.clear()
            bestCombination.addAll(currentCombination) // 更新最佳组合
        }

        for (i in start until numbers.size) {
            currentCombination.add(numbers[i]) // 选择当前数
            backtrack(i + 1, currentSum + numbers[i].cost, currentCombination) // 递归
            currentCombination.removeAt(currentCombination.size - 1) // 回溯，移除当前数
        }
    }

    backtrack(0, 0, mutableListOf())
    return Pair(closest, bestCombination)
}

fun test1() {
    War.me?.let {
        var card = Card(MyCardAction())
        card.apply {
            entityName = "m1"
            entityId = "m1"
            atc = 3
            health = 5
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)

        val card1 = Card(MyCardAction())
        card1.apply {
            entityName = "m2"
            entityId = "m2"
            atc = 6
            health = 3
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card1)

        val card2 = Card(MyCardAction())
        card2.apply {
            entityName = "m3"
            entityId = "m3"
            atc = 2
            health = 10
//            isWindFury = true
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card2)

        card = Card(MyCardAction())
        card.apply {
            entityName = "m4"
            entityId = "m4"
            atc = 3
            health = 2
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)

        card = Card(MyCardAction())
        card.apply {
            entityName = "m5"
            entityId = "m5"
            atc = 4
            health = 4
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)

        card = Card(MyCardAction())
        card.apply {
            entityName = "m6"
            entityId = "m6"
            atc = 5
            health = 2
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)

        card = Card(MyCardAction())
        card.apply {
            entityName = "m7"
            entityId = "m7"
            atc = 2
            health = 1
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)

        card = Card(MyCardAction())
        card.apply {
            entityName = "mHero"
            entityId = "mHero"
            atc = 0
            health = 30
            cardType = CardTypeEnum.HERO
        }
        it.playArea.add(card)
    }

    War.rival?.let {
        var card = Card(MyCardAction())
        card.apply {
            entityName = "r1"
            entityId = "r1"
            atc = 3
            health = 5
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)

        card = Card(MyCardAction())
        card.apply {
            entityName = "r2"
            entityId = "r2"
            atc = 3
            health = 3
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)

        card = Card(MyCardAction())
        card.apply {
            entityName = "r3"
            entityId = "r3"
            atc = 1
            health = 2
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)

        card = Card(MyCardAction())
        card.apply {
            entityName = "r4"
            entityId = "r4"
            atc = 5
            health = 20
            isTaunt = true
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)

        card = Card(MyCardAction())
        card.apply {
            entityName = "r5"
            entityId = "r5"
            atc = 5
            health = 10
            cardType = CardTypeEnum.MINION
        }
//        it.playArea.add(card)

        card = Card(MyCardAction())
        card.apply {
            entityName = "r6"
            entityId = "r6"
            atc = 8
            health = 5
            cardType = CardTypeEnum.MINION
        }
//        it.playArea.add(card)

        card = Card(MyCardAction())
        card.apply {
            entityName = "r7"
            entityId = "r7"
            atc = 3
            health = 5
            cardType = CardTypeEnum.MINION
        }
//        it.playArea.add(card)

        card = Card(MyCardAction())
        card.apply {
            entityName = "rHero"
            entityId = "rHero"
            atc = 0
            health = 30
            cardType = CardTypeEnum.HERO
        }
        it.playArea.add(card)
    }
}

fun test2() {
    War.me?.let {
        val card = Card(MyCardAction())
        card.apply {
            entityName = "m1"
            entityId = "m1"
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
            entityId = "r1"
            atc = 5
            health = 5
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)
    }
}

fun test3() {
    War.me?.let {
        var card = Card(MyCardAction())
        card.apply {
            entityName = "m1"
            entityId = "m1"
            atc = 7
            health = 6
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)

        card = Card(MyCardAction())
        card.apply {
            entityName = "m2"
            entityId = "m2"
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
            entityId = "r1"
            atc = 5
            health = 7
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)

        card = Card(MyCardAction())
        card.apply {
            entityName = "r2"
            entityId = "r2"
            atc = 1
            health = 1
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)

        card = Card(MyCardAction())
        card.apply {
            entityName = "r4"
            entityId = "r4"
            isTaunt = true
            atc = 8
            health = 9
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)
    }
}

fun test4() {
    War.me?.let {
        var card = Card(MyCardAction())
        card.apply {
            entityName = "m1"
            entityId = "m1"
            atc = 7
            health = 7
            isDivineShield = true
            isWindFury = true
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)
//
//        card = Card(MyCardAction())
//        card.apply {
//            entityName = "mHero"
//            atc = 0
//            health = 30
////            isDivineShield = true
//            isWindFury = true
//            cardType = CardTypeEnum.HERO
//        }
//        it.playArea.add(card)
    }

    War.rival?.let {
        var card = Card(MyCardAction())
        card.apply {
            entityName = "r1"
            entityId = "r1"
            atc = 6
            health = 6
            isTaunt = true
//            isDeathRattle = true
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)

        card = Card(MyCardAction())
        card.apply {
            entityName = "r2"
            entityId = "r2"
            atc = 1
            health = 1
            cardType = CardTypeEnum.MINION
        }
//        it.playArea.add(card)

        card = Card(MyCardAction())
        card.apply {
            entityName = "r3"
            entityId = "r3"
            atc = 6
            health = 6
            isTaunt = true
//            isDeathRattle = true
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)
//
//        card = Card(MyCardAction())
//        card.apply {
//            entityName = "r4"
//            atc = 6
//            health = 7
//            cardType = CardTypeEnum.MINION
//        }
//        it.playArea.add(card)
//
        card = Card(MyCardAction())
        card.apply {
            entityName = "rHero"
            entityId = "rHero"
            atc = 0
            health = 10
            cardType = CardTypeEnum.HERO
        }
        it.playArea.add(card)
    }
}

fun testTaunt() {
    War.me?.let {
        var card = Card(MyCardAction())
        card.apply {
            entityName = "m1"
            entityId = "m1"
            atc = 5
            health = 5
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)

        card = Card(MyCardAction())
        card.apply {
            entityName = "m2"
            entityId = "m2"
            atc = 5
            health = 5
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)

        card = Card(MyCardAction())
        card.apply {
            entityName = "m3"
            entityId = "m3"
            atc = 5
            health = 5
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)

        card = Card(MyCardAction())
        card.apply {
            entityName = "m4"
            entityId = "m3"
            atc = 5
            health = 5
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)

        card = Card(MyCardAction())
        card.apply {
            entityName = "m2"
            entityId = "m2"
            atc = 5
            health = 5
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)

        card = Card(MyCardAction())
        card.apply {
            entityName = "m3"
            entityId = "m3"
            cost = 0
            atc = 5
            health = 5
            cardType = CardTypeEnum.MINION
        }
        it.handArea.add(card)

        card = Card(MyCardAction())
        card.apply {
            entityName = "m4"
            entityId = "m3"
            atc = 5
            health = 5
            cost = 0
            cardType = CardTypeEnum.MINION
        }
        it.handArea.add(card)

        card = Card(MyCardAction())
        card.apply {
            entityName = "mHero"
            entityId = "mHero"
            atc = 1
            health = 30
            cardType = CardTypeEnum.HERO
        }
        it.handArea.add(card)
    }

    War.rival?.let {
        var card = Card(MyCardAction())
        card.apply {
            entityName = "r1"
            entityId = "r1"
            atc = 5
            health = 10
            cardType = CardTypeEnum.MINION
        }
        it.handArea.add(card)

        card = Card(MyCardAction())
        card.apply {
            entityName = "r2"
            entityId = "r1"
            atc = 5
            health = 10
            cardType = CardTypeEnum.MINION
        }
        it.handArea.add(card)

        card = Card(MyCardAction())
        card.apply {
            entityName = "r3"
            entityId = "r1"
            atc = 5
            health = 10
            cardType = CardTypeEnum.MINION
        }
        it.handArea.add(card)

        card = Card(MyCardAction())
        card.apply {
            entityName = "r4"
            entityId = "r1"
            atc = 5
            health = 10
            cardType = CardTypeEnum.MINION
        }
        it.handArea.add(card)

        card = Card(MyCardAction())
        card.apply {
            entityName = "r5"
            entityId = "r1"
            atc = 5
            health = 10
            cardType = CardTypeEnum.MINION
        }
        it.handArea.add(card)

        card = Card(MyCardAction())
        card.apply {
            entityName = "r6"
            entityId = "r1"
            atc = 5
            health = 10
            cardType = CardTypeEnum.MINION
        }
        it.handArea.add(card)

        card = Card(MyCardAction())
        card.apply {
            entityName = "r7"
            entityId = "r1"
            atc = 5
            health = 10
            cardType = CardTypeEnum.MINION
        }
        it.handArea.add(card)

        card = Card(MyCardAction())
        card.apply {
            entityName = "rHero"
            entityId = "rHero"
            atc = 1
            health = 30
            cardType = CardTypeEnum.HERO
        }
        it.handArea.add(card)
    }
}