package club.xiaojiawei.bean.warrior

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.PlayAction
import club.xiaojiawei.bean.Player
import club.xiaojiawei.bean.War
import club.xiaojiawei.enums.CardTypeEnum
import club.xiaojiawei.util.CardUtil

/**
 * [疯狂巨龙死亡之翼](https://hearthstone.huijiwiki.com/wiki/Card/55007)
 * @author 肖嘉威
 * @date 2025/1/23 17:41
 */
private val cardIds = arrayOf<String>(
    "%DRG_026",
)


class DeathwingMadAspect : CardAction.DefaultCardAction() {

    override fun generatePlayActions(war: War, player: Player): List<PlayAction> {
        if ((war.me.playArea.cards + war.rival.playArea.cards).none { it.cardType === CardTypeEnum.MINION && !it.isUntouchable && !it.isDormantAwakenConditionEnchant }) {
            return super.generatePlayActions(war, player)
        }

        return listOf(
            PlayAction({ newWar ->
                findSelf(newWar)?.action?.power()
                Thread.sleep(3000L)
            }, { newWar ->
                spendSelfCost(newWar)
                val me = newWar.me
                removeSelf(newWar)?.let { card ->
                    CardUtil.handleCardExhaustedWhenIntoPlayArea(card)
                    if (me.playArea.safeAdd(card)) {
                        val cards = (war.me.playArea.cards + war.rival.playArea.cards).toList()
                        var running = true
                        while (running && !Thread.interrupted()) {
                            running = false
                            for (playCard in cards) {
                                if (playCard.isAlive() && playCard !== card) {
                                    running = true
                                    CardUtil.simulateAttack(newWar, card, playCard)
                                    if (card.isDead()) {
                                        running = false
                                        break
                                    }
                                }
                            }
                        }
                    }
                }
            }, belongCard)
        )
    }

    override fun createNewInstance(): CardAction {
        return DeathwingMadAspect()
    }

    override fun getCardId(): Array<String> {
        return cardIds
    }

}