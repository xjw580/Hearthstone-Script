package club.xiaojiawei.deck.spetial

import club.xiaojiawei.DeckStrategy
import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.isInValid
import club.xiaojiawei.deck.HsCommonDeckStrategy
import club.xiaojiawei.enums.RunModeEnum
import club.xiaojiawei.status.War
import club.xiaojiawei.util.DeckStrategyUtil

/**
 * 沙包战
 * @author 肖嘉威
 * @date 2024/10/17 17:58
 */
class HsSandbagWarriorDeckStrategy : DeckStrategy() {

    private val commonDeckStrategy = HsCommonDeckStrategy()

    override fun name(): String {
        return "沙包战"
    }

    override fun getRunMode(): Array<RunModeEnum> {
        return arrayOf(RunModeEnum.CASUAL, RunModeEnum.STANDARD, RunModeEnum.WILD, RunModeEnum.PRACTICE)
    }

    override fun deckCode(): String {
        return "AAECAQcCo9QE5eYGDp6fBJ+fBLSfBIagBIigBImgBI7UBJDUBJzUBJ/UBLT4BbX4Bb+iBt3zBgAA"
    }

    override fun id(): String {
        return "e71234fa-sandbag-deck-97e9-1f4e126cd33b"
    }

    override fun executeChangeCard(cards: HashSet<Card>) {
        commonDeckStrategy.executeChangeCard(cards)
    }

    override fun executeOutCard() {
        val me = War.me
        val rival = War.rival
        if (me.isInValid() || rival.isInValid()) return
        var myPlayCards = me.playArea.cards.toMutableList()
        var rivalPlayCards = rival.playArea.cards.toMutableList()
        DeckStrategyUtil.cleanPlay(myPlayCards = myPlayCards, rivalPlayCards = rivalPlayCards)

        rivalPlayCards = rival.playArea.cards.toMutableList()
    }

    override fun executeDiscoverChooseCard(vararg cards: Card): Int {
        return commonDeckStrategy.executeDiscoverChooseCard(*cards)
    }
}