package club.xiaojiawei.util

import club.xiaojiawei.bean.SimulateCard
import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.Player
import club.xiaojiawei.bean.area.PlayArea
import club.xiaojiawei.enums.CardTypeEnum
import club.xiaojiawei.log
import club.xiaojiawei.status.War
import java.util.function.Function

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/9/13 17:39
 */
class DeckStrategyUtil {

    companion object {

        const val EXEC_ACTION: Boolean = false

        private var me: Player? = null
        private lateinit var myPlayArea: PlayArea
        private lateinit var myHandCards: List<Card>
        private lateinit var myPlayCards: List<Card>

        private var rival: Player? = null
        private lateinit var rivalPlayArea: PlayArea
        private lateinit var rivalHandCards: List<Card>
        private lateinit var rivalPlayCards: List<Card>

        private fun assign() {
            me = War.me
            me?.let {
                myPlayArea = it.playArea
                myPlayCards = myPlayArea.cards
                myHandCards = it.handArea.cards
            }
            rival = War.rival
            rival?.let {
                rivalPlayArea = it.playArea
                rivalPlayCards = rivalPlayArea.cards
                rivalHandCards = it.handArea.cards
            }
        }

        private class Result(
            var allWeight: Double = -1.0,
            var actions: List<Runnable>? = null
        ) {

            fun execAction() {
                actions?.let {
                    it.forEach { action -> action.run() }
                }

            }

            fun setNewResult(newWeight: Double, newActions: List<Runnable>?) {
                if (newWeight >= allWeight) {
                    synchronized(DeckStrategyUtil::javaClass) {
                        if (newWeight >= allWeight) {
                            allWeight = newWeight
                            actions = newActions?.toList()
                        }
                    }
                }
            }

            companion object {
                fun isFalse(result: Result): Boolean {
                    return result.actions == null
                }
            }
        }

        private fun findTauntCard(rivalPlayCards: List<Card>): Int {
            return rivalPlayCards.withIndex().find { it.value.isTaunt && it.value.canBeAttacked() }?.index ?: -1
        }

        private fun calcBlood(card: Card): Int {
            return card.health + card.armor - card.damage
        }

        /**
         * @param myAtcWeight 我方随从攻击力权重，大于1表示攻击力比生命值重要
         * @param rivalAtcWeight 敌方随从攻击力权重，rivalAtcWeight/myAtcWeight小于1时认为我方随从更加厉害，防止出现我方2-2解对方2-2的情况
         * @param lazyWeight 随从不动的权重
         */
        private fun clean(
            myAtcWeight: Double,
            rivalAtcWeight: Double,
            rivalAttackCountCalc: Function<Card, Int>,
            myCardWeightCalc: Function<Card, Double> = Function { if (it.isDeathRattle) 0.5 else 1.0 },
            rivalCardWeightCalc: Function<Card, Double> = Function { if (it.isDeathRattle) 0.5 else 1.0 },
        ): Result {
            val myCards = mutableListOf<SimulateCard>()
            val rivalCards = mutableListOf<SimulateCard>()

            val myPlayCards = myPlayCards.toMutableList()
            val rivalPlayCards = rivalPlayCards.toMutableList()
            myPlayArea.hero?.let { myPlayCards.add(it) }
            rivalPlayArea.hero?.let { rivalPlayCards.add(it) }

            for (myPlayCard in myPlayCards) {
                val attackCount = if (myPlayCard.canAttack()) {
                    if (myPlayCard.isWindFury) 2 else 1
                } else 0
                val inversionAttackCount = if (myPlayCard.canBeAttacked()) 1 else 0
                val simulateCard = SimulateCard(
                    myPlayCard,
                    attackCount,
                    inversionAttackCount,
                    myAtcWeight,
                    rivalAtcWeight,
                    calcBlood(myPlayCard),
                    cardWeight = if (myPlayCard.cardType === CardTypeEnum.HERO) myCardWeightCalc.apply(myPlayCard) + 0.01 else myCardWeightCalc.apply(
                        myPlayCard
                    ),
                    isDivineShield = myPlayCard.isDivineShield,
                )
                myCards.add(simulateCard)
            }
            for (rivalCard in rivalPlayCards) {
                val attackCount = rivalAttackCountCalc.apply(rivalCard)
                val inversionAttackCount = if (rivalCard.canAttack(true)) {
                    if (rivalCard.isWindFury) 2 else 1
                } else 0
                val simulateCard = SimulateCard(
                    rivalCard,
                    attackCount,
                    inversionAttackCount,
                    rivalAtcWeight,
                    myAtcWeight,
                    calcBlood(rivalCard),
                    cardWeight = if (rivalCard.cardType === CardTypeEnum.HERO) rivalCardWeightCalc.apply(rivalCard) + 0.01 else rivalCardWeightCalc.apply(
                        rivalCard
                    ),
                    isDivineShield = rivalCard.isDivineShield,
                )
                rivalCards.add(simulateCard)
            }

            return cleanPlay(myCards, rivalCards)
        }

        private fun cleanPlay(
            myCards: List<SimulateCard>, rivalCards: List<SimulateCard>,
        ): Result {
            val start = System.currentTimeMillis()
            val initWeight = calcAllWeight(myCards, rivalCards, false)
            val inversionResult = Result()
            recursionCleanPlay(
                copySimulateList(rivalCards),
                copySimulateList(myCards),
                0,
                mutableListOf(),
                inversionResult,
                true
            )
            val result = Result(0.6 * initWeight - 0.4 * inversionResult.allWeight)
            val atcActions = mutableListOf<Runnable>()
            recursionCleanPlay(
                myCards, rivalCards,
                0,
                atcActions, result, false
            )
            log.info { "思考解怪耗时：" + (System.currentTimeMillis() - start) + "ms" }
            if (!EXEC_ACTION) {
                println("思考解怪耗时：" + (System.currentTimeMillis() - start) + "ms")
            }
            return result
        }

        private fun recursionCleanPlay(
            myCards: List<SimulateCard>, rivalCards: List<SimulateCard>,
            myIndex: Int,
            atcActions: MutableList<Runnable>?, result: Result,
            inversion: Boolean,
        ) {
            if (myIndex == myCards.size) {
                val weight = calcAllWeight(myCards, rivalCards, inversion)
                if (inversion) {
                    result.setNewResult(weight, atcActions)
                }else{
                    val inversionResult = Result()
                    recursionCleanPlay(
                        copySimulateList(rivalCards),
                        copySimulateList(myCards),
                        0,
                        mutableListOf(),
                        inversionResult,
                        true
                    )
                    val inversionWeight = inversionResult.allWeight
                    result.setNewResult(0.6 * weight - 0.4 * inversionWeight, atcActions)
                }
                return
            }

            val myCard = myCards[myIndex]
            if (myCard.canAttack(inversion)) {
                for (rivalCard in rivalCards) {
                    attack(
                        myCards,
                        rivalCards,
                        myIndex,
                        atcActions,
                        result,
                        myCard,
                        rivalCard,
                        inversion
                    )
                }
            }
            recursionCleanPlay(
                myCards,
                rivalCards,
                myIndex + 1,
                atcActions,
                result,
                inversion
            )
        }

        private fun copySimulateList(cards: List<SimulateCard>): List<SimulateCard> {
            val copyList = mutableListOf<SimulateCard>()
            for (card in cards) {
                val clone = card.clone()
                copyList.add(clone)
            }
            return copyList
        }

        private fun attack(
            myCards: List<SimulateCard>, rivalCards: List<SimulateCard>,
            myIndex: Int,
            atcActions: MutableList<Runnable>?, result: Result,
            myCard: SimulateCard, rivalCard: SimulateCard,
            inversion: Boolean,
        ) {
            if (rivalCard.canBeAttacked(inversion)) {
                var index = 0
                atcActions?.let {
                    index = atcActions.size
                    atcActions.add(index) {
                        log.info {
                            "${myCard.card.entityName}攻击${rivalCard.card.entityName}"
                        }
                        if (EXEC_ACTION) {
                            myCard.card.action.attack(rivalCard.card)
                        } else {
                            println("${myCard.card.entityName}攻击${rivalCard.card.entityName}")
                        }
                    }
                }

                val myDivineShield = myCard.isDivineShield
                val rivalDivineShield = rivalCard.isDivineShield

                if (inversion) {
                    myCard.inversionAttackCount--
                } else {
                    myCard.attackCount--
                }
                if (myDivineShield) {
                    myCard.isDivineShield = false
                } else {
                    myCard.blood -= rivalCard.card.atc
                }
                if (rivalDivineShield) {
                    rivalCard.isDivineShield = false
                } else {
                    rivalCard.blood -= myCard.card.atc
                }

                val nextIndex = if (myCard.attackCount > 0) myIndex else myIndex + 1
                recursionCleanPlay(
                    myCards,
                    rivalCards,
                    nextIndex,
                    atcActions,
                    result,
                    inversion
                )

                if (inversion) {
                    myCard.inversionAttackCount++
                } else {
                    myCard.attackCount++
                }
                if (myDivineShield) {
                    myCard.isDivineShield = true
                } else {
                    myCard.blood += rivalCard.card.atc
                }
                if (rivalDivineShield) {
                    rivalCard.isDivineShield = true
                } else {
                    rivalCard.blood += myCard.card.atc
                }

                atcActions?.removeAt(index)
            }
        }

        private fun calcAllWeight(
            myCards: List<SimulateCard>, rivalCards: List<SimulateCard>, inversion: Boolean
        ): Double {
            return calcSelfWeight(myCards, inversion) - calcSelfWeight(rivalCards, inversion)
        }

        private fun calcSelfWeight(simulateCards: List<SimulateCard>, inversion: Boolean): Double {
            return simulateCards.sumOf { card ->
                card.calcSelfWeight(inversion)
            }
        }

        private fun calcLazyWeight(simulateCards: List<SimulateCard>, residualAtkWeight: Double): Double {
            return simulateCards.sumOf { card ->
                card.calcLazyWeight(residualAtkWeight)
            }
        }

        fun cleanTaunt(
            myAtcWeight: Double = 1.2,
            rivalAtcWeight: Double = 1.25,
        ): Boolean {
            assign()
            val result = clean(myAtcWeight, rivalAtcWeight, { card ->
                if (card.isTaunt && !card.isFrozen && !card.isDormantAwakenConditionEnchant) {
                    if (card.isWindFury) 2 else 1
                } else 0
            })
            result.execAction()
            return findTauntCard(rivalPlayCards) == -1
        }

        fun cleanNormal(
            myAtcWeight: Double = 1.2,
            rivalAtcWeight: Double = 1.2,
        ): Boolean {
            assign()
            val result = clean(myAtcWeight, rivalAtcWeight, rivalAttackCountCalc = { card ->
                if (card.isFrozen || card.isDormantAwakenConditionEnchant) 0 else {
                    if (card.isWindFury) 2 else 1
                }
            })
            result.execAction()
            return !Result.isFalse(result)
        }
    }

}
