package club.xiaojiawei.deck

import club.xiaojiawei.DeckStrategy
import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.MCTSArg
import club.xiaojiawei.config.log
import club.xiaojiawei.enums.RunModeEnum
import club.xiaojiawei.mcts.MonteCarloTreeSearch
import club.xiaojiawei.status.WAR
import club.xiaojiawei.util.MCTSUtil

/**
 * @author 肖嘉威
 * @date 2024/9/8 14:56
 */
class HsAIStrategy : DeckStrategy() {

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
        log.info { "开始思考如何打牌" }
        val monteCarloTreeSearch = MonteCarloTreeSearch()
        val arg = MCTSArg(15 * 1000, 3, 0.9, 200_000, MCTSUtil.buildScoreCalculator())
//        WAR.me.playArea.cards.forEach { card: Card ->
//            log.info { "play card: $card" }
//        }
//        WAR.me.handArea.cards.forEach { card: Card ->
//            log.info { "hand card: $card" }
//        }
        val start = System.currentTimeMillis()
        val bestActions = monteCarloTreeSearch.getBestActions(WAR, arg)
        log.info { "思考耗时：${(System.currentTimeMillis() - start)}ms，执行动作数：${bestActions.size}" }
        bestActions.forEach { action ->
            action.applyAction.exec.accept(WAR)
        }
    }

    override fun executeDiscoverChooseCard(vararg cards: Card): Int {
        return 1
    }
}