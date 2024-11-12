package club.xiaojiawei.bean

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.abs.ClickPower

/**
 * 德鲁伊技能
 * @author 肖嘉威
 * @date 2024/9/22 18:13
 */
class DruidPower : ClickPower(){

    companion object {
        val cardIds = arrayOf<String>(
            "HERO_06%bp",
            "HERO_06%hp",
            "CS2_017_HS%",
            "VAN_HERO_06%bp",
        )
    }

    override fun getCardId(): Array<String> {
        return cardIds
    }

    override fun createNewInstance(): CardAction {
        return DruidPower()
    }
}