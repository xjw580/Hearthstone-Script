package club.xiaojiawei.hsscriptbasestrategy.util

import club.xiaojiawei.bean.*
import club.xiaojiawei.hsscriptbasestrategy.bean.SimulateCard.Companion.TAUNT_EXTRA_WEIGHT
import club.xiaojiawei.bean.area.PlayArea
import club.xiaojiawei.hsscriptbase.config.CALC_THREAD_POOL
import club.xiaojiawei.hsscriptbase.config.log
import club.xiaojiawei.data.CARD_INFO_TRIE
import club.xiaojiawei.data.CARD_WEIGHT_TRIE
import club.xiaojiawei.enums.CardActionEnum
import club.xiaojiawei.enums.CardTypeEnum
import club.xiaojiawei.hsscriptbase.bean.CardWeight
import club.xiaojiawei.hsscriptbasestrategy.bean.SimulateCard
import club.xiaojiawei.hsscriptbasestrategy.bean.SimulateWeightCard
import club.xiaojiawei.status.WAR
import club.xiaojiawei.util.CardUtil
import java.util.concurrent.CompletableFuture
import java.util.function.Function
import kotlin.math.max
import kotlin.math.min

/**
 * 卡牌策略工具
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/9/13 17:39
 */

private val MAX_INVERSION_CALC_COUNT = min(12, Runtime.getRuntime().availableProcessors())

object DeckStrategyUtil {
    var execAction: Boolean = true

    private var me: Player? = null
    private lateinit var myPlayArea: PlayArea
    private lateinit var myHandCards: List<Card>
    private lateinit var myPlayCards: List<Card>

    private var rival: Player? = null
    private lateinit var rivalPlayArea: PlayArea
    private lateinit var rivalHandCards: List<Card>
    private lateinit var rivalPlayCards: List<Card>

    private fun assign() {
        me = WAR.me
        me?.let {
            myPlayArea = it.playArea
            myPlayCards = myPlayArea.cards
            myHandCards = it.handArea.cards
        }
        rival = WAR.rival
        rival?.let {
            rivalPlayArea = it.playArea
            rivalPlayCards = rivalPlayArea.cards
            rivalHandCards = it.handArea.cards
        }
    }

    private data class Action(
        val deathCard: Card?,
        val runnable: Runnable,
    )

    private class Result(
        @Volatile
        var allWeight: Double = Int.MIN_VALUE.toDouble(),
    ) {
        private var actions: List<Action> = emptyList()

        var myCard: MutableList<SimulateCard>? = null

        var rivalCard: MutableList<SimulateCard>? = null

        fun execAction(): Int {
            val text = "权重: $allWeight, 步骤↓"
            log.info { text }
            if (!execAction) {
                println(text)
            }
            var deathCount = 0
            for (action in actions) {
                deathCount += if (action.deathCard == null) 0 else 1
                action.runnable.run()
            }
            return deathCount
        }

        fun setNewResult(result: Result) {
            setNewResult(result.allWeight, result.actions, result.myCard, result.rivalCard)
        }

        fun setNewResult(
            newWeight: Double,
            newActions: List<Action>,
            myCards: List<SimulateCard>?,
            rivalCards: List<SimulateCard>?,
        ) {
            if (newWeight >= allWeight) {
                synchronized(DeckStrategyUtil::javaClass) {
                    if (newWeight >= allWeight) {
                        allWeight = newWeight
                        actions = newActions.toList()
                        myCards?.let {
                            this.myCard = SimulateCard.copySimulateList(it)
                        }
                        rivalCards?.let {
                            this.rivalCard = SimulateCard.copySimulateList(it)
                        }
                    }
                }
            }
        }

        fun isValid(result: Result): Boolean = result.actions !== emptyList<Action>()
    }

    private fun findTauntCardCount(rivalPlayCards: List<Card>): Int =
        rivalPlayCards.sumOf {
            if (it.isTaunt && it.canBeAttacked()) 1 else 0 as Int
        }

    private fun calcBlood(card: Card): Int = card.health + card.armor - card.damage

    /**
     * @param myAtcWeight 我方随从攻击力权重，大于1表示攻击力比生命值重要
     * @param rivalAtcWeight 敌方随从攻击力权重，rivalAtcWeight/myAtcWeight小于1时认为我方随从更加厉害，防止出现我方2-2解对方2-2的情况
     */
    private fun clean(
        myAtcWeight: Double,
        rivalAtcWeight: Double,
        myPlayCards: MutableList<Card>,
        rivalPlayCards: MutableList<Card>,
    ) {
        val myCardWeightCalc: Function<Card, Double> =
            Function {
                var value = CARD_WEIGHT_TRIE[it.cardId]?.weight ?: 1.0
                if (it.isDeathRattle) {
                    value -= 0.3
                }
                if (it.isTaunt) {
                    value += 0.1
                }
                if (it.isAdjacentBuff) {
                    value += 0.3
                }
                if (it.isAura) {
                    value += 0.3
                }
                if (it.isWindFury) {
                    value += 0.15
                }
                if (it.isMegaWindfury) {
                    value += 0.4
                }
                if (it.isTitan) {
                    value += 0.5
                }
                if (it.isTriggerVisual) {
                    value += 0.1
                }
                if (it.isPoisonous) {
                    value += 0.1
                }
                value += it.spellPower * 0.1
                if (it.cardType === CardTypeEnum.HERO) {
                    value += 0.01
                }
                value
            }
        val rivalCardWeightCalc: Function<Card, Double> = myCardWeightCalc

        val myAtcWeightCalc: Function<Card, Double> =
            Function {
                var value = myAtcWeight
                if (it.isLifesteal) {
                    value += 0.1
                }
                if (it.isReborn) {
                    value -= max((5 - it.atc) / 10.0, 0.1)
                }
                value
            }
        val rivalAtcWeightCalc: Function<Card, Double> =
            Function {
                var value = rivalAtcWeight
                if (it.isLifesteal) {
                    value += 0.1
                }
                if (it.isReborn) {
                    value -= max((5 - it.atc) / 10.0, 0.1)
                }
                value
            }

        var firstMyCards: MutableList<SimulateCard>? = null
        var text: String

//        清理嘲讽
        val myAttackCountCalc: Function<Card, Int> =
            Function<Card, Int> {
                if (it.canAttack()) {
                    if (it.isMegaWindfury) 4
                    if (it.isWindFury) 2 else 1
                } else {
                    0
                }
            }
        var myInversionAttackCountCalc: Function<Card, Int>
        var rivalAttackCountCalc: Function<Card, Int>
        var rivalInversionAttackCountCalc: Function<Card, Int>
        val findTauntCardCount = findTauntCardCount(this.rivalPlayCards)
        if (findTauntCardCount > 0) {
            myInversionAttackCountCalc = Function<Card, Int> { 0 }
            rivalAttackCountCalc =
                Function<Card, Int> {
                    if (it.isTaunt && it.canBeAttacked()) {
                        1
                    } else {
                        0
                    }
                }
            rivalInversionAttackCountCalc = Function<Card, Int> { 0 }
            val (myCards, rivalCards) =
                getCleanData(
                    myPlayCards = myPlayCards,
                    rivalPlayCards = rivalPlayCards,
                    myAtcWeightCalc = myAtcWeightCalc,
                    rivalAtcWeightCalc = rivalAtcWeightCalc,
                    myAttackCountCalc = myAttackCountCalc,
                    myInversionAttackCountCalc = myInversionAttackCountCalc,
                    rivalAttackCountCalc = rivalAttackCountCalc,
                    rivalInversionAttackCountCalc = rivalInversionAttackCountCalc,
                    myCardWeightCalc = myCardWeightCalc,
                    rivalCardWeightCalc = rivalCardWeightCalc,
                )
            text = "开始思考清理嘲讽"
            log.info { text }
            if (!execAction) {
                println(text)
            }
            val result = calcClean(myCards, rivalCards, true)
            val deathCount = result.execAction()
            if (deathCount < findTauntCardCount) {
                return
            }
            if (execAction) {
                Thread.sleep(3500)
                if (findTauntCardCount(this.rivalPlayCards) > 0) {
                    return
                }
                this.myPlayArea.hero?.let { myPlayCards.add(it) }
                this.rivalPlayArea.hero?.let { rivalPlayCards.add(it) }
            } else {
                firstMyCards = result.myCard
                rivalPlayCards.removeIf { it.isTaunt && it.canBeAttacked() }
            }
        }

//        普通清理
        myInversionAttackCountCalc =
            Function<Card, Int> {
                if (it.canBeAttacked()) 1 else 0
            }
        rivalAttackCountCalc = myInversionAttackCountCalc
        rivalInversionAttackCountCalc =
            Function<Card, Int> {
                if (it.canAttack(true)) {
                    if (it.isMegaWindfury) 4
                    if (it.isWindFury) 2 else 1
                } else {
                    0
                }
            }

        val (myNormalCards, rivalNormalCards) =
            getCleanData(
                myPlayCards = myPlayCards,
                rivalPlayCards = rivalPlayCards,
                myAtcWeightCalc = myAtcWeightCalc,
                rivalAtcWeightCalc = rivalAtcWeightCalc,
                myAttackCountCalc = myAttackCountCalc,
                myInversionAttackCountCalc = myInversionAttackCountCalc,
                rivalAttackCountCalc = rivalAttackCountCalc,
                rivalInversionAttackCountCalc = rivalInversionAttackCountCalc,
                myCardWeightCalc = myCardWeightCalc,
                rivalCardWeightCalc = rivalCardWeightCalc,
            )
        text = "开始思考清理万物"
        log.info { text }
        if (!execAction) {
            println(text)
        }
        if (firstMyCards == null) {
            calcClean(myNormalCards, rivalNormalCards).execAction()
        } else {
            calcClean(firstMyCards, rivalNormalCards).execAction()
        }
    }

    private fun getCleanData(
        myPlayCards: MutableList<Card>,
        rivalPlayCards: MutableList<Card>,
        myAtcWeightCalc: Function<Card, Double>,
        rivalAtcWeightCalc: Function<Card, Double>,
        myAttackCountCalc: Function<Card, Int>,
        myInversionAttackCountCalc: Function<Card, Int>,
        rivalAttackCountCalc: Function<Card, Int>,
        rivalInversionAttackCountCalc: Function<Card, Int>,
        myCardWeightCalc: Function<Card, Double>,
        rivalCardWeightCalc: Function<Card, Double>,
    ): Pair<MutableList<SimulateCard>, MutableList<SimulateCard>> {
        val myCards = mutableListOf<SimulateCard>()
        val rivalCards = mutableListOf<SimulateCard>()
        for (myPlayCard in myPlayCards) {
            if (!myPlayCard.isLaunchpad) {
                val simulateCard =
                    SimulateCard(
                        card = myPlayCard,
                        CARD_INFO_TRIE[myPlayCard.cardId],
                        attackCount = myAttackCountCalc.apply(myPlayCard),
                        inversionAttackCount = myInversionAttackCountCalc.apply(myPlayCard),
                        atcWeight = myAtcWeightCalc.apply(myPlayCard),
                        inversionAtcWeight = rivalAtcWeightCalc.apply(myPlayCard),
                        blood = calcBlood(myPlayCard),
//                对末日预言者特殊处理
                        cardWeight =
                            if (myPlayCard.cardId.contains("NEW1_021")) {
                                15.0
                            } else {
                                myCardWeightCalc.apply(
                                    myPlayCard,
                                )
                            },
                        inversionCardWeight = rivalCardWeightCalc.apply(myPlayCard),
                        isDivineShield = myPlayCard.isDivineShield,
                    )
                myCards.add(simulateCard)
            }
        }
        for (rivalCard in rivalPlayCards) {
            val simulateCard =
                SimulateCard(
                    card = rivalCard,
                    CARD_INFO_TRIE[rivalCard.cardId],
                    attackCount = rivalAttackCountCalc.apply(rivalCard),
                    inversionAttackCount = rivalInversionAttackCountCalc.apply(rivalCard),
                    atcWeight = rivalAtcWeightCalc.apply(rivalCard),
                    inversionAtcWeight = myAtcWeightCalc.apply(rivalCard),
                    blood = calcBlood(rivalCard),
                    cardWeight = rivalCardWeightCalc.apply(rivalCard),
                    inversionCardWeight = myCardWeightCalc.apply(rivalCard),
                    isDivineShield = rivalCard.isDivineShield,
                )
            rivalCards.add(simulateCard)
        }
        return Pair(myCards, rivalCards)
    }

    private fun calcClean(
        myCards: MutableList<SimulateCard>,
        rivalCards: MutableList<SimulateCard>,
        disableInversion: Boolean = false,
    ): Result {
        val start = System.currentTimeMillis()
        val finalResult = Result()
        val task = mutableListOf<CompletableFuture<Void>>()
        var text: String
        var realDisableInversion = disableInversion
        if (!realDisableInversion) {
            realDisableInversion =
                rivalCards.sumOf { it.inversionAttackCount } + myCards.sumOf { it.attackCount } > MAX_INVERSION_CALC_COUNT ||
                        rivalCards.sumOf { it.attackCount } + myCards.sumOf { it.inversionAttackCount } > MAX_INVERSION_CALC_COUNT
        }
        text =
            if (realDisableInversion) {
                "禁用反演"
            } else {
                "启用反演"
            }
        log.info { text }
        if (!execAction) {
            println(text)
        }
        for (index in myCards.indices) {
            if (myCards[0].canAttack(false)) {
                val tempMyCards = SimulateCard.copySimulateList(myCards)
                val tempRivalCards = SimulateCard.copySimulateList(rivalCards)
                task.add(
                    CompletableFuture.runAsync({
                        val initWeight = calcStateWeight(tempMyCards, tempRivalCards, false)
                        val result: Result
                        if (realDisableInversion) {
                            result = Result(initWeight)
                        } else {
                            val inversionMyCards = SimulateCard.copySimulateList(tempMyCards)
                            val inversionRivalCards = SimulateCard.copySimulateList(tempRivalCards)
                            val inversionResult = Result()
                            recursionCalcClean(
                                inversionRivalCards,
                                inversionMyCards,
                                0,
                                mutableListOf(),
                                inversionResult,
                                true,
                                disableInversion = false,
                            )
                            result = Result(calcAllWeight(initWeight, inversionResult.allWeight))
                        }
                        recursionCalcClean(
                            tempMyCards,
                            tempRivalCards,
                            0,
                            mutableListOf(),
                            result,
                            false,
                            realDisableInversion,
                        )
                        finalResult.setNewResult(result)
                    }, CALC_THREAD_POOL),
                )
            }
            myCards.add(myCards.removeFirst())
        }
        CompletableFuture.allOf(*task.toTypedArray()).get()
        text = "思考耗时：" + (System.currentTimeMillis() - start) + "ms"
        log.info { text }
        if (!execAction) {
            println(text)
        }
        return finalResult
    }

    private fun recursionCalcClean(
        myCards: List<SimulateCard>,
        rivalCards: List<SimulateCard>,
        myIndex: Int,
        actions: MutableList<Action>,
        result: Result,
        inversion: Boolean,
        disableInversion: Boolean,
    ) {
        if (myIndex == myCards.size) {
            val weight = calcStateWeight(myCards, rivalCards, inversion)
            if (disableInversion || inversion) {
                result.setNewResult(weight, actions, myCards, rivalCards)
            } else {
                val inversionResult = Result()

                recursionCalcClean(
                    SimulateCard.copySimulateList(rivalCards),
                    SimulateCard.copySimulateList(myCards),
                    0,
                    mutableListOf(),
                    inversionResult,
                    true,
                    disableInversion = false,
                )
                result.setNewResult(calcAllWeight(weight, inversionResult.allWeight), actions, myCards, rivalCards)
            }
            return
        }

        val myCard = myCards[myIndex]
        val task = mutableListOf<CompletableFuture<Void>>()
        if (myCard.canAttack(inversion)) {
            for (rivalCard in rivalCards) {
//                敌方随从能被攻击，突袭无法攻击英雄
                if (rivalCard.canBeAttacked(inversion) &&
                    !(
                            rivalCard.card.cardType === CardTypeEnum.HERO &&
                                    (myCard.card.isAttackableByRush || (myCard.card.isRush && myCard.card.numTurnsInPlay == 0))
                            )
                ) {
                    val actionEnum = myCard.cardInfo?.powerActions?.firstOrNull()
                    if (actionEnum == null || actionEnum === CardActionEnum.POINT_RIVAL || actionEnum === CardActionEnum.POINT_WHATEVER ||
                        (rivalCard.card.cardType !== CardTypeEnum.HERO && (actionEnum === CardActionEnum.POINT_RIVAL_MINION || actionEnum === CardActionEnum.POINT_MINION)) ||
                        (rivalCard.card.cardType === CardTypeEnum.HERO && (actionEnum === CardActionEnum.POINT_RIVAL_HERO || actionEnum === CardActionEnum.POINT_HERO))
                    ) {
                        attack(
                            myCards,
                            rivalCards,
                            myIndex,
                            actions,
                            result,
                            myCard,
                            rivalCard,
                            inversion,
                            disableInversion,
                        )
                    }
                }
            }
        }
        recursionCalcClean(
            myCards,
            rivalCards,
            myIndex + 1,
            actions,
            result,
            inversion,
            disableInversion,
        )
        CompletableFuture.allOf(*task.toTypedArray()).get()
    }

    /**
     * 模拟攻击
     */
    private fun attack(
        myCards: List<SimulateCard>,
        rivalCards: List<SimulateCard>,
        myIndex: Int,
        actions: MutableList<Action>,
        result: Result,
        myCard: SimulateCard,
        rivalCard: SimulateCard,
        inversion: Boolean,
        disableInversion: Boolean,
    ) {
        val myDivineShield = myCard.isDivineShield
        val rivalDivineShield = rivalCard.isDivineShield

        val index = actions.size
        val myCardBlood = myCard.blood
        val rivalCardBlood = rivalCard.blood

        if (inversion) {
            myCard.inversionAttackCount--
        } else {
            myCard.attackCount--
        }

        if (myCard.card.isImmuneWhileAttacking || myCard.card.isImmune) {
        } else if (myDivineShield) {
            if (rivalCard.card.atc > 0) {
                myCard.isDivineShield = false
            }
        } else if (rivalCard.card.isPoisonous && myCard.card.cardType === CardTypeEnum.MINION) {
            myCard.blood = -myCard.blood
        } else {
            myCard.blood -= rivalCard.card.atc
        }

        if (rivalCard.card.isImmuneWhileAttacking || rivalCard.card.isImmune) {
        } else if (rivalDivineShield) {
            if (myCard.card.atc > 0) {
                rivalCard.isDivineShield = false
            }
        } else if (myCard.card.isPoisonous && rivalCard.card.cardType === CardTypeEnum.MINION) {
            rivalCard.blood = -rivalCard.blood
        } else {
            rivalCard.blood -= myCard.card.atc
        }

        val deathCard = if (rivalCard.isAlive()) null else rivalCard.card
        actions.add(
            Action(deathCard) {
                val myC = myCard.card
                val rivalC = rivalCard.card
                val text =
                    "【${myC.entityId}-${myC.entityName}: ${myC.atc}-$myCardBlood】攻击【${rivalC.entityId}-${rivalC.entityName}: ${rivalC.atc}-$rivalCardBlood】"
                log.info { text }
                if (execAction) {
                    myCard.card.action.attack(rivalCard.card)
                } else if (deathCard != null) {
                    println("$text -> 死亡:${deathCard.entityId}-${deathCard.entityName}")
                }
            },
        )

        val nextIndex = if (myCard.attackCount > 0) myIndex else myIndex + 1
        recursionCalcClean(
            myCards,
            rivalCards,
            nextIndex,
            actions,
            result,
            inversion,
            disableInversion,
        )

        if (inversion) {
            myCard.inversionAttackCount++
        } else {
            myCard.attackCount++
        }

        if (myCard.card.isImmuneWhileAttacking || myCard.card.isImmune) {
        } else if (myDivineShield) {
            myCard.isDivineShield = true
        } else if (rivalCard.card.isPoisonous && myCard.card.cardType === CardTypeEnum.MINION) {
            myCard.blood = -myCard.blood
        } else {
            myCard.blood += rivalCard.card.atc
        }

        if (rivalCard.card.isImmuneWhileAttacking || rivalCard.card.isImmune) {
        } else if (rivalDivineShield) {
            rivalCard.isDivineShield = true
        } else if (myCard.card.isPoisonous && rivalCard.card.cardType === CardTypeEnum.MINION) {
            rivalCard.blood = -rivalCard.blood
        } else {
            rivalCard.blood += myCard.card.atc
        }
        actions.removeAt(index)
    }

    private fun calcAllWeight(
        weight: Double,
        inversionWeight: Double,
    ): Double = 0.6 * weight - 0.4 * inversionWeight

    /**
     * 评估函数
     */
    private fun calcStateWeight(
        myCards: List<SimulateCard>,
        rivalCards: List<SimulateCard>,
        inversion: Boolean,
    ): Double {
        val myWeight = calcSelfWeight(myCards, inversion)
        val rivalWeight = calcSelfWeight(rivalCards, inversion)
        return myWeight.first - rivalWeight.first - if (rivalWeight.second > 0) TAUNT_EXTRA_WEIGHT else 0
    }

    private fun calcSelfWeight(
        simulateCards: List<SimulateCard>,
        inversion: Boolean,
    ): Pair<Double, Int> {
        var tauntCount = 0
        var weight = 0.0
        for (simulateCard in simulateCards) {
            weight += simulateCard.calcSelfWeight(inversion)
            if (simulateCard.card.isTaunt && simulateCard.card.canBeAttacked() && simulateCard.isAlive()) {
                tauntCount++
            }
        }
        return Pair(weight, tauntCount)
    }

    /**
     * 获取嘲讽[Card.isTaunt]随从[CardTypeEnum.MINION]
     */
    fun getTauntCard(
        cards: List<Card>,
        canBeAttacked: Boolean = true,
    ): MutableList<Card> {
        val result = mutableListOf<Card>()
        for (card in cards) {
            if (card.isTaunt && (!canBeAttacked || card.canBeAttacked())) {
                result.add(card)
            }
        }
        return result
    }

    /**
     * 清场
     * @param myAtcWeight 我方卡牌攻击力权重
     * @param rivalAtcWeight 敌方卡牌攻击力权重
     * @param myPlayCards 需要计算的我方卡牌
     * @param rivalPlayCards 需要计算的敌方卡牌
     */
    fun cleanPlay(
        myAtcWeight: Double = 1.2,
        rivalAtcWeight: Double = 1.2,
        myPlayCards: MutableList<Card>? = null,
        rivalPlayCards: MutableList<Card>? = null,
    ) {
        assign()
        val newMyPlayCards =
            myPlayCards ?: let {
                val toMutableList =
                    WAR.me.playArea.cards
                        .toMutableList()
                this.myPlayArea.hero?.let { toMutableList.add(it) }
                toMutableList
            }
        val newRivalPlayCards =
            rivalPlayCards ?: let {
                val toMutableList =
                    WAR.rival.playArea.cards
                        .toMutableList()
                this.rivalPlayArea.hero?.let { toMutableList.add(it) }
                toMutableList
            }
        clean(myAtcWeight, rivalAtcWeight, newMyPlayCards, newRivalPlayCards)
    }

    fun calcPowerOrderConvert(
        cards: List<Card>,
        target: Int,
    ): Pair<Double, List<SimulateWeightCard>> = calcPowerOrder(convertToSimulateWeightCard(cards), target)

    fun calcPowerOrder(
        cards: List<SimulateWeightCard>,
        target: Int,
    ): Pair<Double, List<SimulateWeightCard>> {
        // dp[j] 表示总 cost 为 j 时的最高 (cost + weight) 值
        val dp = DoubleArray(target + 1)
        val chosenCards = Array(target + 1) { mutableListOf<SimulateWeightCard>() }

        for (card in cards) {
            for (j in target downTo card.card.cost) {
                val newTotal = dp[j - card.card.cost] + card.card.cost + card.weight
                if (newTotal > dp[j] ||
                    (newTotal == dp[j] && chosenCards[j - card.card.cost].sumOf { it.card.cost } < chosenCards[j].sumOf { it.card.cost })
                ) {
                    dp[j] = newTotal
                    chosenCards[j] = chosenCards[j - card.card.cost].toMutableList().apply { add(card) }
                }
            }
        }

        // 处理 cost 为 0 的 Card
        if (target == 0) {
            chosenCards[0] =
                cards
                    .filter { it.card.cost == 0 }
                    .sortedByDescending { it.weight }
                    .toMutableList()
        } else {
            for (card in cards) {
                if (card.card.cost == 0) {
                    for (j in target downTo 0) {
                        if (dp[j] > 0) { // 当前总 cost 大于 0 时才选择 cost 为 0 的 Card
                            chosenCards[j].add(card)
                        }
                    }
                }
            }
        }

        return Pair(dp[target], chosenCards[target].toSet().toList())
    }

    /**
     * 将[Card]转换成[SimulateWeightCard]
     */
    fun convertToSimulateWeightCard(cards: List<Card>): List<SimulateWeightCard> {
        val result = mutableListOf<SimulateWeightCard>()
        for (card in cards) {
            result.add(SimulateWeightCard(card, CARD_WEIGHT_TRIE[card.cardId]?.weight ?: 1.0))
        }
        return result
    }

    /**
     * 根据卡牌的使用权重[CardWeight.powerWeight]排序
     */
    fun sortCardByPowerWeight(cards: List<SimulateWeightCard>): List<SimulateWeightCard> {
        cards.forEach { t ->
            t.powerWeight = CARD_WEIGHT_TRIE[t.card.cardId]?.powerWeight ?: 1.0
        }
        return cards.sortedByDescending { it.powerWeight }
    }

    /**
     * 更新卡牌的文本
     */
    fun updateTextForCard(card: List<SimulateWeightCard>) {
        for (weightCard in card) {
            CardUtil.getCardText(weightCard.card.cardId)?.let {
                weightCard.text = it
            }
        }
    }

    /**
     * 出牌
     */
    fun powerCard(
        me: Player,
        rival: Player,
    ) {
        if (me.playArea.isFull) return

        val myHandCards = me.handArea.cards.toList()
        val myHandCardsCopy = myHandCards.toMutableList()
        myHandCardsCopy.removeAll { card -> card.cardType != CardTypeEnum.MINION || card.isBattlecry }

        val (score, resultCards) =
            calcPowerOrderConvert(
                myHandCardsCopy,
                me.usableResource,
            )

        val coinCard = findCoin(myHandCards)
        if (coinCard != null) {
            val (coinScore, coinResultCards) =
                calcPowerOrderConvert(
                    myHandCardsCopy,
                    me.usableResource + 1,
                )
            if (coinScore > score) {
                coinCard.action.power()
                Thread.sleep(1000)
                outCard(coinResultCards)
                return
            }
        }
        outCard(resultCards)
    }

    /**
     * 使用手牌
     */
    private fun outCard(cards: List<SimulateWeightCard>) {
        if (cards.isNotEmpty()) {
            val sortCard = sortCardByPowerWeight(cards)
            updateTextForCard(sortCard)
            log.info { "待出牌：$sortCard" }
            val me = me!!
            for (simulateWeightCard in sortCard) {
                val card = simulateWeightCard.card
                val cardType = card.cardType
                if (me.usableResource >= card.cost) {
                    if (cardType === CardTypeEnum.SPELL || cardType === CardTypeEnum.HERO) {
                        card.action.autoPower(CARD_INFO_TRIE[card.cardId])
                    } else {
                        if (me.playArea.isFull) break
                        card.action.autoPower(CARD_INFO_TRIE[card.cardId])
                    }
                }
            }
        }
    }

    /**
     * 寻找硬币卡牌
     */
    fun findCoin(cards: List<Card>): Card? = cards.find { it.isCoinCard }

    /**
     * 使用地标
     */
    fun activeLocation(cards: List<Card>) {
        cards.forEach { card ->
            if (card.cardType === CardTypeEnum.LOCATION && !card.isLocationActionCooldown) {
                CARD_INFO_TRIE[card.cardId]?.let {
                    it.powerActions.firstOrNull()?.powerExec(card, it.effectType, WAR)
                } ?: let {
                    card.action.lClick()
                }
            }
        }
    }

    fun createMCTSWar(): War =
        War()
            .apply {
                me =
                    run {
                        val player = Player(playerId = "1", gameId = "myRobot")
                        var card = Card(TestCardAction())
                        card.entityId = "0"
                        card.entityName = "myHero"
                        card.health = 30
                        card.cardType = CardTypeEnum.HERO
                        player.playArea.add(card)

                        card = Card(TestCardAction())
                        card.entityId = "1"
                        card.entityName = "myMinion1"
                        card.health = 4
                        card.atc = 3
                        card.cardType = CardTypeEnum.MINION
                        player.playArea.add(card)

                        card = Card(TestCardAction())
                        card.entityId = "2"
                        card.entityName = "myMinion2"
                        card.health = 3
                        card.atc = 5
                        card.cardType = CardTypeEnum.MINION
                        player.playArea.add(card)

                        card = Card(TestCardAction())
                        card.entityId = "3"
                        card.entityName = "myMinion3"
                        card.health = 5
                        card.atc = 4
//                card.isWindFury = true
//                card.isMegaWindfury = true
                        card.cardType = CardTypeEnum.MINION
                        player.playArea.add(card)

                        card = Card(TestCardAction())
                        card.entityId = "4"
                        card.entityName = "myMinion4"
                        card.health = 2
                        card.atc = 3
                        card.cardType = CardTypeEnum.MINION
                        player.playArea.add(card)

                        card = Card(TestCardAction())
                        card.entityId = "11"
                        card.entityName = "myHand1"
                        card.health = 2
                        card.atc = 3
                        card.cost = 2
                        card.cardType = CardTypeEnum.MINION
                        player.handArea.add(card)

                        card = Card(TestCardAction())
                        card.entityId = "12"
                        card.entityName = "myHand2"
                        card.health = 2
                        card.atc = 3
                        card.cost = 3
                        card.cardType = CardTypeEnum.MINION
                        player.handArea.add(card)

                        card = Card(TestCardAction())
                        card.entityId = "13"
                        card.entityName = "myHand3"
                        card.cost = 1
                        card.cardType = CardTypeEnum.SPELL
                        player.handArea.add(card)

                        player.resources = 6

                        player
                    }

                player1 = me

                rival =
                    run {
                        val player = Player(playerId = "2", gameId = "rivalRobot")
                        var card = Card(TestCardAction())
                        card.entityId = "20"
                        card.entityName = "rivalHero"
                        card.health = 30
//                card.atc = 5
                        card.cardType = CardTypeEnum.HERO
                        player.playArea.add(card)

                        card = Card(TestCardAction())
                        card.entityId = "21"
                        card.entityName = "rivalMinion1"
                        card.health = 4
                        card.atc = 5
//                card.isTaunt = true
                        card.cardType = CardTypeEnum.MINION
                        player.playArea.add(card)

                        card = Card(TestCardAction())
                        card.entityId = "22"
                        card.entityName = "rivalMinion2"
                        card.health = 4
                        card.atc = 3
                        card.cardType = CardTypeEnum.MINION
                        player.playArea.add(card)

                        card = Card(TestCardAction())
                        card.entityId = "23"
                        card.entityName = "rivalMinion3"
                        card.health = 2
                        card.atc = 3
                        card.cardType = CardTypeEnum.MINION
                        player.playArea.add(card)

                        card = Card(TestCardAction())
                        card.entityId = "31"
                        card.entityName = "rivalHand1"
                        card.cost = 1
                        card.cardType = CardTypeEnum.SPELL
                        player.handArea.add(card)

                        player2 = rival

                        player
                    }
            }.clone()

    fun convertToSimulateCard(cards: List<Card>): MutableList<SimulateWeightCard> {
        val res = mutableListOf<SimulateWeightCard>()
        for (card in cards) {
            val cardWeight =
                CARD_WEIGHT_TRIE.getOrDefault(card.cardId) { CardWeight(1.0, 1.0, if (card.cost > 2) -1.0 else 0.0) }
            res.add(SimulateWeightCard(card, cardWeight.weight, cardWeight.powerWeight, cardWeight.changeWeight))
        }
        return res
    }

}
