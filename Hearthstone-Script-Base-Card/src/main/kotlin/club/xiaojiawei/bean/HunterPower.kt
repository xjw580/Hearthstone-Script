package club.xiaojiawei.bean

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.abs.ClickPower

/**
 * 猎人技能
 * @author 肖嘉威
 * @date 2024/9/22 18:13
 */
class HunterPower : ClickPower() {

    override fun getCardId(): String {
        return "HERO_05bp"
    }
    override fun createNewInstance(): CardAction {
        return HunterPower()
    }
}