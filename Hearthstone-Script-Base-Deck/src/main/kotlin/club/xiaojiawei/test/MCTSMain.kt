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

//            card = Card(TestCardAction())
//            card.entityId = "11"
//            card.entityName = "myHand1"
//            card.health = 2
//            card.atc = 3
//            card.cost = 2
//            card.cardType = CardTypeEnum.MINION
//            player.handArea.add(card)
//
//            card = Card(TestCardAction())
//            card.entityId = "12"
//            card.entityName = "myHand2"
//            card.health = 2
//            card.atc = 3
//            card.cost = 3
//            card.cardType = CardTypeEnum.MINION
//            player.handArea.add(card)
//
//            card = Card(TestCardAction())
//            card.entityId = "13"
//            card.entityName = "myHand3"
//            card.cost = 1
//            card.cardType = CardTypeEnum.SPELL
//            player.handArea.add(card)


            player.resources = 6

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
//    val map = mutableMapOf<File, MutableList<File>>()
//    val file = File("D:\\音乐")
//    val listFiles = file.listFiles()
//    val regex = "\\(\\d+\\)\$".toRegex()
//    listFiles?.forEach { file ->
//        file?.let { it ->
//            if (it.isDirectory) return@let
//            val name = it.name
//            val dotIndex = name.lastIndexOf(".")
//            if (dotIndex < 0) return@let
//            val suffix = name.substring(dotIndex)
//            val prefix = name.substring(0, dotIndex)
//
//            val key = if (prefix.contains(regex)) {
//                it.parentFile.resolve(prefix.replace(regex, "").trim())
//            } else {
//                it.parentFile.resolve(prefix.replace(regex, "").trim())
//            }
//            var files = map[key]
//            if (files == null) {
//                files = mutableListOf()
//                map[key] = files
//            }
//            files.add(it)
//        }
//    }
//    map.forEach { (key, files) ->
//        var maxSize = -1L
//        var maxFile: File? = null
//        files.forEach { file ->
//            if (file.length() > maxSize) {
//                maxSize = file.length()
//                maxFile = file
//            }
//        }
//        maxFile?.let { file ->
//            files.forEach { f ->
//                if (f != file) {
//                    println("delete ${f}")
//                    f.delete()
//                }
//            }
//            val name = file.name
//            val dotIndex = name.lastIndexOf(".")
//            if (dotIndex < 0) return@let
//            val suffix = name.substring(dotIndex)
//            val targetFile = File(key.absolutePath + suffix)
//            if (file.renameTo(targetFile)) {
//                println("rename: ${file.name} to ${targetFile.name}")
//            } else {
//                println("failed rename: ${file.name} to ${targetFile.name}")
//            }
//        }
//    }
//    if (true) return
    val mctsWar = createMCTSWar()

    val monteCarloTreeSearch = MonteCarloTreeSearch()

    val start = System.currentTimeMillis()

//    val arg = MCTSArg(2 * 1000, 1, 0.5, 50_000, MCTSUtil.buildScoreCalculator())
    val arg = MCTSArg(15 * 1000, 5, 0.8, 200_000, MCTSUtil.buildScoreCalculator(), true)
    val monteCarloTreeNodes = monteCarloTreeSearch
        .getBestActions(mctsWar, arg)
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