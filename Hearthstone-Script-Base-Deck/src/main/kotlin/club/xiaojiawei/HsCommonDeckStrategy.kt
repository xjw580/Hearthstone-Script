package club.xiaojiawei

import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.Player
import club.xiaojiawei.bean.SimulateWeightCard
import club.xiaojiawei.config.log
import club.xiaojiawei.enums.CardTypeEnum
import club.xiaojiawei.enums.RunModeEnum
import club.xiaojiawei.status.War
import club.xiaojiawei.status.War.me
import club.xiaojiawei.util.DeckStrategyUtil
import club.xiaojiawei.util.DeckStrategyUtil.sortCard

/**
 * @author 肖嘉威
 * @date 2024/9/8 14:56
 */
class HsCommonDeckStrategy : DeckStrategy() {

    override fun name(): String {
        return "基础策略"
    }

    override fun getRunMode(): Array<RunModeEnum> {
        return arrayOf(RunModeEnum.CASUAL, RunModeEnum.STANDARD, RunModeEnum.WILD, RunModeEnum.PRACTICE)
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

//        使用技能
        me.playArea.power?.let {
            if (me.usableResource >= it.cost || it.cost == 0) {
                it.action.power()
            }
        }
        DeckStrategyUtil.cleanPlay()
    }

    private fun powerCard(me: Player, rival: Player) {
        if (me.playArea.isFull) return

        val myHandCards = me.handArea.cards.toList()
        val myHandCardsCopy = myHandCards.toMutableList()
        myHandCardsCopy.removeAll { card -> card.cardType != CardTypeEnum.MINION || card.isBattlecry }

        val (_, resultCards) = DeckStrategyUtil.calcPowerOrderConvert(
            myHandCardsCopy, me.usableResource
        )

        val coinCard = findCoin(myHandCards)
        if (coinCard != null) {
            val (num1, resultCards1) = DeckStrategyUtil.calcPowerOrderConvert(
                myHandCardsCopy, me.usableResource + 1
            )
            if (num1 > me.usableResource) {
                coinCard.action.power()
                outCard(resultCards1)
                return
            }
        }
        outCard(resultCards)
    }

    fun outCard(cards: List<SimulateWeightCard>) {
        if (cards.isNotEmpty()) {
            var sortCard = sortCard(cards)
            log.info { "待出牌：$sortCard" }
            for (simulateWeightCard in sortCard) {
                if (me.playArea.isFull) break
                simulateWeightCard.card.action.power()
            }
        }
    }

    private fun findCoin(cards: List<Card>): Card? {
        return cards.find { it.isCoinCard }
    }

    override fun executeDiscoverChooseCard(vararg cards: Card): Int {
        return 1
    }
}