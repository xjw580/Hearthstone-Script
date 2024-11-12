package club.xiaojiawei.bean

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.abs.ClickPower

/**
 * 恶魔猎手技能
 * @author 肖嘉威
 * @date 2024/9/22 18:48
 */
class DemonHunterPower : ClickPower() {

    companion object {
        val cardIds = arrayOf<String>(
            "HERO_10%bp",
            "HERO_10%hp",
            "RLK_Prologue_Illidan_%p",
            "VAN_HERO_10%bp",
        )
    }

    override fun getCardId(): Array<String> {
        return cardIds
    }

    override fun createNewInstance(): CardAction {
        return DemonHunterPower()
    }
}