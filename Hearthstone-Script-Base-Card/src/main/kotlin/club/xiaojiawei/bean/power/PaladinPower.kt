package club.xiaojiawei.bean.power

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.Player
import club.xiaojiawei.bean.PowerAction
import club.xiaojiawei.bean.TEST_CARD_ACTION
import club.xiaojiawei.bean.abs.ClickPower
import club.xiaojiawei.enums.CardTypeEnum
import club.xiaojiawei.status.WAR
import club.xiaojiawei.bean.War

/**
 * 圣骑士技能
 * @author 肖嘉威
 * @date 2024/9/22 18:13
 */
private val cardIds = arrayOf<String>(
    "HERO_04%bp",
    "HERO_04%hp",
    "CS2_101_H%",
    "RLK_Prologue_Arthas_001p",
)

class PaladinPower : ClickPower() {

    override fun generatePowerActions(war: War, player: Player): List<PowerAction> {
        if (war.me.playArea.isFull) return emptyList()
        return listOf(
            PowerAction(
                { newWar ->
                    newWar.me.playArea.power?.action?.power()
                }, { newWar ->
                    spendSelfCost(newWar)
                    val card = Card(TEST_CARD_ACTION).apply {
                        health = 1
                        atc = 1
                        cost = 1
                        cardType = CardTypeEnum.MINION
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
        return PaladinPower()
    }
}