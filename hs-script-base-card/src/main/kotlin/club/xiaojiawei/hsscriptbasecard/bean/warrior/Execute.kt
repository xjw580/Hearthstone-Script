package club.xiaojiawei.hsscriptbasecard.bean.warrior

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.PlayAction
import club.xiaojiawei.bean.Player
import club.xiaojiawei.enums.CardTypeEnum
import club.xiaojiawei.bean.War

/**
 * [斩杀](https://hearthstone.huijiwiki.com/wiki/Card/69535)
 * @author 肖嘉威
 * @date 2025/1/18 8:19
 */

private val cardIds = arrayOf<String>(
    "%CS2_108",
)

class Execute : CardAction.DefaultCardAction() {

    override fun generatePlayActions(war: War, player: Player): List<PlayAction> {
        val result = mutableListOf<PlayAction>()
        war.rival.playArea.cards.forEach { rivalCard ->
            if (rivalCard.cardType === CardTypeEnum.MINION && rivalCard.canBeTargetedByRivalSpells() && rivalCard.isInjured()) {
                result.add(PlayAction({ newWar ->
                    findSelf(newWar)?.action?.power(rivalCard.action.findSelf(newWar))
                }, { newWar ->
                    spendSelfCost(newWar)
                    removeSelf(newWar)?.let {
                        rivalCard.action.findSelf(newWar)?.let { rivalCard->
                            rivalCard.damage = rivalCard.bloodLimit()
                        }
                    }
                }, belongCard))
            }
        }
        return result
    }

    override fun createNewInstance(): CardAction {
        return Execute()
    }

    override fun getCardId(): Array<String> {
        return cardIds
    }

}