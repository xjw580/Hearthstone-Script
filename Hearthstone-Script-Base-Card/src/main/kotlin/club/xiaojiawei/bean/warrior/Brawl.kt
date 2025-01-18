package club.xiaojiawei.bean.warrior

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.PlayAction
import club.xiaojiawei.bean.Player
import club.xiaojiawei.status.War
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
        return listOf(
            PlayAction({ newWar ->
                findSelf(newWar)?.action?.power()
            }, { newWar ->
                spendSelfCost(newWar)
                removeSelf(newWar)
                val size = newWar.me.playArea.cardSize() + newWar.rival.playArea.cardSize()
                val index = Random.nextInt(size)
                val aliveCard: Card
                if (index >= newWar.me.playArea.cardSize()) {
                    aliveCard = newWar.rival.playArea.cards[index - newWar.rival.playArea.cardSize()]
                } else {
                    aliveCard = newWar.me.playArea.cards[index]
                }
//                按照先下场的顺序依次死亡
                (newWar.me.playArea.cards.toList() + newWar.rival.playArea.cards.toList()).sortedBy { card: Card ->
                    card.numTurnsInPlay
                }.reversed().forEach {card: Card ->
                    if (card != aliveCard) {
                        card.damage = card.bloodLimit()
                    }
                }
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