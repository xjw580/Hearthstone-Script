package club.xiaojiawei.bean.warrior

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.PlayAction
import club.xiaojiawei.bean.Player
import club.xiaojiawei.bean.War

/**
 * [旋风斩](https://hearthstone.huijiwiki.com/wiki/Card/69556)
 * @author 肖嘉威
 * @date 2025/1/18 9:08
 */
private val cardIds = arrayOf<String>(
    "%EX1_400",
)

class Whirlwind : CardAction.DefaultCardAction() {

    companion object {
        private val exec: (Card, War) -> Unit = { card, newWar ->
            if (card.canHurt()) {
                card.injured(1 + newWar.me.getSpellPower())
            }
        }
    }

    override fun generatePlayActions(war: War, player: Player): List<PlayAction> {
        return listOf(
            PlayAction({ newWar ->
                findSelf(newWar)?.action?.power()
            }, { newWar ->
                spendSelfCost(newWar)
                removeSelf(newWar)

//                todo 应该要按随从下场顺序依次受伤
                val myCards = newWar.me.playArea.cards.toList()
                for (card in myCards.reversed()) {
                    exec(card, newWar)
                }
                val rivalCards = newWar.rival.playArea.cards.toList()
                for (card in rivalCards.reversed()) {
                    exec(card, newWar)
                }
            }, belongCard)
        )
    }

    override fun createNewInstance(): CardAction {
        return Whirlwind()
    }

    override fun getCardId(): Array<String> {
        return cardIds
    }

}