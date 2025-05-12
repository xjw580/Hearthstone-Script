package club.xiaojiawei.bean.warrior

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.PlayAction
import club.xiaojiawei.bean.Player
import club.xiaojiawei.bean.War

/**
 * [猛击](https://hearthstone.huijiwiki.com/wiki/Card/69638)
 * @author 肖嘉威
 * @date 2025/1/18 15:43
 */
private val cardIds = arrayOf<String>(
    "%EX1_391",
)

class Slam : CardAction.DefaultCardAction() {

    override fun generatePlayActions(war: War, player: Player): List<PlayAction> {
        val result = mutableListOf<PlayAction>()
        val rivalPlayCards = war.rival.playArea.cards
        for (rivalCard in rivalPlayCards) {
            if (rivalCard.canHurt() && rivalCard.canBeTargetedByRivalSpells()) {
                result.add(PlayAction({ newWar ->
                    findSelf(newWar)?.action?.power(rivalCard.action.findSelf(newWar))
                }, { newWar ->
                    spendSelfCost(newWar)
                    removeSelf(newWar)?.let {
                        rivalCard.action.findSelf(newWar)?.let { rCard->
                            rCard.injured(2 + newWar.me.getSpellPower())
                            if (rCard.isAlive()) {
                                newWar.me.handArea.drawCard()
                            }
                        }
                    }
                }, belongCard))
            }
        }
        return result
    }

    override fun createNewInstance(): CardAction {
        return Slam()
    }

    override fun getCardId(): Array<String> {
        return cardIds
    }
}