package club.xiaojiawei.util

import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.TEST_CARD_ACTION
import club.xiaojiawei.enums.CardTypeEnum
import kotlin.test.Test

/**
 * @author 肖嘉威
 * @date 2025/6/10 17:07
 */
class DeckStrategyUtilTest {

    @Test
    fun testCleanPlay() {
        DeckStrategyUtil.execAction = false
        val myCards = mutableListOf<Card>(
            Card(TEST_CARD_ACTION).apply {
                cardType = CardTypeEnum.MINION
                entityId = "m1"
                isPoisonous = true
                atc = 1
                health = 3
            },
            Card(TEST_CARD_ACTION).apply {
                cardType = CardTypeEnum.MINION
                entityId = "m2"
                atc = 3
                health = 3
            },
            Card(TEST_CARD_ACTION).apply {
                cardType = CardTypeEnum.HERO
                entityId = "mh1"
                atc = 0
                health = 30
            },
        )
        val rivalCards = mutableListOf<Card>(
            Card(TEST_CARD_ACTION).apply {
                cardType = CardTypeEnum.MINION
                entityId = "r1"
                atc = 3
                health = 10
            },
            Card(TEST_CARD_ACTION).apply {
                cardType = CardTypeEnum.MINION
                entityId = "r2"
                atc = 2
                health = 3
            },
            Card(TEST_CARD_ACTION).apply {
                cardType = CardTypeEnum.HERO
                entityId = "rh1"
                atc = 0
                health = 30
            },
        )
        DeckStrategyUtil.cleanPlay(myPlayCards = myCards, rivalPlayCards = rivalCards)
        DeckStrategyUtil.execAction = true
    }

}