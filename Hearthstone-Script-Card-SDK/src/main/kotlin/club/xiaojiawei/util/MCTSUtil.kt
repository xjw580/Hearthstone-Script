package club.xiaojiawei.util

import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.Player
import club.xiaojiawei.bean.TestCardAction
import club.xiaojiawei.data.CARD_WEIGHT_TRIE
import club.xiaojiawei.enums.CardTypeEnum
import club.xiaojiawei.status.War
import java.util.function.Function
import kotlin.math.max

/**
 * @author 肖嘉威
 * @date 2025/1/15 11:22
 */
object MCTSUtil {

    fun isEnd(war: War): Boolean {
        war.rival.playArea.hero?.let { rivalHero ->
            if (rivalHero.blood() <= 0) return true
            war.me.playArea.hero?.let { myHero ->
                return myHero.blood() <= 0
            }
        }
        return true
    }

    fun buildScoreCalculator(): Function<War, Double> {
        return Function { war ->
            calcPlayerScore(war.me) - calcPlayerScore(war.rival)
        }
    }

    private fun calcPlayerScore(player: Player): Double {
        var score = 0.0
        player.playArea.cards.forEach { card ->
            score += calcCardScore(card)
        }
        player.playArea.hero?.let { hero ->
            score += calcCardScore(hero)
        }
        player.playArea.weapon?.let { weapon ->
            score += calcCardScore(weapon) * 0.8
        }
        return score
    }

    private const val BASIC_RATIO = 1.2
    private const val DEATH_RATTLE_VALUE = -0.3 * BASIC_RATIO
    private const val TAUNT_VALUE = 1 * BASIC_RATIO
    private const val ADJACENTBUFF_VALUE = 2 * BASIC_RATIO
    private const val AURA_VALUE = 2 * BASIC_RATIO
    private const val WINDFURY_VALUE = 0.5 * BASIC_RATIO
    private const val MEGAWINDFURY_VALUE = 0.9 * BASIC_RATIO
    private const val TITAN_VALUE = 8 * BASIC_RATIO
    private const val TRIGGERVISUAL_VALUE = 0.8 * BASIC_RATIO

    private fun calcCardScore(card: Card): Double {
        if (card.isSurvival()) {
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
            val basicScore = atc * ration + blood
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
            totalScore += card.spellPower * 1
            return totalScore * cardRatio
        }
        return 0.0
    }

}