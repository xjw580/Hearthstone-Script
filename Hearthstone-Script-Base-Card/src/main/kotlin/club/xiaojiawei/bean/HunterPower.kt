package club.xiaojiawei.bean

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.abs.ClickPower

/**
 * 猎人技能
 * @author 肖嘉威
 * @date 2024/9/22 18:13
 */
class HunterPower : ClickPower() {

    companion object {
        val cardIds = arrayOf<String>(
            "HERO_05%bp",
            "HERO_05%hp",
            "DS1h_292_H%",
            "VAN_HERO_05%bp",
        )
    }

    override fun getCardId(): Array<String> {
        return cardIds
    }

    override fun createNewInstance(): CardAction {
        return HunterPower()
    }
}