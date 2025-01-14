package club.xiaojiawei.mcts

import club.xiaojiawei.bean.InitAction
import club.xiaojiawei.status.War
import club.xiaojiawei.util.WarUtil
import kotlin.random.Random

/**
 * @author 肖嘉威
 * @date 2025/1/10 10:04
 */
class MonteCarloTreeSearch(val maxDepth: Int = 10) {

    private val random: Random = Random(System.currentTimeMillis())

    private fun <T> randomSelect(list: List<T>): T {
        return list[random.nextInt(list.size)]
    }

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
            val action = randomSelect(unExpanded)
            nextNode = node.expand(action)
            println("expand:" + nextNode?.state?.score)
        }
        return nextNode
    }

    private fun simulate(node: MonteCarloTreeNode, rootNode: MonteCarloTreeNode, arg: Arg): Boolean {
        var tempNode = node
        while (!tempNode.isEnd()) {
            val actions = tempNode.actions
            val action = randomSelect(actions)
            val nextTempNode = tempNode.buildNextNode(action)
            tempNode = nextTempNode
        }
        val score = WarUtil.calcScore(tempNode.state.war)
        var inverseScore = 0.0
        val surplusTurn = arg.turnCount - 1
        if (surplusTurn > 0 && !WarUtil.isEnd(tempNode.state.war)) {
            val war = tempNode.state.war.clone().apply {
                for (card in rival.playArea.cards) {
                    card.resetExhausted()
                }
                rival.playArea.hero?.resetExhausted()
                rival.playArea.power?.resetExhausted()
            }
            val tempMe = war.me
            val tempPlayer1 = war.player1
            war.me = war.rival
            war.rival = tempMe
            war.player1 = war.player2
            war.player2 = tempPlayer1
            val nodes =
                MonteCarloTreeSearch(maxDepth).getBestActions(
                    tempNode.state.war,
                    Arg(0, surplusTurn, arg.turnAttenuationFactor * arg.turnAttenuationFactor, arg.countPerTurn)
                )
            if (nodes.isNotEmpty()) {
                val lastNode = nodes.last()
                inverseScore = lastNode.state.score * arg.turnAttenuationFactor
//                println("inverseScore:" + inverseScore)
            }
        }
        return score - inverseScore > rootNode.state.score
    }

    private fun backPropagation(node: MonteCarloTreeNode, win: Boolean) {
        var tempNode: MonteCarloTreeNode? = node
        while (tempNode != null) {
            tempNode.state.increaseVisit()
            if (win) {
                tempNode.state.increaseWin()
            }
            tempNode = tempNode.parent
        }
    }

    private fun buildBestActions(rootNode: MonteCarloTreeNode, totalCount: Int): MutableList<MonteCarloTreeNode> {
        val result = mutableListOf<MonteCarloTreeNode>()

//        var maxNode: MonteCarloTreeNode? = rootNode
//        var maxScore = Int.MIN_VALUE.toDouble()
//        var maxUCB = rootNode.state.calcUCB(totalCount)
//        var maxVisit = Int.MIN_VALUE
//        val children = rootNode.children.toMutableList()
//        while (children.isNotEmpty()) {
//            val list = mutableListOf<MonteCarloTreeNode>()
//            for (child in children) {
//                if (child.isEnd()) {
////                    val score = WarUtil.calcScore(child.state.war)
////                    if (score > maxScore) {
////                        maxNode = child
////                        maxScore = score
////                    }
//                    if (child.state.visitCount > maxVisit) {
//                        maxNode = child
//                        maxVisit = child.state.visitCount
//                    }
////                    val ucb = child.state.calcUCB(totalCount)
////                    if (ucb > maxUCB) {
////                        maxUCB = ucb
////                        maxNode = child
////                    }
//                }
//                list.addAll(child.children)
//            }
//            children.clear()
//            children.addAll(list)
//        }
//
//        var tempNode: MonteCarloTreeNode? = maxNode
//        while (tempNode != null) {
//            result.addFirst(tempNode)
//            tempNode = tempNode.parent
//        }

        var node: MonteCarloTreeNode? = rootNode
        while (node != null) {
            result.add(node)
            var maxVisit = Int.MIN_VALUE
            var maxNode: MonteCarloTreeNode? = null
            for (child in node.children) {
                if (child.state.visitCount > maxVisit) {
                    maxNode = child
                    maxVisit = child.state.visitCount
                }
            }
            node = maxNode
        }

        return result
    }

    fun getBestActions(
        war: War, arg: Arg
    ): MutableList<MonteCarloTreeNode> {
        val rootNode = MonteCarloTreeNode(war.clone(), InitAction)
        var node: MonteCarloTreeNode
        var totalCount = 0
        var win = false

        val runnable = Runnable {
            node = select(rootNode, totalCount)
            if (!node.isEnd()) {
                expand(node)?.let {
                    node = it
                }
            }
            win = simulate(node, rootNode, arg)
            backPropagation(node, win)
            totalCount++
        }
        if (arg.thinkingTime > 0) {
            val endTime = System.currentTimeMillis() + arg.thinkingTime
            while (System.currentTimeMillis() < endTime) {
                runnable.run()
            }
        } else {
            while (totalCount < arg.countPerTurn) {
                runnable.run()
            }
        }

        return buildBestActions(rootNode, totalCount)
    }

    data class Arg(
        val thinkingTime: Int,
        val turnCount: Int,
        val turnAttenuationFactor: Double,
        val countPerTurn: Int,
    )
}