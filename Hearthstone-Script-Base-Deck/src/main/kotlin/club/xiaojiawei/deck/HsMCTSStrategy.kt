package club.xiaojiawei.deck

import club.xiaojiawei.DeckStrategy
import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.EmptyAction
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
class HsMCTSStrategy : DeckStrategy() {

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

    override fun executeOutCard() {
        val war = WAR
        val monteCarloTreeSearch = MonteCarloTreeSearch()
//        var arg: MCTSArg = if (war.me.playArea.cardSize() < 4 && war.rival.playArea.cardSize() < 4) {
//            log.info { "开始思考如何打牌，反演1轮" }
//            MCTSArg(System.currentTimeMillis() + 60 * 1000, 2, 0.5, 15_000, MCTSUtil.buildScoreCalculator(), true)
//        } else {
//            log.info { "开始思考如何打牌，反演0轮" }
//            MCTSArg(System.currentTimeMillis() + 60 * 1000, 1, 0.5, 15_000, MCTSUtil.buildScoreCalculator(), true)
//        }
        var arg = MCTSArg(System.currentTimeMillis() + 45 * 1000, 1, 0.5, 15_000, MCTSUtil.buildScoreCalculator(), true)

//        val stringBuilder = StringBuilder("战场可行动卡牌: ")
//        war.me.playArea.cards.filter { card -> card.canAttack() || card.canPower() }.forEach { card: Card ->
//            stringBuilder.append(card).append(",")
//        }
//        stringBuilder.deleteCharAt(stringBuilder.length - 1)
//        log.info { stringBuilder }
        war.me.handArea.cards.forEach { card: Card ->
            println("cardId:${card.cardId}, entityId:${card.entityId}, cost:${card.cost}, action:${card.action::class.qualifiedName}")
        }

        val start = System.currentTimeMillis()
        var bestNodes = monteCarloTreeSearch.searchBestNode(war, arg).filter { it.applyAction !is EmptyAction }
        log.info { "思考耗时：${(System.currentTimeMillis() - start)}ms，执行动作数：${bestNodes.size}" }
        bestNodes.forEach { action ->
            action.applyAction.exec.accept(war)
        }

        System.gc()
        Thread.sleep(3000)

        log.info { "再次思考如何打牌" }
        arg = MCTSArg(System.currentTimeMillis() + 10 * 1000, 1, 0.5, 10_000, MCTSUtil.buildScoreCalculator(), true)
        bestNodes = monteCarloTreeSearch.searchBestNode(war, arg).filter { it.applyAction !is EmptyAction }
        bestNodes.forEach { action ->
            action.applyAction.exec.accept(war)
        }

        log.info { "出牌结束" }
        System.gc()
    }

    override fun executeDiscoverChooseCard(vararg cards: Card): Int {
        return 1
    }

}