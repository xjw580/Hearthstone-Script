package club.xiaojiawei.bean

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.abs.ClickPower

/**
 * 战士技能
 * @author 肖嘉威
 * @date 2024/9/22 18:13
 */
class WarriorPower : ClickPower() {

    companion object {
        val cardIds = arrayOf<String>(
            "HERO_01%bp",
            "HERO_01%hp",
            "CS2_102_H%",
            "TUTR_HERO_01%bp",
            "VAN_CS2_102_H%",
            "VAN_HERO_01%bp",
        )
    }

    override fun getCardId(): Array<String> {
        return cardIds
    }

    override fun createNewInstance(): CardAction {
        return WarriorPower()
    }
}