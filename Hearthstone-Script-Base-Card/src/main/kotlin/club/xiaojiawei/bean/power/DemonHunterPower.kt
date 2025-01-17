package club.xiaojiawei.bean.power

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.Action
import club.xiaojiawei.bean.PlayAction
import club.xiaojiawei.bean.Player
import club.xiaojiawei.bean.PowerAction
import club.xiaojiawei.bean.abs.ClickPower
import club.xiaojiawei.status.War

/**
 * 恶魔猎手技能
 * @author 肖嘉威
 * @date 2024/9/22 18:48
 */
private val cardIds = arrayOf<String>(
    "HERO_10%bp",
    "HERO_10%hp",
    "RLK_Prologue_Illidan_%p",
    "VAN_HERO_10%bp",
)

class DemonHunterPower : ClickPower() {

    override fun generatePowerActions(war: War, player: Player): List<PowerAction> {
        val entityId = belongCard?.entityId ?: return emptyList()
        return listOf(
            PowerAction(
                { newWar ->
                    newWar.me.playArea.power?.action?.power()
                }, { newWar ->
                    newWar.me.playArea.hero?.let {
                        it.atc++
                    }
                    newWar.me.resourcesUsed++
                    newWar.me.playArea.findByEntityId(entityId)?.isExhausted = true
                })
        )
    }

    override fun getCardId(): Array<String> {
        return cardIds
    }

    override fun createNewInstance(): CardAction {
        return DemonHunterPower()
    }

}