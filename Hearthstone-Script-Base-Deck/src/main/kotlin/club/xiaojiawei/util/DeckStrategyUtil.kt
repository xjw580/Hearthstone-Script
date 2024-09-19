package club.xiaojiawei.util

import club.xiaojiawei.bean.SimulateCard
import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.Player
import club.xiaojiawei.bean.area.PlayArea
import club.xiaojiawei.log
import club.xiaojiawei.status.War
import java.util.function.Function

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/9/13 17:39
 */
class DeckStrategyUtil {

    companion object {

        const val EXEC_ACTION:Boolean = false

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

        /**
         * @param myAtcWeight 我方随从攻击力权重，大于1表示攻击力比生命值重要
         * @param rivalAtcWeight 敌方随从攻击力权重，rivalAtcWeight/myAtcWeight小于1时认为我方随从更加厉害，防止出现我方2-2解对方2-2的情况
         * @param lazyWeight 随从不动的权重
         */
        private fun clean(
            myAtcWeight: Double,
            rivalAtcWeight: Double,
            lazyWeight: Double,
            rivalAttackCountCalc: Function<Card, Int>,
            myCardWeightCalc: Function<Card, Double> = Function { if (it.isDeathRattle) 0.5 else 1.0 },
            rivalCardWeightCalc: Function<Card, Double> = Function { if (it.isDeathRattle) 0.3 else 1.0 },
        ): Result {
            val myCards = mutableListOf<SimulateCard>()
            val rivalCards = mutableListOf<SimulateCard>()
            val myPlay = myPlayCards.toMutableList()
            myPlayArea.hero?.let { myPlay.add(it) }
            val rivalPlay = rivalPlayCards.toMutableList()
            rivalPlayArea.hero?.let { rivalPlay.add(it) }
            for (myPlayCard in myPlay) {
                val attackCount = if (myPlayCard.canAttack()) {
                    if (myPlayCard.isWindFury) 2 else 1
                } else 0
                val simulateCard = SimulateCard(
                    myPlayCard,
                    attackCount,
                    myPlayCard.health + myPlayCard.armor - myPlayCard.damage,
                    myAtcWeight,
                    isDivineShield = myPlayCard.isDivineShield
                )
                myCards.add(simulateCard)
            }
            for (rivalCard in rivalPlay) {
                val attackCount = rivalAttackCountCalc.apply(rivalCard)
                val simulateCard = SimulateCard(
                    rivalCard,
                    attackCount,
                    rivalCard.health + rivalCard.armor - rivalCard.damage,
                    rivalAtcWeight,
                    isDivineShield = rivalCard.isDivineShield,
                )
                rivalCards.add(simulateCard)
            }
            return cleanPlay(myCards, rivalCards, lazyWeight)
        }

        private fun cleanPlay(
            myCards: List<SimulateCard>, rivalCards: List<SimulateCard>,
            lazyAtkWeight: Double
        ): Result {
            val start = System.currentTimeMillis()
            val result = Result(calcAllWeight(myCards, rivalCards, lazyAtkWeight))

            val atcActions = mutableListOf<Runnable>()
            recursionCleanPlay(
                myCards, rivalCards,
                0, lazyAtkWeight,
                atcActions, result
            )
            log.info { "思考解怪耗时：" + (System.currentTimeMillis() - start) + "ms" }
            if (!EXEC_ACTION){
                println("思考解怪耗时：" + (System.currentTimeMillis() - start) + "ms")
            }
            return result
        }

        private fun recursionCleanPlay(
            myCards: List<SimulateCard>, rivalCards: List<SimulateCard>,
            myIndex: Int, lazyWeight: Double,
            atcActions: MutableList<Runnable>?, result: Result,
        ) {
            if (myIndex == myCards.size) {
                val weight = calcAllWeight(myCards, rivalCards, lazyWeight)
                result.setNewResult(weight, atcActions)
                return
            }

            val myCard = myCards[myIndex]
            if (myCard.canAttack()) {
                for (rivalCard in rivalCards) {
                    attack(
                        myCards,
                        rivalCards,
                        myIndex,
                        lazyWeight,
                        atcActions,
                        result,
                        myCard,
                        rivalCard,
                    )
                }
            }
            recursionCleanPlay(
                myCards,
                rivalCards,
                myIndex + 1,
                lazyWeight,
                atcActions,
                result,
            )
        }

        private fun copySimulateList(cards: List<SimulateCard>, resetInit: Boolean = false): List<SimulateCard> {
            val copyList = mutableListOf<SimulateCard>()
            for (card in cards) {
                val clone = card.clone()
                if (resetInit) {
                    clone.attackCount = card.initAttackCount
                }
                copyList.add(clone)
            }
            return copyList
        }

        private fun attack(
            myCards: List<SimulateCard>, rivalCards: List<SimulateCard>,
            myIndex: Int, lazyWeight: Double,
            atcActions: MutableList<Runnable>?, result: Result,
            myCard: SimulateCard, rivalCard: SimulateCard,
        ) {
            if (rivalCard.canBeAttacked()) {
                var index = 0
                atcActions?.let {
                    index = atcActions.size
                    atcActions.add(index) {
                        log.info {
                            "${myCard.card.entityName}攻击${rivalCard.card.entityName}"
                        }
                        if (EXEC_ACTION){
                            myCard.card.action.attack(rivalCard.card)
                        }else{
                            println("${myCard.card.entityName}攻击${rivalCard.card.entityName}")
                        }
                    }
                }

                val myDivineShield = myCard.isDivineShield
                val rivalDivineShield = rivalCard.isDivineShield

                myCard.attackCount--
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
                    lazyWeight,
                    atcActions,
                    result,
                )

                myCard.attackCount++
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
            myCards: List<SimulateCard>, rivalCards: List<SimulateCard>, lazyWeight: Double
        ): Double {
            return calcSelfWeight(myCards) +
                    calcLazyWeight(myCards, lazyWeight) -
                    calcSelfWeight(rivalCards)
        }

        private fun calcSelfWeight(simulateCards: List<SimulateCard>): Double {
            return simulateCards.sumOf { card ->
                card.calcSelfWeight()
            }
        }

        private fun calcLazyWeight(simulateCards: List<SimulateCard>, residualAtkWeight: Double): Double {
            return simulateCards.sumOf { card ->
                card.calcLazyWeight(residualAtkWeight)
            }
        }

        fun cleanTaunt(
            myAtcWeight: Double = 1.0,
            rivalAtcWeight: Double = 1.05,
            lazyWeight: Double = 0.0
        ): Boolean {
            assign()
            val result = clean(myAtcWeight, rivalAtcWeight, lazyWeight, { card ->
                if (card.isTaunt && !card.isFrozen && !card.isDormantAwakenConditionEnchant) {
                    if (card.isWindFury) 2 else 1
                } else 0
            })
            result.execAction()
            return findTauntCard(rivalPlayCards) == -1
        }

        fun cleanNormal(
            myAtcWeight: Double = 1.0,
            rivalAtcWeight: Double = 1.05,
            lazyWeight: Double = 1.1
        ): Boolean {
            assign()
            val result = clean(myAtcWeight, rivalAtcWeight, lazyWeight, { card ->
                if (card.isFrozen || card.isDormantAwakenConditionEnchant) 0 else {
                    if (card.isWindFury) 2 else 1
                }
            })
            result.execAction()
            return !Result.isFalse(result)
        }
    }

}
