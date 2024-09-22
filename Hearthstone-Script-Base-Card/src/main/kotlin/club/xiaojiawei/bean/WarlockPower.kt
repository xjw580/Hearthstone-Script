package club.xiaojiawei.bean

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.abs.ClickPower

/**
 * 术士技能
 * @author 肖嘉威
 * @date 2024/9/22 18:13
 */
class WarlockPower : ClickPower(){

    override fun getCardId(): String {
        return "HERO_07bp"
    }

    override fun createNewInstance(): CardAction {
        return WarlockPower()
    }
}