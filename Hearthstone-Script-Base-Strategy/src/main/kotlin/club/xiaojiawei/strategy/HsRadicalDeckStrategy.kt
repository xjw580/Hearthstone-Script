package club.xiaojiawei.strategy

import club.xiaojiawei.DeckStrategy
import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.isValid
import club.xiaojiawei.config.log
import club.xiaojiawei.enums.CardTypeEnum
import club.xiaojiawei.enums.RunModeEnum
import club.xiaojiawei.status.WAR
import club.xiaojiawei.util.DeckStrategyUtil
import club.xiaojiawei.util.DeckStrategyUtil.activeLocation
import club.xiaojiawei.util.DeckStrategyUtil.isDamageText
import club.xiaojiawei.util.isFalse
import club.xiaojiawei.util.isTrue

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

    override fun executeChangeCard(cards: HashSet<Card>) {
        commonDeckStrategy.executeChangeCard(cards)
    }

    override fun executeOutCard() {
        val me = WAR.me
        if (me.isValid()) {
            val rival = WAR.rival
            var plays = me.playArea.cards.toList()
            activeLocation(plays)
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
                        if (card.cardType === CardTypeEnum.SPELL) {
                            if (isDamageText(cardText)) {
                                log.info { "[${card.cardId}]判断为伤害法术" }
                                rival.playArea.cards.find { c -> c.canBeTargetedByMe() }?.let {
                                    card.action.power(it)
                                } ?: let {
                                    if (rival.playArea.hero?.canBeTargetedByMe() == true) {
                                        card.action.power(rival.playArea.hero)
                                    }
                                }
                            } else {
                                me.playArea.cards.find { c -> c.canBeTargetedByMe() }?.let {
                                    card.action.power(it)
                                } ?: let {
                                    card.action.power()
                                }
                            }
                        } else {
                            if (me.playArea.isFull) break
                            card.isBattlecry
                                .isTrue {
                                    me.playArea.cards.find { card -> card.cardType === CardTypeEnum.MINION }?.let {
                                        if (card.action.executedPower) {
                                            card.action.power(it, false)?.pointTo(it)
                                        } else {
                                            card.action.power(it)
                                        }
                                    } ?: let {
                                        card.action.power()
                                    }
                                }.isFalse {
                                    card.action.power()
                                }
                        }
                    }
                }
            }
            plays = me.playArea.cards.toList()
            activeLocation(plays)
            commonDeckStrategy.executeOutCard()
        }
    }

    override fun executeDiscoverChooseCard(vararg cards: Card): Int = commonDeckStrategy.executeDiscoverChooseCard(*cards)
}
