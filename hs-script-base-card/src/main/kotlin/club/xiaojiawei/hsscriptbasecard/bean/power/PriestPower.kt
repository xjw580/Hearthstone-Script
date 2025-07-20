package club.xiaojiawei.hsscriptbasecard.bean.power

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.Player
import club.xiaojiawei.bean.PowerAction
import club.xiaojiawei.bean.War
import club.xiaojiawei.hsscriptbasecard.bean.abs.PointPower
import club.xiaojiawei.status.WAR
import kotlin.math.max

/**
 * 牧师技能
 * @author 肖嘉威
 * @date 2024/9/22 18:13
 */
private val cardIds = arrayOf<String>(
    "HERO_09%bp",
    "HERO_09%hp",
    "CS1h_001_H%",
    "HERO_09dbp_Copy",
    "VAN_HERO_09%bp",
)

class PriestPower : PointPower() {

    override fun generatePowerActions(war: War, player: Player): List<PowerAction> {
        val myTarget = mutableListOf<Card>()
        val rivalTarget = mutableListOf<Card>()
        myTarget.addAll(war.me.playArea.cards)
        war.me.playArea.hero?.let { hero ->
            myTarget.add(hero)
        }
        rivalTarget.addAll(war.rival.playArea.cards)
        war.rival.playArea.hero?.let { hero ->
            rivalTarget.add(hero)
        }
        val result = mutableListOf<PowerAction>()
        for (card in myTarget) {
            if (card.canBeTargetedByMyHeroPowers()) {
                result.add(
                    PowerAction({ newWar ->
                        newWar.me.playArea.power?.let { myPower ->
                            myPower.action.lClick()?.pointTo(card)
                        }
                    }, { newWar ->
                        spendSelfCost(newWar)
                        card.action.findSelf(newWar)?.let {
                            it.damage = max(it.damage - 2, 0)
                        }
                        findSelf(newWar)?.isExhausted = true
                    }, belongCard)
                )
            }
        }
        for (card in rivalTarget) {
            if (card.canBeTargetedByRivalHeroPowers()){
                result.add(PowerAction({ newWar ->
                    newWar.me.playArea.power?.let { myPower ->
                        myPower.action.lClick()?.pointTo(card)
                    }
                }, { newWar ->
                    spendSelfCost(newWar)
                    card.action.findSelf(newWar)?.let {
                        it.damage = max(it.damage - 2, 0)
                    }
                    findSelf(newWar)?.isExhausted = true
                }, belongCard))
            }
        }
        return result
    }

    override fun getCardId(): Array<String> {
        return cardIds
    }

    override fun execPower(): Boolean {
        if (!execLClick()) return false
        return pointTo(WAR.me.playArea.hero) != null
    }

    override fun createNewInstance(): CardAction {
        return PriestPower()
    }

}