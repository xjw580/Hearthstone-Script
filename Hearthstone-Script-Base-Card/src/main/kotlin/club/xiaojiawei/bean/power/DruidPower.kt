package club.xiaojiawei.bean.power

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.Action
import club.xiaojiawei.bean.PlayAction
import club.xiaojiawei.bean.Player
import club.xiaojiawei.bean.PowerAction
import club.xiaojiawei.bean.abs.ClickPower
import club.xiaojiawei.status.War

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
        val entityId = belongCard?.entityId ?: return emptyList()
        return listOf(
            PowerAction(
                { newWar ->
                    newWar.me.playArea.power?.action?.power()
                }, { newWar ->
                    newWar.me.playArea.hero?.let {
                        it.atc++
                        it.armor++
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
        return DruidPower()
    }
}