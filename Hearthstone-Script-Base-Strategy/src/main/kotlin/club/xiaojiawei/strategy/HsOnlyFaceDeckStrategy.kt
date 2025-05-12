package club.xiaojiawei.strategy

import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.MCTSArg
import club.xiaojiawei.bean.War
import club.xiaojiawei.deck.MCTSDeckStrategy
import club.xiaojiawei.enums.RunModeEnum
import club.xiaojiawei.mctsbuilder.OnlyFaceWarScoreCalculatorBuilder
import club.xiaojiawei.status.WAR
import club.xiaojiawei.util.DeckStrategyUtil

/**
 * @author 肖嘉威
 * @date 2025/4/12 11:16
 */
class HsOnlyFaceDeckStrategy : MCTSDeckStrategy() {
    override fun name(): String = "打脸策略"

    override fun getRunMode(): Array<RunModeEnum> =
        arrayOf(RunModeEnum.CASUAL, RunModeEnum.STANDARD, RunModeEnum.WILD, RunModeEnum.PRACTICE)

    override fun deckCode(): String = ""

    override fun id(): String = "e71234fa-4-only-face-deck-97e9-1f4e126cd33b"

    override fun description(): String = "我的眼里只有脸"

    override fun executeChangeCard(cards: HashSet<Card>) {
        HsCommonDeckStrategy().executeChangeCard(cards)
    }

    override fun executeMCTSOutCard(war: War): List<MCTSArg> {
        val calculator = OnlyFaceWarScoreCalculatorBuilder().build()
        val start = System.currentTimeMillis()
        return listOf(
            MCTSArg(start + 30 * 1000, 1, 0.1, 20_000, calculator, true),
            MCTSArg(start + 10 * 1000, 1, 0.5, 10_000, calculator, true),
        )
    }

    override fun executeOutCard() {
        super.executeOutCard()
        DeckStrategyUtil.powerCard(WAR.me, WAR.rival)
    }

    override fun executeDiscoverChooseCard(vararg cards: Card): Int {
//        选择费用最低的牌
        var minCostCardIndex = 0
        var minCost = Integer.MAX_VALUE
        for ((index, card) in cards.withIndex()) {
            if (card.cost < minCost) {
                minCostCardIndex = index
                minCost = card.cost
            }
        }
        return minCostCardIndex
    }
}
