package club.xiaojiawei.util

import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.Player
import club.xiaojiawei.bean.TestCardAction
import club.xiaojiawei.data.CARD_WEIGHT_TRIE
import club.xiaojiawei.enums.CardTypeEnum
import club.xiaojiawei.status.War
import kotlin.math.max

/**
 * @author 肖嘉威
 * @date 2025/1/14 9:17
 */
object WarUtil {

    fun createMCTSWar(): War {
        return War().apply {
            me = run {
                val player = Player("1", "myRobot")
                var card = Card(TestCardAction())
                card.entityId = "0"
                card.entityName = "我方英雄"
                card.health = 20
                card.cardType = CardTypeEnum.HERO
                player.playArea.add(card)

                card = Card(TestCardAction())
                card.entityId = "1"
                card.entityName = "我方随从1"
                card.health = 4
                card.atc = 4
                card.cardType = CardTypeEnum.MINION
                player.playArea.add(card)

//                card = Card(TestCardAction())
//                card.entityId = "2"
//                card.entityName = "我方随从2"
//                card.health = 3
//                card.atc = 5
//                card.cardType = CardTypeEnum.MINION
//                player.playArea.add(card)
//
//                card = Card(TestCardAction())
//                card.entityId = "3"
//                card.entityName = "我方随从3"
//                card.health = 7
//                card.atc = 3
//                card.cardType = CardTypeEnum.MINION
//                player.playArea.add(card)
//
//                card = Card(TestCardAction())
//                card.entityId = "4"
//                card.entityName = "我方随从4"
//                card.health = 2
//                card.atc = 5
//                card.cardType = CardTypeEnum.MINION
//                player.playArea.add(card)

                player
            }

            player1 = me

            rival = run {
                val player = Player("2", "rivalRobot")
                var card = Card(TestCardAction())
                card.entityId = "0"
                card.entityName = "敌方英雄"
                card.health = 10
                card.cardType = CardTypeEnum.HERO
                player.playArea.add(card)

//                card = Card(TestCardAction())
//                card.entityId = "1"
//                card.entityName = "敌方随从1"
//                card.health = 2
//                card.atc = 4
//                card.cardType = CardTypeEnum.MINION
//                player.playArea.add(card)

                card = Card(TestCardAction())
                card.entityId = "2"
                card.entityName = "敌方随从2"
                card.health = 2
                card.atc = 2
                card.cardType = CardTypeEnum.MINION
                player.playArea.add(card)

//        card = Card(TestCardAction())
//        card.entityId = "3"
//        card.entityName = "敌方随从3"
//        card.health = 5
//        card.atc = 6
//        card.cardType = CardTypeEnum.MINION
//        player.playArea.add(card)

                player2 = rival

                player
            }
        }
    }

    fun isEnd(war: War): Boolean {
        war.rival.playArea.hero?.let { rivalHero ->
            if (rivalHero.blood() <= 0) return true
            war.me.playArea.hero?.let { myHero ->
                return myHero.blood() <= 0
            }
        }
        return true
    }

    fun calcScore(war: War): Double {
        return calcPlayerScore(war.me) - calcPlayerScore(war.rival)
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

    private fun calcCardScore(card: Card): Double {
        if (card.isSurvival()) {
            val ratio = CARD_WEIGHT_TRIE[card.cardId]?.weight ?: 1.0
            val atc = max(card.atc, 0).toDouble()
            val blood = max(card.blood(), 0)
            val basicScore = atc * blood + (card.blood() shr 1)
            val heroScore = if (card.cardType === CardTypeEnum.HERO) Int.MAX_VALUE.toDouble() else 0.0
            var totalScore: Double = basicScore + heroScore
            if (card.isDeathRattle) {
                totalScore -= 0.3
            }
            if (card.isTaunt) {
                totalScore += 1
            }
            if (card.isAdjacentBuff) {
                totalScore += 2
            }
            if (card.isAura) {
                totalScore += 2
            }
            if (card.isWindFury) {
                totalScore += 0.5 * atc
            }
            if (card.isMegaWindfury) {
                totalScore += 0.9 * atc
            }
            if (card.isTitan) {
                totalScore += 8
            }
            if (card.isTriggerVisual) {
                totalScore += 0.8
            }
            totalScore += card.spellPower * 1
            return totalScore * ratio
        }
        return 0.0
    }

}