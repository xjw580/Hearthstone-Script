package club.xiaojiawei.hsscript.strategy

import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.isValid
import club.xiaojiawei.bean.safeRun
import club.xiaojiawei.config.log
import club.xiaojiawei.data.COIN_CARD_ID
import club.xiaojiawei.enums.ModeEnum
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.listener.log.ScreenLogListener
import club.xiaojiawei.hsscript.status.DeckStrategyManager
import club.xiaojiawei.hsscript.status.Mode
import club.xiaojiawei.hsscript.status.PauseStatus
import club.xiaojiawei.hsscript.utils.ConfigUtil
import club.xiaojiawei.hsscript.utils.GameUtil
import club.xiaojiawei.hsscript.utils.SystemUtil
import club.xiaojiawei.hsscript.utils.go
import club.xiaojiawei.status.WAR
import club.xiaojiawei.util.RandomUtil
import club.xiaojiawei.util.isFalse
import club.xiaojiawei.util.isTrue

/**
 * 卡牌策略执行器
 * @author 肖嘉威
 * @date 2022/11/29 17:29
 */
object DeckStrategyActuator {

    private val war = WAR

    fun reset() {
        DeckStrategyManager.currentDeckStrategy?.reset()

        checkSurrender()
    }

    fun randEmoji() {
        if (!canExec()) return

        val random = RandomUtil.RANDOM
        (random.nextInt() and 1 == 1).isTrue {
            (random.nextInt() and 1 == 1).isTrue {
                GameUtil.sendThankEmoji()
            }.isFalse {
                GameUtil.sendGreetEmoji()
            }
        }
    }

    /**
     * 非本人回合随机做点事情
     */
    fun randomDoSomething() {
        if (!canExec()) return

        val random = RandomUtil.RANDOM
        if (random.nextInt() and 1 == 1) {
            log.info { "随机做点事情" }
            Thread.sleep(2000)
            val minTime = 5000
            val maxTime = 12000
            while (!PauseStatus.isPause && !war.isMyTurn && !Thread.interrupted() && Mode.currMode === ModeEnum.GAMEPLAY) {
                var toList = war.rival.playArea.cards.toList()
                for (card in toList) {
                    if (random.nextInt() and 1 == 1) {
                        card.action.lClick()
                        log.info { "点击敌方战场卡牌：${card}" }
                    }
                    SystemUtil.delay(minTime, maxTime)
                }
                SystemUtil.delay(minTime, maxTime)
                if (random.nextInt() and 1 == 1) {
                    war.rival.playArea.hero?.action?.lClick()
                    log.info { "点击敌方英雄" }
                }
                SystemUtil.delay(minTime, maxTime)
                if (random.nextInt() and 1 == 1) {
                    war.rival.playArea.power?.action?.lClick()
                    log.info { "点击敌方英雄技能" }
                }
                SystemUtil.delay(minTime, maxTime)
                toList = war.me.playArea.cards.toList()
                for (card in toList) {
                    if (random.nextInt() and 1 == 1) {
                        card.action.lClick()
                        log.info { "点击我方战场卡牌：${card}" }
                    }
                    SystemUtil.delay(minTime, maxTime)
                }
                SystemUtil.delay(minTime, maxTime)
            }
        }
    }

    fun changeCard() {
        if (!canExec()) return

//        等待动画结束，畸变模式会导致开局动画增加
        SystemUtil.delay(20000 + (if (ConfigUtil.getBoolean(ConfigEnum.DISTORTION)) 4500 else 0))
        if (PauseStatus.isPause) return
        log.info { "执行换牌策略" }
        war.run {
            log.info { "1号玩家牌库数量：" + player1.deckArea.cards.size }
            log.info { "2号玩家牌库数量：" + player2.deckArea.cards.size }
        }

        val me = war.me
        try {
            val copyHandCards = HashSet(me.handArea.cards)
            copyHandCards.removeIf { it.cardId == COIN_CARD_ID }

            DeckStrategyManager.currentDeckStrategy?.executeChangeCard(copyHandCards)
            for (i in me.handArea.cards.indices) {
                val card = me.handArea.cards[i]
                if (card.cardId == COIN_CARD_ID) continue
                if (!copyHandCards.contains(card)) {
                    log.info { "换掉起始卡牌：【entityId:" + card.entityId + "，entityName:" + card.entityName + "，cardId:" + card.cardId + "】" }
                    GameUtil.chooseDiscoverCard(i, me.handArea.cardSize())
                    SystemUtil.delayShortMedium()
                }
            }
            log.info { "执行换牌策略完毕" }
            checkSurrender()
        } finally {
            for (i in 0..2) {
                GameUtil.CONFIRM_RECT.lClick(false)
                SystemUtil.delayShort()
            }
        }
    }

    fun outCard() {
        if (!canExec()) return

        if (Mode.currMode !== ModeEnum.GAMEPLAY) {
            log.warn { "没有处于${ModeEnum.GAMEPLAY.comment}，但试图执行出牌方法，如脚本运行不正常请提交issue并附带游戏日志" }
            ScreenLogListener.logFilePath?.let {
                SystemUtil.openFile(it)
            }
        }

        val surrenderNumber = ConfigUtil.getInt(ConfigEnum.OVER_TURN_SURRENDER)

        if (surrenderNumber >= 0 && war.me.turn >= surrenderNumber) {
            log.info { "到达投降回合" }
            GameUtil.surrender()
            return
        }

        // 等待动画结束
        SystemUtil.delay(5000)
        if (!war.isMyTurn || PauseStatus.isPause) return
        log.info { "执行出牌策略" }

        try {
            war.me.safeRun {
                log.info { "回合开始可用水晶数：" + it.usableResource }
            }
            DeckStrategyManager.currentDeckStrategy?.executeOutCard()
            log.info { "执行出牌策略完毕" }
            checkSurrender()
        } finally {
            GameUtil.cancelAction()
            for (i in 0 until 20) {
                if (!war.isMyTurn) break
                if (i > 3) {
                    GameUtil.getThreeDiscoverCardRect(0).lClick()
                    SystemUtil.delayShortMedium()
                }
                GameUtil.END_TURN_RECT.lClick(false)
                SystemUtil.delayShortMedium()
            }
        }
    }

    fun discoverChooseCard(cards: List<Card>) {
        if (!canExec()) return

        log.info { "执行发现选牌策略" }

        SystemUtil.delayShortMedium()
        var index = -1
        try {
            index = DeckStrategyManager.currentDeckStrategy?.executeDiscoverChooseCard(*cards.toTypedArray()) ?: 0
        } catch (e: Exception) {
            log.error(e) { "执行发现选择策略异常" }
        } finally {
            if (index == -1) {
                index = 0
                GameUtil.chooseDiscoverCard(index, cards.size)
            }
        }
        war.me.let {
            GameUtil.chooseDiscoverCard(index, cards.size)
            SystemUtil.delayShort()
            val card = cards[index]
            log.info { "选择了第${index + 1}张：" + card.toSimpleString() }
        }
        log.info { "执行发现选牌策略完毕" }

        checkSurrender()
    }

    private fun canExec(): Boolean {
        return ConfigUtil.getBoolean(ConfigEnum.STRATEGY) && validPlayer() && !checkSurrender()
    }

    private fun validPlayer(): Boolean {
        if (!war.rival.isValid() && war.me.isValid()) {
            log.warn { "玩家无效" }
            return false
        }
        return true
    }

    private fun checkSurrender(): Boolean {
        DeckStrategyManager.currentDeckStrategy?.let {
            if (it.needSurrender) {
                go { GameUtil.surrender() }
                it.needSurrender = false
                return true
            }
        }
        return false
    }

}
