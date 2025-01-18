package club.xiaojiawei.bean.warrior

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.PlayAction
import club.xiaojiawei.bean.Player
import club.xiaojiawei.enums.CardTypeEnum
import club.xiaojiawei.status.War
import java.util.function.BiConsumer

/**
 * [旋风斩](https://hearthstone.huijiwiki.com/wiki/Card/69556)
 * @author 肖嘉威
 * @date 2025/1/18 9:08
 */
private val cardIds = arrayOf<String>(
    "%EX1_400",
)

class Whirlwind : CardAction.DefaultCardAction() {

    override fun generatePlayActions(war: War, player: Player): List<PlayAction> {
        return listOf(
            PlayAction({ newWar ->
                findSelf(newWar)?.action?.power()
            }, { newWar ->
                spendSelfCost(newWar)
                removeSelf(newWar)
                val exec = BiConsumer<Player, Card> { player, card ->
                    if (card.cardType === CardTypeEnum.MINION) {
                        card.damage += 1 + newWar.me.getSpellPower()
                    }
                }
//                todo 应该要按随从下场顺序依次受伤
                newWar.me.playArea.cards.forEach { card ->
                    exec.accept(newWar.me, card)
                }
                newWar.rival.playArea.cards.forEach { card ->
                    exec.accept(newWar.rival, card)
                }
            })
        )
    }

    override fun createNewInstance(): CardAction {
        return Whirlwind()
    }

    override fun getCardId(): Array<String> {
        return cardIds
    }

}