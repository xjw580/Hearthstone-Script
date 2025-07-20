package club.xiaojiawei.hsscriptbasecard.bean.warrior

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.War

/**
 * [焦油爬行者](https://hearthstone.huijiwiki.com/wiki/Card/41418)
 * @author 肖嘉威
 * @date 2025/1/18 20:31
 */
private val cardIds = arrayOf<String>(
    "%UNG_928",
)

class TarCreeper : CardAction.DefaultCardAction() {

    override fun triggerTurnStart(war: War) {
        super.triggerTurnStart(war)
        findSelf(war)?.let { card ->
            if (card.area.player === war.currentPlayer) {
                card.atc -= 2
            }
        }
    }

    override fun triggerTurnEnd(war: War) {
        super.triggerTurnEnd(war)
        findSelf(war)?.let { card ->
            if (card.area.player === war.currentPlayer) {
                card.atc += 2
            }
        }
    }

    override fun createNewInstance(): CardAction {
        return TarCreeper()
    }

    override fun getCardId(): Array<String> {
        return cardIds
    }

}