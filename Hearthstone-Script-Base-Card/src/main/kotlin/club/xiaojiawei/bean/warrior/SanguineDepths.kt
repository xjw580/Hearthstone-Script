package club.xiaojiawei.bean.warrior

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.Player
import club.xiaojiawei.bean.PowerAction
import club.xiaojiawei.enums.CardTypeEnum
import club.xiaojiawei.status.War
import java.util.function.Consumer

/**
 * [赤红深渊](https://baike.baidu.com/item/%E8%B5%A4%E7%BA%A2%E6%B7%B1%E6%B8%8A/61784480)
 * @author 肖嘉威
 * @date 2025/1/19 21:53
 */
private val cardIds = arrayOf<String>(
    "%REV_990",
)

class SanguineDepths : CardAction.DefaultCardAction() {

    override fun generatePowerActions(war: War, player: Player): List<PowerAction> {
        val result = mutableListOf<PowerAction>()
        val exec = Consumer<Card> { card ->
            result.add(
                PowerAction({ newWar ->
                    findSelf(newWar)?.action?.attack(card.action.findSelf(newWar))
                }, { newWar ->
                    findSelf(newWar)?.let { newMyCard ->
                        newMyCard.isLocationActionCooldown = true
                        card.action.findSelf(newWar)?.let { newCard ->
                            newMyCard.damage++
                            newCard.atc += 2
                            newCard.damage += 1
                        }
                    }
                })
            )
        }
        war.rival.playArea.cards.forEach { rivalCard ->
            if (rivalCard.cardType === CardTypeEnum.MINION) {
                exec.accept(rivalCard)
            }
        }
        war.me.playArea.cards.forEach { myCard ->
            if (myCard.cardType === CardTypeEnum.MINION) {
                exec.accept(myCard)
            }
        }
        return result
    }

    override fun createNewInstance(): CardAction {
        return SanguineDepths()
    }

    override fun getCardId(): Array<String> {
        return cardIds
    }

}