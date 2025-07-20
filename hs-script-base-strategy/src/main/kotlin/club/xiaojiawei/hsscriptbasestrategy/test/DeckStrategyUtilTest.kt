package club.xiaojiawei.hsscriptbasestrategy.test

import club.xiaojiawei.bean.Card
import club.xiaojiawei.hsscriptbasestrategy.bean.SimulateWeightCard
import club.xiaojiawei.bean.TestCardAction
import club.xiaojiawei.enums.CardTypeEnum
import club.xiaojiawei.status.WAR
import club.xiaojiawei.hsscriptbasestrategy.util.DeckStrategyUtil.calcPowerOrder

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/9/18 17:44
 */

fun main() {
//    WAR.me = War.player1
//    WAR.rival = War.player2
//
////    test1()
//    test2()
////    test3()
////    test4()
////    testTaunt()
//    val me = War.player1
//    val cards = me.handArea.cards.toList()
////    println(cards)
//    val toMutableList = cards.toMutableList()
////    toMutableList.removeAll { card -> card.cardType != CardTypeEnum.MINION || card.isBattlecry }
////    val (num, resultCards) = findClosestSum(toMutableList, 1)
////    println(toMutableList)
////    val (num, resultCards) = DeckStrategyUtil.calcPowerOrder(toMutableList, 0)
////    println(resultCards)
////    println(num)
//
//    DeckStrategyUtil.cleanPlay()
////    DeckStrategyUtil.cleanTaunt()

    testPower()
}

fun testPower() {
    val card = Card(TestCardAction())
    card.cost = 2
    card.cardId = "1"

    val card1 = Card(TestCardAction())
    card1.cost = 1
    card1.cardId = "2"

    val card2 = Card(TestCardAction())
    card2.cost = 1
    card2.cardId = "3"

    val card3 = Card(TestCardAction())
    card3.cost = 5
    card3.cardId = "4"

    val card4 = Card(TestCardAction())
    card4.cost = 3
    card4.cardId = "5"

    val card5 = Card(TestCardAction())
    card5.cost = 3
    card5.cardId = "6"

    val card6 = Card(TestCardAction())
    card6.cost = 1
    card6.cardId = "7"

    val card7 = Card(TestCardAction())
    card7.cost = 3
    card7.cardId = "8"

    val card8 = Card(TestCardAction())
    card8.cost = 1
    card8.cardId = "9"

    val card9 = Card(TestCardAction())
    card9.cost = 5
    card9.cardId = "10"

    val mutableListOf = mutableListOf<Card>(card, card1, card2, card3, card4, card5, card6, card7, card8, card9)
    val mutableListOf1 = mutableListOf<SimulateWeightCard>()
    for (item in mutableListOf) {
        mutableListOf1.add(SimulateWeightCard(item, 1.0))
    }
//    mutableListOf1[0].weight = 1.0
//    mutableListOf1[1].weight = 1.9
//    mutableListOf1[2].weight = 1.5
//    mutableListOf1[3].weight = 2.0
//    mutableListOf1[7].weight = 2.0
    mutableListOf1[9].weight = 10.0
    val (first, second) = calcPowerOrder(mutableListOf1, 6)
    println(first)
    for (weightCard in second) {
        println("${weightCard.card},${weightCard.card.cost}")
    }
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
    WAR.me?.let {
        var card = Card(TestCardAction())
        card.apply {
            entityName = "m1"
            entityId = "m1"
            atc = 3
            health = 5
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)

        val card1 = Card(TestCardAction())
        card1.apply {
            entityName = "m2"
            entityId = "m2"
            atc = 6
            health = 3
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card1)

        val card2 = Card(TestCardAction())
        card2.apply {
            entityName = "m3"
            entityId = "m3"
            atc = 2
            health = 10
//            isWindFury = true
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card2)

        card = Card(TestCardAction())
        card.apply {
            entityName = "m4"
            entityId = "m4"
            atc = 3
            health = 2
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)

        card = Card(TestCardAction())
        card.apply {
            entityName = "m5"
            entityId = "m5"
            atc = 4
            health = 4
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)

        card = Card(TestCardAction())
        card.apply {
            entityName = "m6"
            entityId = "m6"
            atc = 5
            health = 2
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)

        card = Card(TestCardAction())
        card.apply {
            entityName = "m7"
            entityId = "m7"
            atc = 2
            health = 1
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)

        card = Card(TestCardAction())
        card.apply {
            entityName = "mHero"
            entityId = "mHero"
            atc = 0
            health = 30
            cardType = CardTypeEnum.HERO
        }
        it.playArea.add(card)
    }

    WAR.rival?.let {
        var card = Card(TestCardAction())
        card.apply {
            entityName = "r1"
            entityId = "r1"
            atc = 3
            health = 5
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)

        card = Card(TestCardAction())
        card.apply {
            entityName = "r2"
            entityId = "r2"
            atc = 3
            health = 3
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)

        card = Card(TestCardAction())
        card.apply {
            entityName = "r3"
            entityId = "r3"
            atc = 1
            health = 2
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)

        card = Card(TestCardAction())
        card.apply {
            entityName = "r4"
            entityId = "r4"
            atc = 5
            health = 20
            isTaunt = true
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)

        card = Card(TestCardAction())
        card.apply {
            entityName = "r5"
            entityId = "r5"
            atc = 5
            health = 10
            cardType = CardTypeEnum.MINION
        }
//        it.playArea.add(card)

        card = Card(TestCardAction())
        card.apply {
            entityName = "r6"
            entityId = "r6"
            atc = 8
            health = 5
            cardType = CardTypeEnum.MINION
        }
//        it.playArea.add(card)

        card = Card(TestCardAction())
        card.apply {
            entityName = "r7"
            entityId = "r7"
            atc = 3
            health = 5
            cardType = CardTypeEnum.MINION
        }
//        it.playArea.add(card)

        card = Card(TestCardAction())
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
    WAR.me?.let {
        var card = Card(TestCardAction())
        card.apply {
            entityName = "m1"
            entityId = "m1"
            atc = 4
            health = 2
            cardType = CardTypeEnum.MINION
            isAttackableByRush = true
        }
        it.playArea.add(card)
        card = Card(TestCardAction())
        card.apply {
            entityName = "m2"
            entityId = "m2"
            atc = 2
            health = 2
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)
    }

    WAR.rival?.let {
        var card = Card(TestCardAction())
        card.apply {
            entityName = "r1"
            entityId = "r1"
            atc = 0
            health = 31
            cardType = CardTypeEnum.HERO
        }
        it.playArea.add(card)
    }
}

fun test3() {
    WAR.me?.let {
        var card = Card(TestCardAction())
        card.apply {
            entityName = "m1"
            entityId = "m1"
            atc = 7
            health = 6
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)

        card = Card(TestCardAction())
        card.apply {
            entityName = "m2"
            entityId = "m2"
            atc = 2
            health = 2
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)
    }

    WAR.rival?.let {
        var card = Card(TestCardAction())
        card.apply {
            entityName = "r1"
            entityId = "r1"
            atc = 5
            health = 7
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)

        card = Card(TestCardAction())
        card.apply {
            entityName = "r2"
            entityId = "r2"
            atc = 1
            health = 1
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)

        card = Card(TestCardAction())
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
    WAR.me?.let {
        var card = Card(TestCardAction())
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

    WAR.rival?.let {
        var card = Card(TestCardAction())
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

        card = Card(TestCardAction())
        card.apply {
            entityName = "r2"
            entityId = "r2"
            atc = 1
            health = 1
            cardType = CardTypeEnum.MINION
        }
//        it.playArea.add(card)

        card = Card(TestCardAction())
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
        card = Card(TestCardAction())
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
    WAR.me?.let {
        var card = Card(TestCardAction())
        card.apply {
            entityName = "m1"
            entityId = "m1"
            atc = 5
            health = 5
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)

        card = Card(TestCardAction())
        card.apply {
            entityName = "m2"
            entityId = "m2"
            atc = 5
            health = 5
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)

        card = Card(TestCardAction())
        card.apply {
            entityName = "m3"
            entityId = "m3"
            atc = 5
            health = 5
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)

        card = Card(TestCardAction())
        card.apply {
            entityName = "m4"
            entityId = "m3"
            atc = 5
            health = 5
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)

        card = Card(TestCardAction())
        card.apply {
            entityName = "m2"
            entityId = "m2"
            atc = 5
            health = 5
            cardType = CardTypeEnum.MINION
        }
        it.playArea.add(card)

        card = Card(TestCardAction())
        card.apply {
            entityName = "m3"
            entityId = "m3"
            cost = 0
            atc = 5
            health = 5
            cardType = CardTypeEnum.MINION
        }
        it.handArea.add(card)

        card = Card(TestCardAction())
        card.apply {
            entityName = "m4"
            entityId = "m3"
            atc = 5
            health = 5
            cost = 0
            cardType = CardTypeEnum.MINION
        }
        it.handArea.add(card)

        card = Card(TestCardAction())
        card.apply {
            entityName = "mHero"
            entityId = "mHero"
            atc = 1
            health = 30
            cardType = CardTypeEnum.HERO
        }
        it.handArea.add(card)
    }

    WAR.rival?.let {
        var card = Card(TestCardAction())
        card.apply {
            entityName = "r1"
            entityId = "r1"
            atc = 5
            health = 10
            cardType = CardTypeEnum.MINION
        }
        it.handArea.add(card)

        card = Card(TestCardAction())
        card.apply {
            entityName = "r2"
            entityId = "r1"
            atc = 5
            health = 10
            cardType = CardTypeEnum.MINION
        }
        it.handArea.add(card)

        card = Card(TestCardAction())
        card.apply {
            entityName = "r3"
            entityId = "r1"
            atc = 5
            health = 10
            cardType = CardTypeEnum.MINION
        }
        it.handArea.add(card)

        card = Card(TestCardAction())
        card.apply {
            entityName = "r4"
            entityId = "r1"
            atc = 5
            health = 10
            cardType = CardTypeEnum.MINION
        }
        it.handArea.add(card)

        card = Card(TestCardAction())
        card.apply {
            entityName = "r5"
            entityId = "r1"
            atc = 5
            health = 10
            cardType = CardTypeEnum.MINION
        }
        it.handArea.add(card)

        card = Card(TestCardAction())
        card.apply {
            entityName = "r6"
            entityId = "r1"
            atc = 5
            health = 10
            cardType = CardTypeEnum.MINION
        }
        it.handArea.add(card)

        card = Card(TestCardAction())
        card.apply {
            entityName = "r7"
            entityId = "r1"
            atc = 5
            health = 10
            cardType = CardTypeEnum.MINION
        }
        it.handArea.add(card)

        card = Card(TestCardAction())
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