package club.xiaojiawei.bean.power

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.Player
import club.xiaojiawei.bean.PowerAction
import club.xiaojiawei.bean.TEST_CARD_ACTION
import club.xiaojiawei.bean.abs.ClickPower
import club.xiaojiawei.enums.CardTypeEnum
import club.xiaojiawei.status.War
import kotlin.random.Random

/**
 * 术士技能
 * @author 肖嘉威
 * @date 2024/9/22 18:13
 */
private val cardIds = arrayOf<String>(
    "HERO_07%bp",
    "HERO_07%hp",
    "CS2_056_H%",
    "VAN_HERO_07%bp",
)

class WarlockPower : ClickPower() {

    override fun generatePowerActions(war: War, player: Player): List<PowerAction> {
        val entityId = belongCard?.entityId ?: return emptyList()
        return listOf(
            PowerAction(
                { newWar ->
                    newWar.me.playArea.power?.action?.power()
                }, { newWar ->
                    newWar.me.playArea.hero?.let {
                        it.damage += 2
                    }
                    if (!newWar.me.handArea.isFull) {
                        val card = Card(TEST_CARD_ACTION).apply {
                            cost = Random.nextInt(11)
                            cardType = CardTypeEnum.SPELL
                        }
                        newWar.maxEntityId?.let { maxEntityId ->
                            card.entityId = (maxEntityId.toInt() + 1).toString()
                            newWar.maxEntityId = card.entityId
                        }
                        newWar.me.handArea.add(card)
                    }
                    newWar.me.resourcesUsed += 2
                    newWar.me.playArea.findByEntityId(entityId)?.isExhausted = true
                })
        )
    }

    override fun getCardId(): Array<String> {
        return cardIds
    }

    override fun createNewInstance(): CardAction {
        return WarlockPower()
    }
}