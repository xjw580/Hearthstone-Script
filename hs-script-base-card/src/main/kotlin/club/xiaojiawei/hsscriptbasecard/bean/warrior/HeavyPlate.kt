package club.xiaojiawei.hsscriptbasecard.bean.warrior

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.PlayAction
import club.xiaojiawei.bean.Player
import club.xiaojiawei.bean.War

/**
 * [厚重板甲](https://hearthstone.huijiwiki.com/wiki/Card/65656)
 * @author 肖嘉威
 * @date 2025/1/18 18:22
 */
private val cardIds = arrayOf<String>(
    "%SW_094",
)

class HeavyPlate : CardAction.DefaultCardAction() {

    override fun generatePlayActions(war: War, player: Player): List<PlayAction> {
        return listOf(
            PlayAction({ newWar ->
                findSelf(newWar)?.action?.power()
            }, { newWar ->
                spendSelfCost(newWar)
                removeSelf(newWar)?.let { card ->
                    newWar.me.playArea.hero?.let { hero ->
                        hero.armor += 8
                    }
                }
            }, belongCard)
        )
    }

    override fun createNewInstance(): CardAction {
        return HeavyPlate()
    }

    override fun getCardId(): Array<String> {
        return cardIds
    }

}