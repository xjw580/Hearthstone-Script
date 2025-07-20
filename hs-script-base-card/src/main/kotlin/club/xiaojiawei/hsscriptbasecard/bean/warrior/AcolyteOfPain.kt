package club.xiaojiawei.hsscriptbasecard.bean.warrior

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.War

/**
 * [苦痛侍僧](https://hearthstone.huijiwiki.com/wiki/Card/69809)
 * @author 肖嘉威
 * @date 2025/1/18 20:41
 */
private val cardIds = arrayOf<String>(
    "%EX1_007",
)

class AcolyteOfPain : CardAction.DefaultCardAction() {

    override fun triggerDamage(war: War, damage: Int) {
        super.triggerDamage(war, damage)
        findSelf(war)?.let { card: Card ->
            card.area.player.handArea.drawCard()
        }
    }

    override fun createNewInstance(): CardAction {
        return AcolyteOfPain()
    }

    override fun getCardId(): Array<String> {
        return cardIds
    }
}