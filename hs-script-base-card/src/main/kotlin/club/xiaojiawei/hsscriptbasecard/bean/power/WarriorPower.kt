package club.xiaojiawei.hsscriptbasecard.bean.power

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.Player
import club.xiaojiawei.bean.PowerAction
import club.xiaojiawei.bean.War
import club.xiaojiawei.hsscriptbasecard.bean.abs.ClickPower

/**
 * 战士技能
 * @author 肖嘉威
 * @date 2024/9/22 18:13
 */
private val cardIds =
    arrayOf<String>(
        "HERO_01%bp",
        "HERO_01%hp",
        "CS2_102_H%",
        "TUTR_HERO_01%bp",
        "VAN_CS2_102_H%",
        "VAN_HERO_01%bp",
    )

class WarriorPower : ClickPower() {
    override fun generatePowerActions(
        war: War,
        player: Player,
    ): List<PowerAction> =
        listOf(
            PowerAction(
                { newWar ->
                    newWar.me.playArea.power
                        ?.action
                        ?.power()
                },
                { newWar ->
                    spendSelfCost(newWar)
                    newWar.me.playArea.hero?.let {
                        it.armor += 2
                    }
                    findSelf(newWar)?.isExhausted = true
                }, belongCard
            ),
        )

    override fun getCardId(): Array<String> = cardIds

    override fun createNewInstance(): CardAction = WarriorPower()
}
