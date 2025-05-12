package club.xiaojiawei.bean.power

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.Player
import club.xiaojiawei.bean.PowerAction
import club.xiaojiawei.bean.abs.ClickPower
import club.xiaojiawei.bean.War

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
        return listOf(
            PowerAction(
                { newWar ->
                    newWar.me.playArea.power?.action?.power()
                }, { newWar ->
                    newWar.me.playArea.hero?.injured(2)
                    newWar.me.handArea.drawCard()
                    newWar.me.usedResources += 2
                    findSelf(newWar)?.isExhausted = true
                }, belongCard)
        )
    }

    override fun getCardId(): Array<String> {
        return cardIds
    }

    override fun createNewInstance(): CardAction {
        return WarlockPower()
    }
}