package club.xiaojiawei.strategy.spetial

import club.xiaojiawei.DeckStrategy
import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.Player
import club.xiaojiawei.bean.isInValid
import club.xiaojiawei.strategy.HsCommonDeckStrategy
import club.xiaojiawei.enums.RunModeEnum
import club.xiaojiawei.status.WAR
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
        return "AAECAQcC4+YG5eYGDp6fBJ+fBLSfBIagBIigBImgBI7UBJDUBJzUBJ/UBKPUBLT4BbX4Bd3zBgAA"
    }

    override fun id(): String {
        return "e71234fa-sandbag-deck-97e9-1f4e126cd33b"
    }

    override fun executeChangeCard(cards: HashSet<Card>) {
        commonDeckStrategy.executeChangeCard(cards)
    }

    private var me: Player = Player.UNKNOWN_PLAYER
    private var rival: Player = Player.UNKNOWN_PLAYER
    private lateinit var myPlayCards: MutableList<Card>
    private lateinit var rivalPlayCards: MutableList<Card>

    override fun executeOutCard() {
        me = WAR.me
        rival = WAR.rival
        if (me.isInValid() || rival.isInValid()) return
        myPlayCards = me.playArea.cards.toMutableList()
        rivalPlayCards = rival.playArea.cards.toMutableList()
        DeckStrategyUtil.cleanPlay(myPlayCards = myPlayCards, rivalPlayCards = rivalPlayCards)
        rivalPlayCards = rival.playArea.cards.toMutableList()
        dealResource()
    }

    private fun dealResource() {
        when (me.usableResource) {
            0 -> dealZeroResource()
            1 -> dealOneResource()
            2 -> dealTwoResource()
            3 -> dealThreeResource()
            4 -> dealFourResource()
            5 -> dealFiveResource()
            6 -> dealSixResource()
            7 -> dealSevenResource()
            8 -> dealEightResource()
            9 -> dealNineResource()
            10 -> dealTenResource()
        }
    }

    private fun dealZeroResource() {

    }

    private fun dealOneResource() {

    }

    private fun dealTwoResource() {

    }

    private fun dealThreeResource() {

    }

    private fun dealFourResource() {

    }

    private fun dealFiveResource() {

    }

    private fun dealSixResource() {

    }

    private fun dealSevenResource() {

    }

    private fun dealEightResource() {

    }

    private fun dealNineResource() {

    }

    private fun dealTenResource() {

    }

    override fun executeDiscoverChooseCard(vararg cards: Card): Int {
        return commonDeckStrategy.executeDiscoverChooseCard(*cards)
    }
}
