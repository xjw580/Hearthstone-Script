package club.xiaojiawei.hsscriptbasecard.bean.power

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.Player
import club.xiaojiawei.bean.PowerAction
import club.xiaojiawei.hsscriptbasecard.bean.abs.ClickPower
import club.xiaojiawei.bean.War

/**
 * 猎人技能
 * @author 肖嘉威
 * @date 2024/9/22 18:13
 */
private val cardIds = arrayOf<String>(
    "HERO_05%bp",
    "HERO_05%hp",
    "DS1h_292_H%",
    "VAN_HERO_05%bp",
)

class HunterPower : ClickPower() {

    override fun generatePowerActions(war: War, player: Player): List<PowerAction> {
        return listOf(
            PowerAction(
                { newWar ->
                    newWar.me.playArea.power?.action?.power()
                }, { newWar ->
                    spendSelfCost(newWar)
                    newWar.rival.playArea.hero?.injured(2)
                    findSelf(newWar)?.isExhausted = true
                }, belongCard)
        )
    }


    override fun getCardId(): Array<String> {
        return cardIds
    }

    override fun createNewInstance(): CardAction {
        return HunterPower()
    }
}