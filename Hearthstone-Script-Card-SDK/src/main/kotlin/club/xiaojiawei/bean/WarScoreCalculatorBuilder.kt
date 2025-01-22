package club.xiaojiawei.bean

import club.xiaojiawei.bean.area.DeckArea
import club.xiaojiawei.bean.area.HandArea
import club.xiaojiawei.bean.area.PlayArea
import club.xiaojiawei.bean.area.SecretArea
import club.xiaojiawei.data.CARD_WEIGHT_TRIE
import club.xiaojiawei.enums.CardTypeEnum
import java.util.function.Function
import kotlin.math.log
import kotlin.math.max

/**
 * 战局评分器
 * @author 肖嘉威
 * @date 2025/1/22 16:31
 */
private const val BASIC_RATIO = 1.2
private const val RESOURCES_VALUE = 3.0 * BASIC_RATIO
private const val USABLE_RESOURCES_VALUE = 0.1 * BASIC_RATIO
private const val DEATH_RATTLE_VALUE = -0.3 * BASIC_RATIO
private const val TAUNT_VALUE = 1 * BASIC_RATIO
private const val ADJACENTBUFF_VALUE = 2 * BASIC_RATIO
private const val AURA_VALUE = 2 * BASIC_RATIO
private const val WINDFURY_VALUE = 0.5 * BASIC_RATIO
private const val MEGAWINDFURY_VALUE = 0.9 * BASIC_RATIO
private const val TITAN_VALUE = 8 * BASIC_RATIO
private const val TRIGGERVISUAL_VALUE = 0.8 * BASIC_RATIO
private const val LIFESTEAL_VALUE = 0.5 * BASIC_RATIO
private const val REBORN_VALUE = DEATH_RATTLE_VALUE
private const val ISDIVINESHIELD_VALUE = 0.5 * BASIC_RATIO

val DEFAULT_WAR_SCORE_CALCULATOR by lazy { WarScoreCalculatorBuilder() }

open class WarScoreCalculatorBuilder {

    fun build(): Function<War, Double> {
        return Function<War, Double> { war ->
            calcPlayerScore(war.me) - calcPlayerScore(war.rival)
        }
    }

    protected open fun calcPlayerScore(player: Player): Double {
        return calcHandScore(player.handArea) + calcPlayScore(player.playArea) +
                calcDeckScore(player.deckArea) + calcSecretScore(player.secretArea) + calcResourcesScore(player)
    }

    protected open fun calcResourcesScore(player: Player): Double {
        return (player.tempResources + player.resources) * RESOURCES_VALUE + player.usableResource * USABLE_RESOURCES_VALUE
    }

    protected open fun calcSecretScore(area: SecretArea): Double {
        return area.cards.sumOf { card -> card.cost }.toDouble()
    }

    protected open fun calcDeckScore(area: DeckArea): Double {
//        通过对数计算牌库得分，牌库数量较多时加牌对分数影响不大，但数量较低时加牌对分数影响较大
        return log(area.cardSize() + 1.0, 2.0) * 10.0
    }

    protected open fun calcHandScore(area: HandArea): Double {
        return area.cards.sumOf { card ->
            if (card.cardType === CardTypeEnum.SPELL) {
                (card.cost shl 1).toDouble()
            } else {
                calcPlayCardScore(card) * 0.4
            }
        }
    }

    protected open fun calcPlayScore(area: PlayArea): Double {
        var score = 0.0
        area.cards.forEach { card ->
            score += calcPlayCardScore(card)
        }
        area.hero?.let { hero ->
            score += calcPlayCardScore(hero)
        }
        area.weapon?.let { weapon ->
            score += calcPlayCardScore(weapon) * 0.6
        }
        return score
    }

    protected open fun calcPlayCardScore(card: Card): Double {
        if (card.isAlive()) {
            val cardRatio = CARD_WEIGHT_TRIE[card.cardId]?.weight ?: 1.0
            val atc = max(card.atc, 0).toDouble()
            val blood = max(card.blood(), 0)
            val ration: Double = if (blood == 0) {
                if (card.cardType === CardTypeEnum.HERO) {
                    BASIC_RATIO
                } else {
                    1.0
                }
            } else {
                BASIC_RATIO
            }
            val basicScore =
                atc * ration + blood * (if (card.cardType === CardTypeEnum.HERO) 0.5 else 1.0) + if (card.cardType === CardTypeEnum.LOCATION) card.cost else 0
            val heroScore = if (card.cardType === CardTypeEnum.HERO) Int.MAX_VALUE.toDouble() else 0.0
            var totalScore: Double = basicScore + heroScore
            if (card.isDeathRattle) {
                totalScore += DEATH_RATTLE_VALUE
            }
            if (card.isTaunt) {
                totalScore += TAUNT_VALUE
            }
            if (card.isAdjacentBuff) {
                totalScore += ADJACENTBUFF_VALUE
            }
            if (card.isAura) {
                totalScore += AURA_VALUE
            }
            if (card.isWindFury) {
                totalScore += WINDFURY_VALUE * atc
            }
            if (card.isMegaWindfury) {
                totalScore += MEGAWINDFURY_VALUE * atc
            }
            if (card.isTitan) {
                totalScore += TITAN_VALUE
            }
            if (card.isTriggerVisual) {
                totalScore += TRIGGERVISUAL_VALUE
            }
            if (card.isLifesteal) {
                totalScore += LIFESTEAL_VALUE * atc
            }
            if (card.isReborn) {
                totalScore += REBORN_VALUE
            }
            if (card.isDivineShield) {
                totalScore += ISDIVINESHIELD_VALUE * atc
            }
            totalScore += card.spellPower * 1
            return totalScore * cardRatio
        }
        return 0.0
    }

}