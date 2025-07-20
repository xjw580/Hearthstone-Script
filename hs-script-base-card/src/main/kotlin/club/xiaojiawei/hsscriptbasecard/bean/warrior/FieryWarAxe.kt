package club.xiaojiawei.hsscriptbasecard.bean.warrior

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.PlayAction
import club.xiaojiawei.bean.Player
import club.xiaojiawei.bean.War

/**
 * [炽炎战斧](https://hearthstone.huijiwiki.com/wiki/Card/69534)
 * @author 肖嘉威
 * @date 2025/1/18 18:09
 */
private val cardIds = arrayOf<String>(
    "%CS2_106",
)

class FieryWarAxe : CardAction.DefaultCardAction() {

    override fun generatePlayActions(war: War, player: Player): List<PlayAction> {
        return listOf(
            PlayAction({ newWar ->
                findSelf(newWar)?.action?.power()
            }, { newWar ->
                spendSelfCost(newWar)
                removeSelf(newWar)?.let { card ->
                    newWar.me.playArea.add(card)
                }
            }, belongCard)
        )
    }

    override fun createNewInstance(): CardAction {
        return FieryWarAxe()
    }

    override fun getCardId(): Array<String> {
        return cardIds
    }

}