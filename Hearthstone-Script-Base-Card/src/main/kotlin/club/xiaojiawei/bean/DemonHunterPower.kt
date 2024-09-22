package club.xiaojiawei.bean

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.abs.ClickPower

/**
 * 恶魔猎手技能
 * @author 肖嘉威
 * @date 2024/9/22 18:48
 */
class DemonHunterPower : ClickPower() {

    override fun getCardId(): String {
        return "HERO_10bp"
    }

    override fun createNewInstance(): CardAction {
        return DemonHunterPower()
    }
}