package club.xiaojiawei.bean.power

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.Action
import club.xiaojiawei.bean.PlayAction
import club.xiaojiawei.bean.Player
import club.xiaojiawei.bean.PowerAction
import club.xiaojiawei.bean.abs.ClickPower
import club.xiaojiawei.status.War

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
        val entityId = belongCard?.entityId ?: return emptyList()
        return listOf(
            PowerAction(
                { newWar ->
                    newWar.me.playArea.power?.action?.power()
                }, { newWar ->
                    newWar.rival.playArea.hero?.let {
                        it.damage += 2
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
        return HunterPower()
    }
}