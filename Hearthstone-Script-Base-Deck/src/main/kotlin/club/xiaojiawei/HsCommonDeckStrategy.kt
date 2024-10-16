package club.xiaojiawei

import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.MagePower
import club.xiaojiawei.bean.Player
import club.xiaojiawei.bean.PriestPower
import club.xiaojiawei.bean.abs.PointPower
import club.xiaojiawei.config.log
import club.xiaojiawei.enums.CardTypeEnum
import club.xiaojiawei.enums.RunModeEnum
import club.xiaojiawei.status.War
import club.xiaojiawei.util.DeckStrategyUtil

/**
 * @author 肖嘉威
 * @date 2024/9/8 14:56
 */
class HsCommonDeckStrategy : DeckStrategy() {

    override fun name(): String {
        return "基础策略"
    }

    override fun getRunMode(): Array<RunModeEnum> {
        return arrayOf(RunModeEnum.CASUAL, RunModeEnum.STANDARD, RunModeEnum.WILD)
    }

    override fun deckCode(): String {
        return ""
    }

    override fun id(): String {
        return "e71234fa-base-deck-97e9-1f4e126cd33b"
    }

    override fun executeChangeCard(cards: HashSet<Card>) {
        cards.removeIf { card -> card.cost > 2 }
    }

    override fun executeOutCard() {
        val me = War.me
        val rival = War.rival

        powerCard(me, rival)

        DeckStrategyUtil.cleanPlay()

        powerCard(me, rival)

        me.playArea.power?.let {
            if (!me.playArea.isFull && me.usableResource >= it.cost) {
                if (it.action is PointPower) {
                    if (it.action is MagePower) {
                        it.action.power(false)?.pointTo(rival.playArea.hero)
                    } else if (it.action is PriestPower) {
                        it.action.power(false)?.pointTo(me.playArea.hero)
                    }
                } else {
                    it.action.power()
                }
                DeckStrategyUtil.cleanPlay()
            }
        }
    }

    private fun powerCard(me: Player, rival: Player) {
        if (me.playArea.isFull) return

        val cards = me.handArea.cards.toList()
        val toMutableList = cards.toMutableList()
        toMutableList.removeAll { card -> card.cardType != CardTypeEnum.MINION || card.isBattlecry }

        val (num, resultCards) = DeckStrategyUtil.calcPowerOrder(toMutableList, me.usableResource)

        val coinCard = findCoin(cards)
        if (coinCard != null) {
            val (num1, resultCards1) = DeckStrategyUtil.calcPowerOrder(toMutableList, me.usableResource + 1)
            if (num1 > me.usableResource) {
                coinCard.action.power()
                if (resultCards1.isNotEmpty()) {
                    log.info { resultCards }
                }
                for (card in resultCards1) {
                    if (me.playArea.isFull) break
                    card.action.power()
                }
                return
            }
        }
        if (resultCards.isNotEmpty()) {
            log.info { resultCards }
        }
        for (card in resultCards) {
            if (me.playArea.isFull) break
            card.action.power()
        }
    }

    private fun findCoin(cards: List<Card>): Card? {
        return cards.find { it.isCoinCard }
    }

    override fun executeDiscoverChooseCard(vararg cards: Card): Int {
        return 0
    }
}