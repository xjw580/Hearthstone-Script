package club.xiaojiawei.hsscriptbasecard.bean.power

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.Player
import club.xiaojiawei.bean.PowerAction
import club.xiaojiawei.bean.TEST_CARD_ACTION
import club.xiaojiawei.hsscriptbasecard.bean.abs.ClickPower
import club.xiaojiawei.enums.CardRaceEnum
import club.xiaojiawei.enums.CardTypeEnum
import club.xiaojiawei.status.WAR
import club.xiaojiawei.bean.War
import club.xiaojiawei.hsscriptbase.util.randomSelect

/**
 * 萨满技能
 * @author 肖嘉威
 * @date 2024/9/22 18:13
 */
private val cardIds = arrayOf(
    "HERO_02%bp",
    "HERO_02%hp",
    "CS2_049_H%",
    "HERO_02ajbp_Copy",
    "VAN_HERO_02%bp",
)
private val totems = listOf(
    Card(TEST_CARD_ACTION).apply {
        entityName = "空气之怒图腾"
        cardType = CardTypeEnum.MINION
        cardRace = CardRaceEnum.TOTEM
        cost = 1
        health = 2
        spellPower = 1
    },
    Card(TEST_CARD_ACTION).apply {
        entityName = "治疗图腾"
        cardType = CardTypeEnum.MINION
        cardRace = CardRaceEnum.TOTEM
        cost = 1
        health = 2
    },
    Card(TEST_CARD_ACTION).apply {
        entityName = "石爪图腾"
        cardType = CardTypeEnum.MINION
        cardRace = CardRaceEnum.TOTEM
        cost = 1
        health = 2
        isTaunt = true
    },
    Card(TEST_CARD_ACTION).apply {
        entityName = "灼热图腾"
        cardType = CardTypeEnum.MINION
        cardRace = CardRaceEnum.TOTEM
        cost = 1
        health = 1
        atc = 1
    },
)

class ShamanPower : ClickPower() {

    override fun generatePowerActions(war: War, player: Player): List<PowerAction> {
        if (war.me.playArea.isFull) return emptyList()
        return listOf(
            PowerAction(
                { newWar ->
                    newWar.me.playArea.power?.action?.power()
                }, { newWar ->
                    spendSelfCost(newWar)
                    val card = totems.randomSelect().clone().apply {
                        isExhausted = true
                        this.entityId = newWar.incrementMaxEntityId()
                    }
                    newWar.addCard(card, newWar.me.playArea)
                    findSelf(newWar)?.isExhausted = true
                }, belongCard)
        )
    }

    override fun getCardId(): Array<String> {
        return cardIds
    }

    override fun execPower(): Boolean {
        if (WAR.me.playArea.isFull) return false
        return super.execPower()
    }

    override fun createNewInstance(): CardAction {
        return ShamanPower()
    }
}