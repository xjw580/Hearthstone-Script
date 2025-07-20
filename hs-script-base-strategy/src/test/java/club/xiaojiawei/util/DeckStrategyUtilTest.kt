package club.xiaojiawei.util

import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.TEST_CARD_ACTION
import club.xiaojiawei.enums.CardTypeEnum
import club.xiaojiawei.hsscriptbasestrategy.util.DeckStrategyUtil
import org.junit.jupiter.api.Disabled
import kotlin.test.Test

/**
 * @author 肖嘉威
 * @date 2025/6/10 17:07
 */
class DeckStrategyUtilTest {

    private fun buildPoisonousMinio(entityId: String): Card {
        return Card(TEST_CARD_ACTION).apply {
            cardType = CardTypeEnum.MINION
            this.entityId = entityId
            isPoisonous = true
            atc = 2
            health = 4
            isTaunt = true
            isDeathRattle = true
        }
    }


    /**
     * 测试剧毒随从的解场倾向
     */
    @Test
    @Disabled("这个测试无法验证结果")
    fun testPoisonousCleanPlay() {
        DeckStrategyUtil.execAction = false
        val myCards = mutableListOf<Card>(
            buildPoisonousMinio("m1"),
            buildPoisonousMinio("m2"),
            Card(TEST_CARD_ACTION).apply {
                cardType = CardTypeEnum.MINION
                entityId = "m3"
                atc = 3
                health = 5
                isTaunt = true
            },
            Card(TEST_CARD_ACTION).apply {
                cardType = CardTypeEnum.MINION
                entityId = "m4"
                atc = 0
                health = 2
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
                atc = 8
                health = 8
            },
            Card(TEST_CARD_ACTION).apply {
                cardType = CardTypeEnum.MINION
                entityId = "r2"
                atc = 4
                health = 3
            },
            Card(TEST_CARD_ACTION).apply {
                cardType = CardTypeEnum.MINION
                entityId = "r3"
                atc = 5
                health = 4
            },
            Card(TEST_CARD_ACTION).apply {
                cardType = CardTypeEnum.MINION
                entityId = "r3"
                atc = 2
                health = 2
            },
            Card(TEST_CARD_ACTION).apply {
                cardType = CardTypeEnum.MINION
                entityId = "r4"
                atc = 2
                health = 2
                isDeathRattle = true
            },
            Card(TEST_CARD_ACTION).apply {
                cardType = CardTypeEnum.MINION
                entityId = "r5"
                atc = 1
                health = 1
            },
            Card(TEST_CARD_ACTION).apply {
                cardType = CardTypeEnum.HERO
                entityId = "rh1"
                atc = 0
                health = 28
            },
        )
        DeckStrategyUtil.cleanPlay(myPlayCards = myCards, rivalPlayCards = rivalCards)
        DeckStrategyUtil.execAction = true
    }

}