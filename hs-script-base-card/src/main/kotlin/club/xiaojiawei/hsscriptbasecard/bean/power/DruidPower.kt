package club.xiaojiawei.hsscriptbasecard.bean.power

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.Player
import club.xiaojiawei.bean.PowerAction
import club.xiaojiawei.hsscriptbasecard.bean.abs.ClickPower
import club.xiaojiawei.bean.War

/**
 * 德鲁伊技能
 * @author 肖嘉威
 * @date 2024/9/22 18:13
 */
private val cardIds = arrayOf<String>(
    "HERO_06%bp",
    "HERO_06%hp",
    "CS2_017_HS%",
    "VAN_HERO_06%bp",
)

class DruidPower : ClickPower() {

    override fun generatePowerActions(war: War, player: Player): List<PowerAction> {
        return listOf(
            PowerAction(
                { newWar ->
                    newWar.me.playArea.power?.action?.power()
                }, { newWar ->
                    spendSelfCost(newWar)
                    newWar.me.playArea.hero?.let {
                        it.atc++
                        it.armor++
                    }
                    findSelf(newWar)?.isExhausted = true
                }, belongCard)
        )
    }

    override fun getCardId(): Array<String> {
        return cardIds
    }

    override fun createNewInstance(): CardAction {
        return DruidPower()
    }
}