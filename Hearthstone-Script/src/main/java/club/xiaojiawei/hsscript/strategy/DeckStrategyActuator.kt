package club.xiaojiawei.hsscript.strategy

import club.xiaojiawei.DeckStrategy
import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.isValid
import club.xiaojiawei.config.log
import club.xiaojiawei.enums.ModeEnum
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.status.Mode
import club.xiaojiawei.hsscript.status.PauseStatus
import club.xiaojiawei.hsscript.utils.ConfigUtil
import club.xiaojiawei.hsscript.utils.GameUtil
import club.xiaojiawei.hsscript.utils.SystemUtil
import club.xiaojiawei.status.War
import club.xiaojiawei.status.War.isMyTurn
import club.xiaojiawei.status.War.player1
import club.xiaojiawei.status.War.player2
import kotlin.random.Random

/**
 * 卡牌策略抽象类
 * @author 肖嘉威
 * @date 2022/11/29 17:29
 */
object DeckStrategyActuator {

    var deckStrategy: DeckStrategy? = null
        set(value) {
            log.info { "本局游戏使用策略：【${value?.name()}】" }
            field = value
        }

    fun reset(){
        deckStrategy?.reset()

        checkSurrender()
    }

    /**
     * 非本人回合随机做点事情
     */
    fun randomDoSomething(){
        if (!ConfigUtil.getBoolean(ConfigEnum.STRATEGY)) return
        if (!validPlayer()) return
        if (Random.nextInt() and 1 == 1){
            log.info { "随机做点事情" }
            Thread.sleep(2000)
            val minTime = 4000
            val maxTime = 12000
            while (!PauseStatus.isPause && !isMyTurn && !Thread.interrupted() && Mode.currMode === ModeEnum.GAMEPLAY) {
                var toList = War.rival.playArea.cards.toList()
                for (card in toList) {
                    if (Random.nextInt() and 1 == 1) {
                        card.action.lClick()
                        log.info { "点击敌方战场卡牌：${card}" }
                    }
                    SystemUtil.delay(minTime, maxTime)
                }
                SystemUtil.delay(minTime, maxTime)
                if (Random.nextInt() and 1 == 1) {
                    War.rival.playArea.hero?.action?.lClick()
                    log.info { "点击敌方英雄" }
                }
                SystemUtil.delay(minTime, maxTime)
                if (Random.nextInt() and 1 == 1) {
                    War.rival.playArea.power?.action?.lClick()
                    log.info { "点击敌方英雄技能" }
                }
                SystemUtil.delay(minTime, maxTime)
                toList = War.me.playArea.cards.toList()
                for (card in toList) {
                    if (Random.nextInt() and 1 == 1) {
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
        if (!ConfigUtil.getBoolean(ConfigEnum.STRATEGY)) return
        if (!validPlayer()) return
        if (checkSurrender()) return
//        等待动画结束，畸变模式会导致开局动画增加
        SystemUtil.delay(20000 + (if (ConfigUtil.getBoolean(ConfigEnum.DISTORTION)) 4500 else 0))
        if (PauseStatus.isPause) return
        log.info { "执行换牌策略" }
        log.info { "1号玩家牌库数量：" + player1.deckArea.cards.size }
        log.info { "2号玩家牌库数量：" + player2.deckArea.cards.size }

        val me = War.me
        try {
            val copyHandCards = HashSet(me.handArea.cards)
            deckStrategy?.executeChangeCard(copyHandCards)
            for (i in me.handArea.cards.indices) {
                val card = me.handArea.cards[i]
                if (!copyHandCards.contains(card)) {
                    log.info { "换掉起始卡牌：【entityId:" + card.entityId + "，entityName:" + card.entityName + "，cardId:" + card.cardId + "】" }
                    GameUtil.clickDiscover(i, me.handArea.cardSize())
                    SystemUtil.delayShortMedium()
                }
            }
            log.info { "执行换牌策略完毕" }
        } finally {
            for (i in 0..2) {
                GameUtil.CONFIRM_RECT.lClick(false)
                SystemUtil.delayShort()
            }
        }

        checkSurrender()
    }

    fun outCard() {
        if (!ConfigUtil.getBoolean(ConfigEnum.STRATEGY)) return
        if (!validPlayer()) return
        if (checkSurrender()) return
        // 等待动画结束
        SystemUtil.delay(5000)
        if (!isMyTurn || PauseStatus.isPause) return
        log.info { "执行出牌策略" }

        try {
            War.me.let {
                log.info { "回合开始可用水晶数：" + it.usableResource }
            }
            deckStrategy?.executeOutCard()
            log.info { "执行出牌策略完毕" }
        } finally {
            GameUtil.cancelAction()
            for (i in 0 until 20) {
                if (!isMyTurn) break
                GameUtil.END_TURN_RECT.lClick(false)
                SystemUtil.delayShortMedium()
            }
        }

        checkSurrender()
    }

    fun discoverChooseCard(vararg cards: Card) {
        if (!ConfigUtil.getBoolean(ConfigEnum.STRATEGY)) return
        if (!validPlayer()) return
        if (checkSurrender()) return
        log.info { "执行发现选牌策略" }

        SystemUtil.delayShortMedium()
        val index = deckStrategy?.executeDiscoverChooseCard(*cards)?:0
        War.me.let {
            GameUtil.clickDiscover(index, it.handArea.cardSize())
            SystemUtil.delayShort()
            val card = cards[index]
            log.info { "选择了：" + card.toSimpleString() }
        }
        log.info { "执行发现选牌策略完毕" }

        checkSurrender()
    }

    private fun validPlayer():Boolean{
        if (!War.rival.isValid() && War.me.isValid()){
            log.warn { "玩家无效" }
            return false
        }
        return true
    }

    private fun checkSurrender(): Boolean{
        deckStrategy?.let {
            if (it.needSurrender){
                GameUtil.surrender()
                it.needSurrender = false
                return true
            }
        }
        return false
    }

}
