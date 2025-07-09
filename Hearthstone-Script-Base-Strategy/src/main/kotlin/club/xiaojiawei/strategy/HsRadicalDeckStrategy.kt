package club.xiaojiawei.strategy

import club.xiaojiawei.DeckStrategy
import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.isValid
import club.xiaojiawei.config.log
import club.xiaojiawei.data.CARD_INFO_TRIE
import club.xiaojiawei.enums.CardTypeEnum
import club.xiaojiawei.enums.RunModeEnum
import club.xiaojiawei.status.WAR
import club.xiaojiawei.util.DeckStrategyUtil

/**
 * @author 肖嘉威
 * @date 2024/10/17 17:58
 */
class HsRadicalDeckStrategy : DeckStrategy() {
    private val commonDeckStrategy = HsCommonDeckStrategy()

    override fun name(): String = "激进策略"

    override fun description(): String = "会在基础策略的基础上使用战吼，法术，地标牌（依旧不识别战吼或法术）"

    override fun getRunMode(): Array<RunModeEnum> =
        arrayOf(RunModeEnum.CASUAL, RunModeEnum.STANDARD, RunModeEnum.WILD, RunModeEnum.PRACTICE)

    override fun deckCode(): String = ""

    override fun id(): String = "e71234fa-1-radical-deck-97e9-1f4e126cd33b"

    override fun referWeight(): Boolean = true

    override fun referPowerWeight(): Boolean = true

    override fun referChangeWeight(): Boolean = true

    override fun referCardInfo(): Boolean = true

    override fun executeChangeCard(cards: HashSet<Card>) {
        commonDeckStrategy.executeChangeCard(cards)
    }

    override fun executeOutCard() {
        val me = WAR.me
        if (me.isValid()) {
            val rival = WAR.rival
            var plays = me.playArea.cards.toList()
            DeckStrategyUtil.activeLocation(plays)
            val hands = me.handArea.cards.toList()
            val (_, resultCards) = DeckStrategyUtil.calcPowerOrderConvert(hands, me.usableResource)
            if (resultCards.isNotEmpty()) {
                DeckStrategyUtil.updateTextForCard(resultCards)
                val sortCard = DeckStrategyUtil.sortCardByPowerWeight(resultCards)
                log.info { "待出牌：$sortCard" }
                for (simulateWeightCard in sortCard) {
                    val card = simulateWeightCard.card
                    val cardText = simulateWeightCard.text
                    if (me.usableResource >= card.cost) {
                        if (card.cardType === CardTypeEnum.SPELL || card.cardType === CardTypeEnum.HERO) {
                            card.action.autoPower(CARD_INFO_TRIE[card.cardId])
                        } else {
                            if (me.playArea.isFull) break
                            card.action.autoPower(CARD_INFO_TRIE[card.cardId])
                        }
                    }
                }
            }
            plays = me.playArea.cards.toList()
            DeckStrategyUtil.activeLocation(plays)
            commonDeckStrategy.executeOutCard()
        }
    }

    override fun executeDiscoverChooseCard(vararg cards: Card): Int =
        commonDeckStrategy.executeDiscoverChooseCard(*cards)
}
