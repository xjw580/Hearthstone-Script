package club.xiaojiawei.bean

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.abs.PointPower

/**
 * 法师技能
 * @author 肖嘉威
 * @date 2024/9/22 18:13
 */
class MagePower : PointPower() {

    override fun getCardId(): String {
        return "HERO_08bp"
    }

    override fun createNewInstance(): CardAction {
        return MagePower()
    }
}