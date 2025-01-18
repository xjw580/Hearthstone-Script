package club.xiaojiawei.mcts

import club.xiaojiawei.bean.InitAction
import club.xiaojiawei.bean.LRunnable
import club.xiaojiawei.bean.MCTSArg
import club.xiaojiawei.config.CALC_THREAD_POOL
import club.xiaojiawei.status.War
import club.xiaojiawei.util.randomSelect
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.function.Function
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min

/**
 * @author 肖嘉威
 * @date 2025/1/10 10:04
 */
private const val MCTS_DEFAULT_DEPTH = 10

class MonteCarloTreeSearch(val maxDepth: Int = MCTS_DEFAULT_DEPTH) {

    private fun select(rootNode: MonteCarloTreeNode, totalCount: Int): MonteCarloTreeNode {
        var node: MonteCarloTreeNode = rootNode
        var maxUCB = Int.MIN_VALUE.toDouble()
        var level = 0
        while (node.isFullExpanded() && !node.isLeaf()) {
            val parentNode = node
            val children = node.children
            for (child in children) {
                val ucb = child.state.calcUCB(parentNode.state.visitCount)
                if (ucb > maxUCB) {
                    maxUCB = ucb
                    node = child
                }
            }
            level++
            if (level > maxDepth) {
                break
            }
        }
        return node
    }

    private fun expand(node: MonteCarloTreeNode): MonteCarloTreeNode? {
        var nextNode: MonteCarloTreeNode? = null
        if (!node.isFullExpanded()) {
            val unExpanded = node.getUnExpanded()
            val action = unExpanded.randomSelect()
            nextNode = node.expand(action)
        }
        return nextNode
    }

    private fun simulate(node: MonteCarloTreeNode, rootNode: MonteCarloTreeNode, arg: MCTSArg): Boolean {
        var tempNode = node
        while (!tempNode.isEnd()) {
            val actions = tempNode.actions
            val action = actions.randomSelect()
            val nextTempNode = tempNode.buildNextNode(action)
            tempNode = nextTempNode
        }
        val score = tempNode.state.score
//        var inverseScore = 0.0
//        val surplusTurn = arg.turnCount - 1
//        if (surplusTurn > 0 && !MCTSUtil.isEnd(tempNode.state.war)) {
//            val war = tempNode.state.war.clone().apply {
//                for (card in rival.playArea.cards) {
//                    card.resetExhausted()
//                }
//                rival.playArea.hero?.resetExhausted()
//                rival.playArea.power?.resetExhausted()
//            }
//            war.exchangePlayer()
//            val nodes =
//                MonteCarloTreeSearch(maxDepth).getBestActions(
//                    tempNode.state.war,
//                    MCTSArg(
//                        0,
//                        surplusTurn,
//                        arg.turnFactor * arg.turnFactor,
//                        arg.countPerTurn,
//                        arg.scoreCalculator
//                    )
//                )
//            if (nodes.isNotEmpty()) {
//                val lastNode = nodes.last()
//                inverseScore = lastNode.state.score * arg.turnFactor
//            }
//        }
        return score > rootNode.state.score
    }

    private fun backPropagation(node: MonteCarloTreeNode, win: Boolean?) {
        var tempNode: MonteCarloTreeNode? = node
        while (tempNode != null) {
            tempNode.state.update(win)
            tempNode = tempNode.parent
        }
    }

    private fun buildBestActions(rootNode: MonteCarloTreeNode, totalCount: Int): MutableList<MonteCarloTreeNode> {
        val result = mutableListOf<MonteCarloTreeNode>()

        var maxNode: MonteCarloTreeNode? = rootNode
        var maxScore = Int.MIN_VALUE.toDouble()
        var children = rootNode.children
        while (children.isNotEmpty()) {
            val list = mutableListOf<MonteCarloTreeNode>()
            for (child in children) {
                if (child.isEnd()) {
                    val score = child.state.score
                    if (score > maxScore) {
                        maxNode = child
                        maxScore = score
                    }
//                    if (child.state.visitCount > maxVisit) {
//                        maxNode = child
//                        maxVisit = child.state.visitCount
//                    }
//                    val ucb = child.state.calcUCB(totalCount)
//                    if (ucb > maxUCB) {
//                        maxUCB = ucb
//                        maxNode = child
//                    }
                }
                list.addAll(child.children)
            }
            children = list
        }

        var tempNode: MonteCarloTreeNode? = maxNode
        while (tempNode != null) {
            result.addFirst(tempNode)
            tempNode = tempNode.parent
        }

//        var node: MonteCarloTreeNode? = rootNode
//        while (node != null) {
//            result.add(node)
//            var maxVisit = Int.MIN_VALUE
//            var maxNode: MonteCarloTreeNode? = null
//            for (child in node.children) {
//                if (child.state.visitCount > maxVisit) {
//                    maxNode = child
//                    maxVisit = child.state.visitCount
//                }
//            }
//            node = maxNode
//        }

        return result
    }

    fun getBestActions(
        war: War, arg: MCTSArg
    ): MutableList<MonteCarloTreeNode> {
        val newWar = war.clone()
//        因为对手手牌不可知，所以去除模拟
        newWar.rival.handArea.cards.clear()
        val newArg = MCTSArg(
            arg.thinkingSecTime,
            arg.turnCount,
            arg.turnFactor,
            arg.countPerTurn,
            arg.scoreCalculator,
            false
        )
        val endTime = System.currentTimeMillis() + newArg.thinkingSecTime
        val rootNode = MonteCarloTreeNode(newWar, InitAction, newArg)
        val results = Collections.synchronizedList(mutableListOf<MutableList<MonteCarloTreeNode>>())
        val tasks = mutableListOf<CompletableFuture<Void>>()
        val tasker = Function<MonteCarloTreeNode, MutableList<MonteCarloTreeNode>> { newRootNode ->
            var totalCount = 0
            var node: MonteCarloTreeNode

            while (totalCount < newArg.countPerTurn && System.currentTimeMillis() < endTime) {
                node = select(newRootNode, totalCount)
                var win: Boolean? = null
                if (!node.isEnd()) {
                    expand(node)?.let {
                        node = it
                        win = simulate(node, newRootNode, newArg)
                    }
                }
                backPropagation(node, win)
                totalCount++
            }

            buildBestActions(newRootNode, totalCount)
        }

        if (arg.enableMultiThread) {
            val maxTaskSize = Runtime.getRuntime().availableProcessors()
            val size = rootNode.actions.size
            val countPerTask = ceil(size / maxTaskSize.toDouble()).toInt()
            var index = 0
            while (index < size) {
                val endIndex = min(index + countPerTask, size)
                val rootNodesList = mutableListOf<MonteCarloTreeNode>()
                val counts = endIndex - index
                val childArg = MCTSArg(
                    floor(arg.thinkingSecTime / counts.toDouble()).toInt(),
                    arg.turnCount,
                    arg.turnFactor,
                    floor(arg.countPerTurn / counts.toDouble()).toInt(),
                    arg.scoreCalculator,
                    false
                )
                for (i in index until endIndex) {
                    rootNode.expand(rootNode.actions[i], childArg)?.let { newRootNode ->
                        rootNodesList.add(newRootNode)
                    }
                }
                tasks.add(
                    CompletableFuture.runAsync(
                        LRunnable {
                            rootNodesList.forEach { newRootNode ->
                                results.add(tasker.apply(newRootNode))
                            }
                        }, CALC_THREAD_POOL
                    )
                )
                index = endIndex
            }
        } else {
            results.add(tasker.apply(rootNode))
        }

        if (tasks.isNotEmpty()) {
            CompletableFuture.allOf(*tasks.toTypedArray()).get(arg.thinkingSecTime + 5L, TimeUnit.SECONDS)
        }

        var maxScore = Int.MIN_VALUE.toDouble()
        var bestResult: MutableList<MonteCarloTreeNode>? = null
        results.forEach { result ->
            if (result.isNotEmpty()) {
                val score = result.last().state.score
                if (score > maxScore) {
                    maxScore = score
                    bestResult = result
                }
            }
        }
        return bestResult ?: mutableListOf()
    }

}

