package club.xiaojiawei.mcts

import club.xiaojiawei.bean.InitAction
import club.xiaojiawei.hsscriptbase.bean.LRunnable
import club.xiaojiawei.bean.MCTSArg
import club.xiaojiawei.bean.War
import club.xiaojiawei.hsscriptbase.config.CALC_THREAD_POOL
import club.xiaojiawei.hsscriptbase.config.log
import club.xiaojiawei.hsscriptbase.util.randomSelect
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.function.Function
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min

/**
 * 蒙特卡洛树搜索
 * @author 肖嘉威
 * @date 2025/1/10 10:04
 */
const val MCTS_DEFAULT_DEPTH = 10

class MonteCarloTreeSearch(val maxDepth: Int = MCTS_DEFAULT_DEPTH) {

    private fun select(rootNode: MonteCarloTreeNode, endTime: Long): MonteCarloTreeNode {
        var node: MonteCarloTreeNode = rootNode
        var maxUCB = Int.MIN_VALUE.toDouble()
        var level = 0
        while (node.isFullExpanded() && !node.isLeaf() && System.currentTimeMillis() < endTime) {
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

    private fun simulate(node: MonteCarloTreeNode, rootNode: MonteCarloTreeNode, endTime: Long): Boolean {
        var tempNode = node
        var isFirstTempNode = true
        while (!tempNode.isEnd() && System.currentTimeMillis() < endTime) {
            val actions = tempNode.actions
            val action = actions.randomSelect()

            val nextTempNode = if (isFirstTempNode) {
                isFirstTempNode = false
                tempNode.buildNextNode(action, cloneWar = true)
            } else tempNode.buildNextNode(action, cloneWar = false)

            tempNode = nextTempNode
        }
        val score = tempNode.state.score
        return score > rootNode.state.score
    }

    private fun backPropagation(node: MonteCarloTreeNode, win: Boolean?) {
        var tempNode: MonteCarloTreeNode? = node
        while (tempNode != null) {
            tempNode.state.update(win)
            tempNode = tempNode.parent
        }
    }

    private fun buildBest(rootNode: MonteCarloTreeNode): MutableList<MonteCarloTreeNode> {
        val result = mutableListOf<MonteCarloTreeNode>()

        var maxNode: MonteCarloTreeNode? = rootNode
        var maxScore = Int.MIN_VALUE.toDouble()
        var maxVisit = Int.MIN_VALUE
        var children = rootNode.children.toList()
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

    fun searchBestNode(
        war: War, arg: MCTSArg
    ): MutableList<MonteCarloTreeNode> {
        val totalMillisTime = arg.endMillisTime - System.currentTimeMillis()
        val newWar = war.clone()
//        因为对手手牌不可知，所以去除模拟，todo 非正确处理方式
        newWar.rival.handArea.cards.clear()
        val newArg = MCTSArg(
            arg.endMillisTime,
            arg.turnCount,
            arg.turnFactor,
            arg.countPerTurn,
            arg.scoreCalculator,
            false
        )
        val endTime = arg.endMillisTime
        val rootNode = MonteCarloTreeNode(newWar, InitAction, newArg)
        val results = Collections.synchronizedList(mutableListOf<MutableList<MonteCarloTreeNode>>())
        val tasks = mutableListOf<CompletableFuture<Void>>()
        val tasker = Function<MonteCarloTreeNode, MutableList<MonteCarloTreeNode>> { newRootNode ->
            var totalCount = 0
            var node: MonteCarloTreeNode

            while (totalCount < newArg.countPerTurn && System.currentTimeMillis() < endTime) {
                node = select(newRootNode, endTime)
                var win: Boolean? = null
                if (!node.isEnd()) {
                    expand(node)?.let {
                        node = it
                        win = simulate(node, newRootNode, endTime)
                    }
                }
                backPropagation(node, win)
                totalCount++
            }

            buildBest(newRootNode)
        }

        if (arg.enableMultiThread) {
            val maxTaskSize = Runtime.getRuntime().availableProcessors()
            val size = rootNode.actions.size
            val countPerTask = ceil(size / maxTaskSize.toDouble()).toInt()
            var index = 0
            while (index < size && System.currentTimeMillis() < endTime) {
                val endIndex = min(index + countPerTask, size)
                val rootNodesList = mutableListOf<MonteCarloTreeNode>()
                val counts = endIndex - index
                val childArg = MCTSArg(
                    arg.endMillisTime,
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
                            for (newRootNode in rootNodesList.reversed()) {
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
            try {
                CompletableFuture.allOf(*tasks.toTypedArray()).get(totalMillisTime, TimeUnit.MILLISECONDS)
            } catch (e: TimeoutException) {
                log.warn(e) { "计算超时" }
            } catch (e: InterruptedException) {
                log.warn(e) { "计算中断" }
            } catch (e: Exception) {
                log.error(e) { "计算异常" }
            }
        }

        var maxScore = Int.MIN_VALUE.toDouble()
        var bestResult: MutableList<MonteCarloTreeNode>? = null
        if (results.isEmpty()) {
            bestResult = buildBest(rootNode)
        } else {
            for (result in results) {
                if (result.isNotEmpty()) {
                    val score = result.last().state.score
                    if (score > maxScore) {
                        maxScore = score
                        bestResult = result
                    }
                }
            }
        }

        return bestResult ?: mutableListOf()
    }

}