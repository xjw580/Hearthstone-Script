package club.xiaojiawei.bean.warrior

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.PlayAction
import club.xiaojiawei.bean.Player
import club.xiaojiawei.bean.War

/**
 * [剑刃风暴](https://hearthstone.huijiwiki.com/wiki/Card/56504)
 * @author 肖嘉威
 * @date 2025/1/18 16:32
 */
private val cardIds = arrayOf<String>(
    "%BT_117",
)

class Bladestorm : CardAction.DefaultCardAction() {

    override fun generatePlayActions(war: War, player: Player): List<PlayAction> {
        return listOf(
            PlayAction({ newWar ->
                findSelf(newWar)?.action?.power()
            }, { newWar ->
                spendSelfCost(newWar)
                removeSelf(newWar)
                val damage = 1 + newWar.me.getSpellPower()
                val cardMap = mutableMapOf<Player, MutableList<Card>>()
                cardMap[newWar.me] = newWar.me.playArea.cards
                cardMap[newWar.rival] = newWar.rival.playArea.cards
                var noDead = true
//                todo 应该要按随从下场顺序依次受伤
                var count = 0
                while (noDead) {
                    if (newWar.me.playArea.isEmpty && newWar.rival.playArea.isEmpty) break
                    cardMap.forEach { (p, cards) ->
                        cards.toList().forEach { card ->
                            if (card.canHurt()) {
                                card.injured(damage)
                                count++
                                if (!card.isAlive()) {
                                    noDead = false
                                }
                            }
                        }
                    }
                }
                Thread.sleep(count * 250L)
            })
        )
    }

    override fun createNewInstance(): CardAction {
        return Bladestorm()
    }

    override fun getCardId(): Array<String> {
        return cardIds
    }

}