package club.xiaojiawei.bean

import club.xiaojiawei.bean.area.DeckArea
import club.xiaojiawei.bean.area.HandArea
import club.xiaojiawei.bean.area.PlayArea
import club.xiaojiawei.bean.area.SecretArea
import club.xiaojiawei.data.CARD_WEIGHT_TRIE
import club.xiaojiawei.enums.CardTypeEnum
import kotlin.math.log
import kotlin.math.max

/**
 * 战局评分器
 * @author 肖嘉威
 * @date 2025/1/22 16:31
 */
private const val BASIC_RATIO = 1.2
private const val RESOURCES_VALUE = 3.0 * BASIC_RATIO
private const val USED_RESOURCES_VALUE = 0.9
private const val DEATH_RATTLE_VALUE = -0.2 * BASIC_RATIO
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
    fun build(): ScoreCalculator =
        ScoreCalculator { war ->
            calcPlayerScore(war.me, true) - calcPlayerScore(war.rival, false)
        }

    open fun calcPlayerScore(
        player: Player,
        isMe: Boolean,
    ): Double =
        if (player.playArea.hero?.isDead() == true) Int.MIN_VALUE.toDouble() else {
            calcHandScore(player.handArea, isMe) + calcPlayScore(player.playArea, isMe) +
                    calcDeckScore(player.deckArea, isMe) + calcSecretScore(player.secretArea, isMe) +
                    calcResourcesScore(
                        player,
                        isMe,
                    )
        }


    open fun calcResourcesScore(
        player: Player,
        isMe: Boolean,
    ): Double =
        (player.tempResources + player.resources) * RESOURCES_VALUE + player.usedResources * USED_RESOURCES_VALUE

    open fun calcSecretScore(
        area: SecretArea,
        isMe: Boolean,
    ): Double = area.cards.sumOf { card -> card.cost }.toDouble()

    open fun calcDeckScore(
        area: DeckArea,
        isMe: Boolean,
    ): Double {
//        通过对数计算牌库得分，牌库数量较多时加牌对分数影响不大，但数量较低时加牌对分数影响较大
        return log(area.cardSize() + 1.0, 2.0) * 10.0
    }

    open fun calcHandScore(
        area: HandArea,
        isMe: Boolean,
    ): Double =
        area.cards.sumOf { card ->
            if (card.cardType === CardTypeEnum.SPELL) {
                (card.cost shl 1).toDouble()
            } else {
                calcPlayCardScore(card, isMe) * 0.4
            }
        }

    open fun calcPlayScore(
        area: PlayArea,
        isMe: Boolean,
    ): Double {
        var score = 0.0
        score += area.cards.sumOf { calcPlayCardScore(it, isMe) }
        area.hero?.let { hero ->
            score += calcPlayCardScore(hero, isMe)
        }
        area.weapon?.let { weapon ->
            score += calcPlayCardScore(weapon, isMe) * 0.6
        }
        return score
    }

    open fun calcPlayCardScore(
        card: Card,
        isMe: Boolean,
    ): Double {
        if (card.isAlive()) {
            val cardRatio = CARD_WEIGHT_TRIE[card.cardId]?.weight ?: 1.0
            val atc = max(card.atc, 0).toDouble()
            val blood = max(card.blood(), 0)
            val ration: Double =
                if (blood == 0) {
                    if (card.cardType === CardTypeEnum.HERO) {
                        BASIC_RATIO
                    } else {
                        1.0
                    }
                } else {
                    BASIC_RATIO
                }
            val basicScore =
                atc * ration + blood * (
                        if (card.cardType === CardTypeEnum.HERO) {
                            0.5
                        } else if (card.cardType === CardTypeEnum.LOCATION) {
                            card.cost / card.bloodLimit().toDouble()
                        } else {
                            1.0
                        }
                        )
            var totalScore: Double = basicScore
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
