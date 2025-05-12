package club.xiaojiawei.bean.power

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.Player
import club.xiaojiawei.bean.PowerAction
import club.xiaojiawei.bean.War
import club.xiaojiawei.bean.abs.PointPower
import club.xiaojiawei.status.WAR

/**
 * 法师技能
 * @author 肖嘉威
 * @date 2024/9/22 18:13
 */
private val cardIds = arrayOf<String>(
    "HERO_08%bp",
    "HERO_08%hp",
    "DS1h_034_H%",
    "TB_MagicalGuardians_Fireblast",
    "TUTR_HERO_08%bp",
    "VAN_HERO_08%bp",
)

class MagePower : PointPower() {

    override fun generatePowerActions(war: War, player: Player): List<PowerAction> {
        val myTarget = mutableListOf<Card>()
        val rivalTarget = mutableListOf<Card>()
        myTarget.addAll(war.me.playArea.cards.reversed())
        war.me.playArea.hero?.let { hero ->
            myTarget.add(hero)
        }
        rivalTarget.addAll(war.rival.playArea.cards)
        war.rival.playArea.hero?.let { hero ->
            rivalTarget.add(hero)
        }
        val result = mutableListOf<PowerAction>()
        for (card in myTarget) {
            if (card.canHurt() && card.canBeTargetedByMyHeroPowers()) {
                result.add(PowerAction({ newWar ->
                    newWar.me.playArea.power?.let { myPower ->
                        myPower.action.lClick()?.pointTo(card)
                    }
                }, { newWar ->
                    spendSelfCost(newWar)
                    card.action.findSelf(newWar)?.injured(1)
                    findSelf(newWar)?.isExhausted = true
                }, belongCard))
            }
        }
        for (card in rivalTarget) {
            if (card.canHurt() && card.canBeTargetedByRivalHeroPowers()) {
                result.add(PowerAction({ newWar ->
                    newWar.me.playArea.power?.let { myPower ->
                        myPower.action.lClick()?.pointTo(card)
                    }
                }, { newWar ->
                    spendSelfCost(newWar)
                    card.action.findSelf(newWar)?.injured(1)
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
        return pointTo(WAR.rival.playArea.hero) != null
    }

    override fun createNewInstance(): CardAction {
        return MagePower()
    }
}