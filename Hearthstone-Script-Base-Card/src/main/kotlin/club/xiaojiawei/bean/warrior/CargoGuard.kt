package club.xiaojiawei.bean.warrior

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.War

/**
 * [货物保镖](https://hearthstone.huijiwiki.com/wiki/Card/64278)
 * @author 肖嘉威
 * @date 2025/1/18 20:45
 */
private val cardIds = arrayOf<String>(
    "%SW_030",
)

class CargoGuard : CardAction.DefaultCardAction() {

    override fun triggerTurnEnd(war: War) {
        super.triggerTurnEnd(war)
        findSelf(war)?.let { card ->
            if (card.area.player === war.currentPlayer) {
                card.area.player.playArea.hero?.let { hero ->
                    hero.armor += 3
                }
            }
        }
    }

    override fun createNewInstance(): CardAction {
        return CargoGuard()
    }

    override fun getCardId(): Array<String> {
        return cardIds
    }

}