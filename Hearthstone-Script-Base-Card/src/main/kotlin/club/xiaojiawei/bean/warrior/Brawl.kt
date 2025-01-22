package club.xiaojiawei.bean.warrior

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.PlayAction
import club.xiaojiawei.bean.Player
import club.xiaojiawei.enums.CardTypeEnum
import club.xiaojiawei.bean.War
import kotlin.random.Random

/**
 * [绝命乱斗](https://hearthstone.huijiwiki.com/wiki/Card/69640)
 * @author 肖嘉威
 * @date 2025/1/18 20:47
 */
private val cardIds = arrayOf<String>(
    "%EX1_407",
)

class Brawl : CardAction.DefaultCardAction() {

    override fun generatePlayActions(war: War, player: Player): List<PlayAction> {
        if (war.me.playArea.cards.count { card: Card -> card.cardType === CardTypeEnum.MINION && !card.isDormantAwakenConditionEnchant }
            + war.rival.playArea.cards.count { card: Card -> card.cardType === CardTypeEnum.MINION && !card.isDormantAwakenConditionEnchant } < 2) return emptyList()
        return listOf(
            PlayAction({ newWar ->
                findSelf(newWar)?.action?.power()
            }, { newWar ->
                val newMyCards =
                    newWar.me.playArea.cards.filter { card: Card -> card.cardType === CardTypeEnum.MINION && !card.isDormantAwakenConditionEnchant }
                val newRivalCards =
                    newWar.rival.playArea.cards.filter { card: Card -> card.cardType === CardTypeEnum.MINION && !card.isDormantAwakenConditionEnchant }
                val size = newMyCards.size + newRivalCards.size
                if (size < 2) return@PlayAction
                spendSelfCost(newWar)
                removeSelf(newWar)

                val index = Random.nextInt(size)
                val aliveCard: Card
                if (index >= newMyCards.size) {
                    aliveCard = newRivalCards[index - newMyCards.size]
                } else {
                    aliveCard = newMyCards[index]
                }
//                按照先下场的顺序依次死亡
                (newMyCards + newRivalCards).sortedBy { card: Card ->
                    card.numTurnsInPlay
                }.reversed().forEach { card: Card ->
                    if (card.isAlive() && card != aliveCard) {
                        card.damage = card.bloodLimit()
                    }
                }
//                乱斗动画长
                Thread.sleep(3000L + size * 500L)
            })
        )
    }

    override fun createNewInstance(): CardAction {
        return Brawl()
    }

    override fun getCardId(): Array<String> {
        return cardIds
    }

}