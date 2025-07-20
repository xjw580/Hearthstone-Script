package club.xiaojiawei.hsscriptbasecard.bean.warrior

import club.xiaojiawei.CardAction
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
                Thread.sleep(2000L)
            }, { newWar ->
                spendSelfCost(newWar)
                removeSelf(newWar)
                val damage = 1 + newWar.me.getSpellPower()
                var noDead = true
                val cards = (newWar.me.playArea.cards + newWar.rival.playArea.cards).filter { it.canHurt() }
                    .sortedBy { it.numTurnsInPlay }.reversed()
                var isWork: Boolean
                while (noDead || Thread.interrupted()) {
                    isWork = false
                    for (card in cards) {
                        if (card.canHurt()) {
                            isWork = true
                            card.injured(damage)
                            if (!card.isAlive()) {
                                noDead = false
                            }
                        }
                    }
                    if (!isWork) break
                }
            }, belongCard)
        )
    }

    override fun createNewInstance(): CardAction {
        return Bladestorm()
    }

    override fun getCardId(): Array<String> {
        return cardIds
    }

}