package club.xiaojiawei.deck

import club.xiaojiawei.DeckStrategy
import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.MCTSArg
import club.xiaojiawei.enums.RunModeEnum
import club.xiaojiawei.mcts.MonteCarloTreeSearch
import club.xiaojiawei.status.WAR
import club.xiaojiawei.util.MCTSUtil

/**
 * @author 肖嘉威
 * @date 2024/9/8 14:56
 */
class HsMCTSStrategy : DeckStrategy() {

    override fun name(): String {
        return "ai策略"
    }

    override fun getRunMode(): Array<RunModeEnum> {
        return arrayOf(RunModeEnum.CASUAL, RunModeEnum.STANDARD, RunModeEnum.WILD, RunModeEnum.PRACTICE)
    }

    override fun deckCode(): String {
        return ""
    }

    override fun id(): String {
        return "e71234fa-ai-deck-97e9-1f4e126cd33b"
    }

    override fun executeChangeCard(cards: HashSet<Card>) {
        cards.removeIf { card -> card.cost > 2 }
    }

    override fun executeOutCard() {
        val monteCarloTreeSearch = MonteCarloTreeSearch()
        val arg = MCTSArg(10 * 1000, 3, 0.9, 200_000, MCTSUtil.buildScoreCalculator())
        val bestActions = monteCarloTreeSearch.getBestActions(WAR, arg)
        bestActions.forEach { action ->
            action.applyAction.exec.accept(WAR)
        }
    }

    override fun executeDiscoverChooseCard(vararg cards: Card): Int {
        return 1
    }
}