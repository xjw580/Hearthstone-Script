package club.xiaojiawei.test.mcts

import club.xiaojiawei.Action
import club.xiaojiawei.InitAction
import club.xiaojiawei.TurnOverAction
import club.xiaojiawei.WarState
import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.Player
import club.xiaojiawei.enums.CardTypeEnum
import club.xiaojiawei.test.TestCardAction
import kotlin.random.Random

/**
 * @author 肖嘉威
 * @date 2025/1/10 10:04
 */
class MonteCarloTreeSearch(val maxDepth: Int = 20) {

    private val random: Random = Random(System.currentTimeMillis())

    private fun <T> randomSelect(list: List<T>): T {
        return list[random.nextInt(list.size)]
    }

    private fun select(rootNode: MonteCarloTreeNode, totalCount: Int): MonteCarloTreeNode {
        var node: MonteCarloTreeNode = rootNode
        var maxUCB = Int.MIN_VALUE.toDouble()
        var level = 0
        while (node.isFullExpand()) {
            val children = node.children
            for (child in children) {
                val ucb = child.state.calcUCB(totalCount)
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
        if (!node.isFullExpand()) {
            val unExpanded = node.getUnExpanded()
            val action = randomSelect(unExpanded)
            nextNode = node.expand(action)
        }
        return nextNode
    }

    private fun simulate(node: MonteCarloTreeNode): Double {
        var tempNode = node
        while (!tempNode.isEnd()) {
            val actions = tempNode.actions
            val action = randomSelect(actions)
            val nextTempNode = tempNode.buildNextNode(action)
            tempNode = nextTempNode
        }
        return tempNode.state.warState.calcScore()
    }

    private fun backPropagation(node: MonteCarloTreeNode, score: Double) {
        var tempNode: MonteCarloTreeNode? = node
        while (tempNode != null) {
            tempNode.state.increaseVisit()
            tempNode.state.addScore(score)
            tempNode = tempNode.parent
        }
    }

    fun getBestActions(
        me: Player, rival: Player, thinkingTime: Int = 5 * 1000
    ): MutableList<Action> {
        val endTime = System.currentTimeMillis() + thinkingTime

        val rootNode = MonteCarloTreeNode(WarState(me.clone(), rival.clone()), InitAction)
        var node: MonteCarloTreeNode

        var totalCount = 0
        while (System.currentTimeMillis() < endTime) {
            var score = 0.0
            node = select(rootNode, totalCount)
            if (!node.isEnd()) {
                expand(node)?.let {
                    node = it
                    score = simulate(node)
                }
            }
            backPropagation(node, score)
            totalCount++
        }

        val result = mutableListOf<Action>()
        node = rootNode
        while (!node.isEnd()) {
            val children = node.children
            var maxVisitCount = Int.MIN_VALUE
            var maxUCB = Int.MIN_VALUE.toDouble()
            var maxNode: MonteCarloTreeNode? = null
            for (child in children) {
//                if (child.state.visitCount > maxVisitCount) {
//                    maxVisitCount = child.state.visitCount
//                    maxNode = child
//                }
                val ucb = child.state.calcUCB(totalCount)
                if (ucb > maxUCB){
                    maxUCB = ucb
                    maxNode= child
                }
            }
            maxNode?.let {
                if (maxNode.applyAction !== TurnOverAction) {
                    result.add(maxNode.applyAction)
                }
                node = maxNode
            } ?: break
        }
        for (card in node.state.warState.me.playArea.cards) {
            if (card.isSurvival()) {
                println("myCard: entityId:${card.entityId}, atc:${card.atc}, blood:${card.blood()}")
            } else {
                println("myCard die: entityId:${card.entityId}, atc:${card.atc}, blood:${card.blood()}")
            }
        }
        val myHero = node.state.warState.me.playArea.hero
        myHero?.let { card ->
            if (card.isSurvival()) {
                println("myHero: entityId:${card.entityId}, atc:${card.atc}, blood:${card.blood()}")
            } else {
                println("myHero die: entityId:${card.entityId}, atc:${card.atc}, blood:${card.blood()}")
            }
        }
        println("====================================")
        for (card in node.state.warState.rival.playArea.cards) {
            if (card.isSurvival()) {
                println("rivalCard: entityId:${card.entityId}, atc:${card.atc}, blood:${card.blood()}")
            } else {
                println("rivalCard die: entityId:${card.entityId}, atc:${card.atc}, blood:${card.blood()}")
            }
        }
        val rivalHero = node.state.warState.rival.playArea.hero
        rivalHero?.let { card ->
            if (card.isSurvival()) {
                println("rivalHero: entityId:${card.entityId}, atc:${card.atc}, blood:${card.blood()}")
            } else {
                println("rivalHero die: entityId:${card.entityId}, atc:${card.atc}, blood:${card.blood()}")
            }
        }
        return result
    }
}

fun main() {
    val monteCarloTreeSearch = MonteCarloTreeSearch()
    val me = run {
        val player = Player("1", "myRobot")
        var card = Card(TestCardAction())
        card.entityId = "0"
        card.health = 20
        card.cardType = CardTypeEnum.HERO
        player.playArea.add(card)
        card = Card(TestCardAction())
        card.entityId = "1"
        card.health = 4
        card.atc = 4
        card.cardType = CardTypeEnum.MINION
        player.playArea.add(card)
        card = Card(TestCardAction())
        card.entityId = "2"
        card.health = 3
        card.atc = 5
        card.cardType = CardTypeEnum.MINION
        player.playArea.add(card)
        card = Card(TestCardAction())
        card.entityId = "3"
        card.health = 7
        card.atc = 3
        card.cardType = CardTypeEnum.MINION
        player.playArea.add(card)
        card = Card(TestCardAction())
        card.entityId = "4"
        card.health = 2
        card.atc = 5
        card.cardType = CardTypeEnum.MINION
        player.playArea.add(card)
        player.clone()
    }

    val rival = run {
        val player = Player("2", "rivalRobot")
        var card = Card(TestCardAction())
        card.entityId = "0"
        card.health = 15
        card.cardType = CardTypeEnum.HERO
        player.playArea.add(card)
        card = Card(TestCardAction())
        card.entityId = "1"
        card.health = 2
        card.atc = 4
        card.cardType = CardTypeEnum.MINION
        player.playArea.add(card)
        card = Card(TestCardAction())
        card.entityId = "2"
        card.health = 2
        card.atc = 2
        card.cardType = CardTypeEnum.MINION
        player.playArea.add(card)
//        card = Card(TestCardAction())
//        card.entityId = "3"
//        card.health = 5
//        card.atc = 6
//        card.cardType = CardTypeEnum.MINION
//        player.playArea.add(card)
        player.clone()
    }
    println("==================================================================================")
    val bestActions = monteCarloTreeSearch.getBestActions(me.clone(), rival.clone(), 3 * 1000)
    println("==================================================================================")
    val warState = WarState(me, rival)
    for (bestAction in bestActions) {
        bestAction.exec.accept(warState)
    }
}