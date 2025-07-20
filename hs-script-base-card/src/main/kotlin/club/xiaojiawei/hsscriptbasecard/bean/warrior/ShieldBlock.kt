package club.xiaojiawei.hsscriptbasecard.bean.warrior

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.PlayAction
import club.xiaojiawei.bean.Player
import club.xiaojiawei.bean.War

/**
 * [盾牌格挡](https://hearthstone.huijiwiki.com/wiki/Card/68479)
 * @author 肖嘉威
 * @date 2025/1/18 18:14
 */
private val cardIds = arrayOf<String>(
    "%EX1_606",
)

class ShieldBlock : CardAction.DefaultCardAction() {

    override fun generatePlayActions(war: War, player: Player): List<PlayAction> {
        return listOf(
            PlayAction({ newWar ->
                findSelf(newWar)?.action?.power()
            }, { newWar ->
                spendSelfCost(newWar)
                removeSelf(newWar)?.let { card ->
                    newWar.me.playArea.hero?.let { hero ->
                        hero.armor += 5
                        newWar.me.handArea.drawCard()
                    }
                }
            }, belongCard)
        )
    }

    override fun createNewInstance(): CardAction {
        return ShieldBlock()
    }

    override fun getCardId(): Array<String> {
        return cardIds
    }

}