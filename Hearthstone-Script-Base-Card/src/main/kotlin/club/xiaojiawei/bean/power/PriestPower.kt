package club.xiaojiawei.bean.power

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.Player
import club.xiaojiawei.bean.PowerAction
import club.xiaojiawei.bean.abs.PointPower
import club.xiaojiawei.config.log
import club.xiaojiawei.status.WAR
import club.xiaojiawei.status.War
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
        val entityId = belongCard?.entityId ?: return emptyList()
        val myTarget = mutableListOf<Card>()
        val rivalTarget = mutableListOf<Card>()
        war.me.playArea.cards.forEach { card ->
            if (card.canBeTargetedByMyHeroPowers()) {
                myTarget.add(card)
            }
        }
        war.me.playArea.hero?.let { hero ->
            if (hero.canBeTargetedByMyHeroPowers()) {
                myTarget.add(hero)
            }
        }
        war.rival.playArea.cards.forEach { card ->
            if (card.canBeTargetedByMyHeroPowers()) {
                rivalTarget.add(card)
            }
        }
        war.rival.playArea.hero?.let { hero ->
            if (hero.canBeTargetedByRivalHeroPowers()) {
                rivalTarget.add(hero)
            }
        }
        val result = mutableListOf<PowerAction>()
        for (card in myTarget) {
            val targetEntityId = card.entityId
            result += PowerAction({ newWar ->
                newWar.me.playArea.power?.let { myPower ->
                    myPower.action.lClick()?.pointTo(card)
                }
            }, { newWar ->
                newWar.me.playArea.findByEntityId(targetEntityId)?.let {
                    it.damage = max(it.damage - 2, 0)
                } ?: let {
                    log.warn { "PowerAction查询战场卡牌失败,entityId:${entityId}" }
                }
                newWar.me.resourcesUsed += 2
                newWar.me.playArea.findByEntityId(entityId)?.isExhausted = true
            })
        }
        for (card in rivalTarget) {
            val targetEntityId = card.entityId
            result += PowerAction({ newWar ->
                newWar.me.playArea.power?.let { myPower ->
                    myPower.action.lClick()?.pointTo(card)
                }
            }, { newWar ->
                newWar.rival.playArea.findByEntityId(targetEntityId)?.let {
                    it.damage = max(it.damage - 2, 0)
                } ?: let {
                    log.warn { "PowerAction查询战场卡牌失败,entityId:${entityId}" }
                }
                newWar.me.resourcesUsed += 2
                newWar.me.playArea.findByEntityId(entityId)?.isExhausted = true
            })
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