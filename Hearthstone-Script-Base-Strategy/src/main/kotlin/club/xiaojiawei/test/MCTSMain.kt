package club.xiaojiawei.test

import club.xiaojiawei.bean.DEFAULT_WAR_SCORE_CALCULATOR
import club.xiaojiawei.bean.MCTSArg
import club.xiaojiawei.mcts.MonteCarloTreeNode
import club.xiaojiawei.mcts.MonteCarloTreeSearch
import club.xiaojiawei.util.DeckStrategyUtil.createMCTSWar

/**
 * @author 肖嘉威
 * @date 2025/1/14 9:14
 */

data class Result(val score: Double, val visitCount: Int)

fun addNode(tempNode: MonteCarloTreeNode?, level: Int, nodes: MutableList<MutableList<Result>>) {
    tempNode ?: return
    val children = tempNode.children
    if (children.isEmpty()) {
        return
    } else {
        if (level >= nodes.size) {
            nodes.add(mutableListOf())
        }
        val list = nodes[level]
        for (child in children) {
            list.add(Result(child.state.score, child.state.visitCount))
        }
        for (child in children) {
            addNode(child, level + 1, nodes)
        }
    }
}


fun main() {
    val mctsWar = createMCTSWar()

    val monteCarloTreeSearch = MonteCarloTreeSearch()

    val start = System.currentTimeMillis()

    val arg = MCTSArg(start + 3000 * 1000, 1, 0.5, 500_000, DEFAULT_WAR_SCORE_CALCULATOR.build(), false)
//    val arg = MCTSArg(start + 15 * 1000, 2, 0.5, 200_000, DEFAULT_WAR_SCORE_CALCULATOR.build(), true)
    val monteCarloTreeNodes = monteCarloTreeSearch
        .searchBestNode(mctsWar, arg)
//    val tempNode: MonteCarloTreeNode? = monteCarloTreeNodes.first()
//    tempNode?.let {
//        val scores = mutableListOf<MutableList<Result>>(
//            mutableListOf(
//                Result(
//                    tempNode.state.score,
//                    tempNode.state.visitCount
//                )
//            )
//        )
//        addNode(tempNode, 1, scores)
//        for ((index, doubles) in scores.withIndex()) {
//            doubles.sortBy { it.visitCount }
//            print("no:$index")
//            print(doubles)
//            println("\n")
//        }
//    }
    println("==================================================================================")
    println("time: ${System.currentTimeMillis() - start}ms")
    println("action size:" + monteCarloTreeNodes.size)
    for (node in monteCarloTreeNodes) {
        println("visitCount: ${node.state.visitCount}")
        node.applyAction.exec.accept(mctsWar)
    }
    if (monteCarloTreeNodes.isNotEmpty()) {
        println("==================================================================================")
        printMsg(monteCarloTreeNodes.last())
        println("==================================================================================")
    }
}

fun printMsg(node: MonteCarloTreeNode) {
    for (card in node.state.war.me.playArea.cards) {
        println("myCard: entityId:${card.entityId}, atc:${card.atc}, blood:${card.blood()}")
    }
    val myHero = node.state.war.me.playArea.hero
    myHero?.let { card ->
        println("myHero: entityId:${card.entityId}, atc:${card.atc}, blood:${card.blood()}")
    }
    println("====================================")
    for (card in node.state.war.rival.playArea.cards) {
        println("rivalCard: entityId:${card.entityId}, atc:${card.atc}, blood:${card.blood()}")
    }
    val rivalHero = node.state.war.rival.playArea.hero
    rivalHero?.let { card ->
        println("rivalHero: entityId:${card.entityId}, atc:${card.atc}, blood:${card.blood()}")
    }
    println("score:" + node.state.score)
}