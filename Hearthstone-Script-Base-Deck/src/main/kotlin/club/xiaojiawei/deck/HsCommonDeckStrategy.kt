package club.xiaojiawei.deck

import club.xiaojiawei.DeckStrategy
import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.Player
import club.xiaojiawei.enums.CardTypeEnum
import club.xiaojiawei.enums.RunModeEnum
import club.xiaojiawei.status.WAR
import club.xiaojiawei.util.DeckStrategyUtil
import club.xiaojiawei.util.DeckStrategyUtil.findCoin
import club.xiaojiawei.util.DeckStrategyUtil.outCard

/**
 * @author 肖嘉威
 * @date 2024/9/8 14:56
 */
class HsCommonDeckStrategy : DeckStrategy() {

    override fun name(): String {
        return "基础策略"
    }

    override fun description(): String {
        return "未对卡牌和卡组适配，自行组一套无战吼无法术的套牌即可。参考权重设置"
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
        val me = WAR.me
        val rival = WAR.rival

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

        me.playArea.cards.toTypedArray().forEach { card: Card ->
            if (card.isLaunchpad && me.usableResource >= card.launchCost()) {
                card.action.launch()
            }
        }
    }

    private fun powerCard(me: Player, rival: Player) {
        if (me.playArea.isFull) return

        val myHandCards = me.handArea.cards.toList()
        val myHandCardsCopy = myHandCards.toMutableList()
        myHandCardsCopy.removeAll { card -> card.cardType != CardTypeEnum.MINION || card.isBattlecry }

        val (num, resultCards) = DeckStrategyUtil.calcPowerOrderConvert(
            myHandCardsCopy, me.usableResource
        )

        val coinCard = findCoin(myHandCards)
        if (coinCard != null) {
            val (num1, resultCards1) = DeckStrategyUtil.calcPowerOrderConvert(
                myHandCardsCopy, me.usableResource + 1
            )
            if (num1 > num) {
                coinCard.action.power()
                Thread.sleep(1000)
                outCard(resultCards1)
                return
            }
        }
        outCard(resultCards)
    }

    override fun executeDiscoverChooseCard(vararg cards: Card): Int {
        return 1
    }
}