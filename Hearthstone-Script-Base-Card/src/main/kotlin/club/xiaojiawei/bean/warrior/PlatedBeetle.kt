package club.xiaojiawei.bean.warrior

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.War

/**
 * [硬壳甲虫](https://hearthstone.huijiwiki.com/wiki/Card/45930)
 * @author 肖嘉威
 * @date 2025/1/18 18:17
 */
private val cardIds = arrayOf<String>(
    "%LOOT_413",
)

class PlatedBeetle : CardAction.DefaultCardAction() {

    override fun triggerDeathRattle(war: War) {
        super.triggerDeathRattle(war)
        findSelf(war)?.let { card: Card ->
            card.area.player.playArea.hero?.let { hero ->
                hero.armor += 3
            }
        }
    }

    override fun createNewInstance(): CardAction {
        return PlatedBeetle()
    }

    override fun getCardId(): Array<String> {
        return cardIds
    }

}