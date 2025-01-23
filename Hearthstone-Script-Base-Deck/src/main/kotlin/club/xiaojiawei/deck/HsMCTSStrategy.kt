package club.xiaojiawei.deck

import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.DEFAULT_WAR_SCORE_CALCULATOR
import club.xiaojiawei.bean.MCTSArg
import club.xiaojiawei.bean.War
import club.xiaojiawei.enums.RunModeEnum

/**
 * @author 肖嘉威
 * @date 2024/9/8 14:56
 */
class HsMCTSStrategy : MCTSDeckStrategy() {

    override fun name(): String {
        return "mcts策略"
    }

    override fun getRunMode(): Array<RunModeEnum> {
        return arrayOf(RunModeEnum.CASUAL, RunModeEnum.STANDARD, RunModeEnum.WILD, RunModeEnum.PRACTICE)
    }

    override fun deckCode(): String {
        return ""
    }

    override fun id(): String {
        return "e71234fa-mcts-deck-97e9-1f4e126cd33b"
    }

    override fun executeChangeCard(cards: HashSet<Card>) {
        cards.removeIf { card -> card.cost > 2 }
    }

    override fun executeMCTSOutCard(war: War): List<MCTSArg> {
        val calculator = DEFAULT_WAR_SCORE_CALCULATOR.build()
        val start = System.currentTimeMillis()
        return listOf(
            MCTSArg(start + 20 * 1000, 1, 0.1, 20_000, calculator, true),
            MCTSArg(start + 10 * 1000, 1, 0.5, 10_000, calculator, true)
        )
    }

    override fun executeDiscoverChooseCard(vararg cards: Card): Int {
        return 1
    }

}