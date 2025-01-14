package club.xiaojiawei.test

import club.xiaojiawei.mcts.MonteCarloTreeNode
import club.xiaojiawei.mcts.MonteCarloTreeSearch
import club.xiaojiawei.util.WarUtil

/**
 * @author 肖嘉威
 * @date 2025/1/14 9:14
 */

data class Result(val score: Double, val visitCount: Int)

fun printAll(tempNode: MonteCarloTreeNode?, level: Int, scores: MutableList<MutableList<Result>>) {
    tempNode ?: return
    val children = tempNode.children
    if (children.isEmpty()) {
        return
    } else {
        if (level >= scores.size) {
            scores.add(mutableListOf())
        }
        val list = scores[level]
        for (child in children) {
            list.add(Result(WarUtil.calcScore(child.state.war), child.state.visitCount))
        }
        for (child in children) {
            printAll(child, level + 1, scores)
        }
    }
}

fun main() {
    val mctsWar = WarUtil.createMCTSWar()

    val monteCarloTreeSearch = MonteCarloTreeSearch()

    val start = System.currentTimeMillis()
    val monteCarloTreeNodes = monteCarloTreeSearch
        .getBestActions(mctsWar, MonteCarloTreeSearch.Arg(10 * 1000, 1, 0.8, 10000))
    var tempNode: MonteCarloTreeNode? = monteCarloTreeNodes.first()
    tempNode?.let {
        val scores = mutableListOf<MutableList<Result>>(
            mutableListOf(
                Result(
                    WarUtil.calcScore(tempNode.state.war),
                    tempNode.state.visitCount
                )
            )
        )
        printAll(tempNode, 1, scores)
        for ((index, doubles) in scores.withIndex()) {
            doubles.sortBy { it.visitCount }
            print("no:$index")
            print(doubles)
            println("\n")
        }
    }
    println("==================================================================================")
    println("time: ${System.currentTimeMillis() - start}ms")
    println("size:" + monteCarloTreeNodes.size)
    for (node in monteCarloTreeNodes) {
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
    println("score:" + WarUtil.calcScore(node.state.war))
}