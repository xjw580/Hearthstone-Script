package club.xiaojiawei.deck

import club.xiaojiawei.DeckStrategy
import club.xiaojiawei.bean.EmptyAction
import club.xiaojiawei.bean.MCTSArg
import club.xiaojiawei.bean.War
import club.xiaojiawei.config.log
import club.xiaojiawei.mcts.MonteCarloTreeSearch
import club.xiaojiawei.status.WAR

/**
 * 蒙特卡洛树搜索算法
 * @author 肖嘉威
 * @date 2025/1/22 17:04
 */
abstract class MCTSDeckStrategy : DeckStrategy() {

    final override fun executeOutCard() {
        val war = WAR
        val mctsArgList = executeMCTSOutCard(war)
        val monteCarloTreeSearch = MonteCarloTreeSearch()
        for ((index, mctsArg) in mctsArgList.withIndex()) {
            val start = System.currentTimeMillis()
            val bestNodes = monteCarloTreeSearch.searchBestNode(war, mctsArg).filter { it.applyAction !is EmptyAction }
            log.info { "思考耗时：${(System.currentTimeMillis() - start)}ms，执行动作数：${bestNodes.size}" }
            bestNodes.forEach { action ->
                action.applyAction.exec.accept(war)
            }
            if (index < mctsArgList.size - 1) {
                Thread.sleep(2000)
            }
            System.gc()
        }
    }

    /**
     * 通过mcts算法出牌
     * @return mcts算法参数，返回几个参数就执行几次
     */
    abstract fun executeMCTSOutCard(war: War): List<MCTSArg>

}