package club.xiaojiawei.bean

import club.xiaojiawei.CardAction
import club.xiaojiawei.config.log
import club.xiaojiawei.data.COIN_CARD_ID
import club.xiaojiawei.status.War

/**
 * 幸运币
 * @author 肖嘉威
 * @date 2025/1/17 15:22
 */
private val cardIds = arrayOf<String>(
    COIN_CARD_ID
)

class Coin : CardAction.DefaultCardAction() {

    override fun generatePlayActions(war: War, player: Player): List<PlayAction> {
        val entityId = belongCard?.entityId ?: return emptyList()
        return listOf(
            PlayAction({ newWar ->
                newWar.me.handArea.findByEntityId(entityId)?.let { card: Card ->
                    card.action.power()
                } ?: let {
                    log.warn { "PlayAction查找手中卡牌失败,entityId:${entityId}" }
                }
            }, { newWar ->
                newWar.me.handArea.removeByEntityId(entityId)?.let {
                    newWar.me.tempResources++
                } ?: let {
                    log.warn { "PlayAction移除手中卡牌失败,entityId:${entityId}" }
                }
            })
        )
    }

    override fun createNewInstance(): CardAction {
        return Coin()
    }

    override fun getCardId(): Array<String> {
        return cardIds
    }
}