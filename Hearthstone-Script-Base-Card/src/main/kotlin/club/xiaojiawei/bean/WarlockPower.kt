package club.xiaojiawei.bean

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.abs.ClickPower

/**
 * 术士技能
 * @author 肖嘉威
 * @date 2024/9/22 18:13
 */
class WarlockPower : ClickPower(){

    companion object {
        val cardIds = arrayOf<String>(
            "HERO_07%bp",
            "HERO_07%hp",
            "CS2_056_H%",
            "VAN_HERO_07%bp",
        )
    }

    override fun getCardId(): Array<String> {
        return cardIds
    }

    override fun createNewInstance(): CardAction {
        return WarlockPower()
    }
}