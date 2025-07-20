package club.xiaojiawei.hsscriptbasecard.bean.power

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.Player
import club.xiaojiawei.bean.PowerAction
import club.xiaojiawei.hsscriptbasecard.bean.abs.ClickPower
import club.xiaojiawei.bean.War

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
        return listOf(
            PowerAction(
                { newWar ->
                    newWar.me.playArea.power?.action?.power()
                }, { newWar ->
                    spendSelfCost(newWar)
                    newWar.me.playArea.hero?.let {
                        it.atc++
                    }
                    findSelf(newWar)?.isExhausted = true
                }, belongCard)
        )
    }

    override fun getCardId(): Array<String> {
        return cardIds
    }

    override fun createNewInstance(): CardAction {
        return DemonHunterPower()
    }

}