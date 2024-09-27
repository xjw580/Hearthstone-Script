package club.xiaojiawei.strategy

import club.xiaojiawei.DeckStrategy
import club.xiaojiawei.bean.Card
import club.xiaojiawei.config.log
import club.xiaojiawei.status.War
import club.xiaojiawei.status.War.isMyTurn
import club.xiaojiawei.utils.GameUtil
import club.xiaojiawei.utils.SystemUtil
import org.springframework.stereotype.Component

/**
 * 卡牌策略抽象类
 * @author 肖嘉威
 * @date 2022/11/29 17:29
 */
@Component
object DeckStrategyActuator {

    var deckStrategy: DeckStrategy? = null

    fun changeCard() {
        log.info { "执行换牌策略" }
        val me = War.me
        val rival = War.rival
        me ?: let {
            log.info { "me为null" }
            return
        }
        rival ?: let {
            log.info { "rival为null" }
            return
        }
        try {
            val copyHandCards = HashSet(me.handArea.cards)
            deckStrategy!!.executeChangeCard(copyHandCards)
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
                GameUtil.CONFIRM_RECT.lClick()
                SystemUtil.delayShort()
            }
        }
    }

    fun outCard() {
        try {
            log.info { "执行出牌策略" }
            War.me?.let {
                log.info { "回合开始可用水晶数：" + it.usableResource }
            } ?: let {
                log.info { "me为null" }
            }
            deckStrategy!!.executeOutCard()
            log.info { "执行出牌策略完毕" }
        } finally {
            GameUtil.cancelAction()
            for (i in 0 until 3) {
                if (!isMyTurn) break
                GameUtil.END_TURN_RECT.lClick()
                SystemUtil.delayMedium()
            }
        }
    }

    fun discoverChooseCard(vararg cards: Card) {
        SystemUtil.delay(1000)
        log.info { "执行发现选牌策略" }
        val index = deckStrategy!!.executeDiscoverChooseCard(*cards)
        War.me?.let {
            GameUtil.clickDiscover(index, it.handArea.cardSize())
            SystemUtil.delayShortMedium()
            val card = cards[index]
            log.info { "选择了：" + card.toSimpleString() }
        } ?: let {
            log.info { "me为null" }
        }
        log.info { "执行发现选牌策略完毕" }
    }
}
