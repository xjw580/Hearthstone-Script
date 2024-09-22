package club.xiaojiawei.bean

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.abs.PointPower

/**
 * 牧师技能
 * @author 肖嘉威
 * @date 2024/9/22 18:13
 */
class PriestPower : PointPower() {

    override fun getCardId(): String {
        return "HERO_09bp"
    }

    override fun createNewInstance(): CardAction {
        return PriestPower()
    }

}