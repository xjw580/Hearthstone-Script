package club.xiaojiawei.test

import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.MCTSArg
import club.xiaojiawei.bean.Player
import club.xiaojiawei.bean.TestCardAction
import club.xiaojiawei.enums.CardTypeEnum
import club.xiaojiawei.mcts.MonteCarloTreeNode
import club.xiaojiawei.mcts.MonteCarloTreeSearch
import club.xiaojiawei.status.War
import club.xiaojiawei.util.MCTSUtil

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

fun createMCTSWar(): War {
    return War().apply {
        me = run {
            val player = Player("1", "myRobot")
            var card = Card(TestCardAction())
            card.entityId = "0"
            card.entityName = "myHero"
            card.health = 30
            card.cardType = CardTypeEnum.HERO
            player.playArea.add(card)

            card = Card(TestCardAction())
            card.entityId = "1"
            card.entityName = "myMinion1"
            card.health = 4
            card.atc = 3
            card.cardType = CardTypeEnum.MINION
            player.playArea.add(card)

            card = Card(TestCardAction())
            card.entityId = "2"
            card.entityName = "myMinion2"
            card.health = 3
            card.atc = 5
            card.cardType = CardTypeEnum.MINION
            player.playArea.add(card)

            card = Card(TestCardAction())
            card.entityId = "3"
            card.entityName = "myMinion3"
            card.health = 5
            card.atc = 4
//                card.isWindFury = true
//                card.isMegaWindfury = true
            card.cardType = CardTypeEnum.MINION
            player.playArea.add(card)

            card = Card(TestCardAction())
            card.entityId = "4"
            card.entityName = "myMinion4"
            card.health = 2
            card.atc = 3
            card.cardType = CardTypeEnum.MINION
            player.playArea.add(card)

            card = Card(TestCardAction())
            card.entityId = "11"
            card.entityName = "myHand1"
            card.health = 2
            card.atc = 3
            card.cost = 2
            card.isExhausted = true
            card.cardType = CardTypeEnum.MINION
            player.handArea.add(card)

            player.resources = 5

            player
        }

        player1 = me

        rival = run {
            val player = Player("2", "rivalRobot")
            var card = Card(TestCardAction())
            card.entityId = "0"
            card.entityName = "rivalHero"
            card.health = 30
//                card.atc = 5
            card.cardType = CardTypeEnum.HERO
            player.playArea.add(card)

            card = Card(TestCardAction())
            card.entityId = "1"
            card.entityName = "rivalMinion1"
            card.health = 4
            card.atc = 5
//                card.isTaunt = true
            card.cardType = CardTypeEnum.MINION
            player.playArea.add(card)

            card = Card(TestCardAction())
            card.entityId = "2"
            card.entityName = "rivalMinion2"
            card.health = 4
            card.atc = 3
            card.cardType = CardTypeEnum.MINION
            player.playArea.add(card)

            card = Card(TestCardAction())
            card.entityId = "3"
            card.entityName = "rivalMinion3"
            card.health = 2
            card.atc = 3
            card.cardType = CardTypeEnum.MINION
            player.playArea.add(card)

            player2 = rival

            player
        }
    }
}

fun main() {
    val mctsWar = createMCTSWar()

    val monteCarloTreeSearch = MonteCarloTreeSearch()

    val start = System.currentTimeMillis()

    val arg = MCTSArg(2 * 1000, 1, 0.5, 50000, MCTSUtil.buildScoreCalculator())
//    val arg = MCTSArg(10 * 1000, 3, 0.9, 200_000, MCTSUtil.buildScoreCalculator())
    val monteCarloTreeNodes = monteCarloTreeSearch
        .getBestActions(mctsWar, arg)
    val tempNode: MonteCarloTreeNode? = monteCarloTreeNodes.first()
    tempNode?.let {
        val scores = mutableListOf<MutableList<Result>>(
            mutableListOf(
                Result(
                    tempNode.state.score,
                    tempNode.state.visitCount
                )
            )
        )
        addNode(tempNode, 1, scores)
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
    println("score:" + node.state.score)
}