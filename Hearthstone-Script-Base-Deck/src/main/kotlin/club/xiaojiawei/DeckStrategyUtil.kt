package club.xiaojiawei

import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.Player
import club.xiaojiawei.bean.area.PlayArea
import club.xiaojiawei.config.CALC_THREAD_POOL
import club.xiaojiawei.status.War
import java.util.concurrent.CompletableFuture
import java.util.function.Function

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/9/13 17:39
 */
class DeckStrategyUtil {

    companion object {

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
                    it.forEach { action -> action.run()}
                }

            }

            fun setNewResult(newWeight: Double, newActions: List<Runnable>) {
                if (newWeight >= allWeight) {
                    synchronized(HsBaseDeckStrategy::javaClass) {
                        if (newWeight >= allWeight) {
                            allWeight = newWeight
                            actions = newActions.toList()
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
         * @param residualAtkWeight 随从不动的权重
         */
        private fun cleanPlay(
            myAtcWeight: Double,
            rivalAtcWeight: Double,
            residualAtkWeight: Double,
            rivalAttackCountCalc: Function<Card, Int>
        ): Result {
            val myCards = mutableListOf<SimulateCard>()
            val rivalCards = mutableListOf<SimulateCard>()
            val myPlay = mutableListOf(myPlayArea.hero) + myPlayCards
            val rivalPlay = mutableListOf(rivalPlayArea.hero) + rivalPlayCards
            for (myPlayCard in myPlay) {
                val attackCount = if (myPlayCard.canMove()) {
                    if (myPlayCard.isWindFury) 2 else 1
                } else 0
                val simulateCard = SimulateCard(
                    myPlayCard,
                    attackCount,
                    myPlayCard.health + myPlayCard.armor - myPlayCard.damage,
                    myAtcWeight
                )
                myCards.add(simulateCard)
            }
            for (rivalCard in rivalPlay) {
                val attackCount = rivalAttackCountCalc.apply(rivalCard)
                val simulateCard = SimulateCard(
                    rivalCard,
                    attackCount,
                    rivalCard.health + rivalCard.armor - rivalCard.damage,
                    rivalAtcWeight
                )
                rivalCards.add(simulateCard)
            }
            return cleanPlay(myCards, rivalCards, myAtcWeight, rivalAtcWeight, residualAtkWeight)
        }

        private fun cleanPlay(
            myCards: List<SimulateCard>, rivalCards: List<SimulateCard>,
            myAtcWeight: Double, rivalAtcWeight: Double, residualAtkWeight: Double,
        ): Result {
            val result = Result(calcAllWeight(myCards, rivalCards))
            val start = System.currentTimeMillis()
            val atcActions = mutableListOf<Runnable>()
            recursionCleanPlay(
                myCards, rivalCards,
                myAtcWeight, rivalAtcWeight,
                0, residualAtkWeight,
                atcActions, result
            )
            log.info { "思考解怪耗时：" + (System.currentTimeMillis() - start) + "ms" }
            return result
        }

        private fun recursionCleanPlay(
            myCards: List<SimulateCard>, rivalCards: List<SimulateCard>,
            myCardAtcWeight: Double, rivalCardAtcWeight: Double,
            myIndex: Int, residualAtkWeight: Double,
            atcActions: MutableList<Runnable>, result: Result,
        ) {
            if (myIndex == myCards.size) {
                val weight = calcAllWeight(myCards, rivalCards)
                result.setNewResult(weight, atcActions)
                return
            }
            val futures: Array<CompletableFuture<*>?> = arrayOfNulls(rivalCards.size + 1)
            val myCard = myCards[myIndex]
            if (myCard.canAttack()) {
                for ((index, rivalCard) in rivalCards.withIndex()) {
                    futures[index] = CompletableFuture.runAsync({
                        attack(
                            myCards,
                            rivalCards,
                            myCardAtcWeight,
                            rivalCardAtcWeight,
                            myIndex,
                            residualAtkWeight,
                            atcActions,
                            result,
                            myCard,
                            rivalCard
                        )
                    }, CALC_THREAD_POOL)
                }
            }
            futures[futures.size - 1] = CompletableFuture.runAsync {
                recursionCleanPlay(
                    myCards,
                    rivalCards,
                    myCardAtcWeight,
                    rivalCardAtcWeight,
                    myIndex + 1,
                    residualAtkWeight,
                    atcActions,
                    result
                )
            }
            CompletableFuture.allOf(*futures).get()
        }

        private fun attack(
            myCards: List<SimulateCard>, rivalCards: List<SimulateCard>,
            myCardAtcWeight: Double, rivalCardAtcWeight: Double,
            myIndex: Int, residualAtkWeight: Double,
            atcActions: MutableList<Runnable>, result: Result,
            myCard: SimulateCard, rivalCard: SimulateCard,
        ) {
            if (rivalCard.canBeAttacked()) {
                val index = atcActions.size
                atcActions.add(index) {
                    log.info {
                        "${myCard.card.entityName}攻击${rivalCard.card.entityName}"
                    }
//                    myCard.card.action.attack(rivalCard.card)
                }

                myCard.attackCount--
                myCard.blood -= rivalCard.card.atc
                rivalCard.blood -= myCard.card.atc

                val nextIndex = if (myCard.attackCount > 0) myIndex else myIndex + 1
                recursionCleanPlay(
                    myCards,
                    rivalCards,
                    myCardAtcWeight,
                    rivalCardAtcWeight,
                    nextIndex,
                    residualAtkWeight,
                    atcActions,
                    result
                )

                myCard.attackCount++
                myCard.blood += rivalCard.card.atc
                rivalCard.blood += myCard.card.atc

                atcActions.removeAt(index)
            }
        }

        private fun calcAllWeight(
            myCards: List<SimulateCard>, rivalCards: List<SimulateCard>,
        ): Double {
            return calcWeight(myCards) - calcWeight(rivalCards) + calcResidualAtkWeight(myCards)
        }

        private fun calcWeight(simulateCards: List<SimulateCard>): Double {
            return simulateCards.sumOf { card ->
                card.calcSelfWeight()
            }
        }

        private fun calcResidualAtkWeight(simulateCards: List<SimulateCard>): Double {
            return simulateCards.sumOf { card ->
                card.calcAtcWeight()
            }
        }

        fun cleanTaunt(
            myAtcWeight: Double = 1.3,
            rivalAtcWeight: Double = 10.0,
            residualAtkWeight: Double = 0.001
        ): Boolean {
            assign()
            val result = cleanPlay(myAtcWeight, rivalAtcWeight, residualAtkWeight) { card ->
                if (!card.isTaunt || card.isFrozen || card.isDormantAwakenConditionEnchant || card.atc <= 0) 0 else 1
            }
            result.execAction()
            return findTauntCard(rivalPlayCards) == -1
        }

        fun cleanNormal(
            myAtcWeight: Double = 1.3,
            rivalAtcWeight: Double = 1.35,
            residualAtkWeight: Double = 1.0
        ): Boolean {
            assign()
            val result = cleanPlay(myAtcWeight, rivalAtcWeight, residualAtkWeight) { card ->
                if (card.isFrozen || card.isDormantAwakenConditionEnchant || card.atc <= 0) 0 else 1
            }
            result.execAction()
            return !Result.isFalse(result)
        }
    }

}
