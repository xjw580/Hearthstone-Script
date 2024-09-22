package club.xiaojiawei.strategy

import club.xiaojiawei.DeckStrategy
import club.xiaojiawei.bean.Card
import club.xiaojiawei.config.log
import club.xiaojiawei.enums.ConfigurationEnum
import club.xiaojiawei.status.War.isMyTurn
import club.xiaojiawei.status.War.me
import club.xiaojiawei.status.War.rival
import club.xiaojiawei.utils.GameUtil
import club.xiaojiawei.utils.SystemUtil
import jakarta.annotation.Resource
import javafx.beans.property.BooleanProperty
import lombok.Setter
import lombok.extern.slf4j.Slf4j
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.atomic.AtomicReference

/**
 * 卡牌策略抽象类
 * @author 肖嘉威
 * @date 2022/11/29 17:29
 */
@Component
object DeckStrategyActuator {

    var deckStrategy: DeckStrategy? = null

    fun changeCard() {
        val me = me
        val rival = rival
        try {
            log.info { "执行换牌策略" }
            val copyHandCards = HashSet(me!!.handArea.cards)
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
            log.info { "回合开始可用水晶数：" + me!!.usableResource }
            deckStrategy!!.executeOutCard()
            log.info { "执行出牌策略完毕" }
        } finally {
            GameUtil.cancelAction()
            var i = 0
            while (i < 3 && isMyTurn) {
                SystemUtil.delayMedium()
                GameUtil.END_TURN_RECT.lClick()
                i++
            }
        }
    }

    fun discoverChooseCard(vararg cards: Card) {
        SystemUtil.delay(1000)
        log.info { "执行发现选牌策略" }
        val index = deckStrategy!!.executeDiscoverChooseCard(*cards)
        GameUtil.clickDiscover(index, me!!.handArea.cardSize())
        SystemUtil.delayShortMedium()
        val card = cards[index]
        log.info { "选择了：" + card.toSimpleString() }
        log.info { "执行发现选牌策略完毕" }
    }
}
