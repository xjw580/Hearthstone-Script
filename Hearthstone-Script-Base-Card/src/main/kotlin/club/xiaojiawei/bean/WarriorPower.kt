package club.xiaojiawei.bean

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.abs.ClickPower

/**
 * 战士技能
 * @author 肖嘉威
 * @date 2024/9/22 18:13
 */
class WarriorPower : ClickPower() {

    override fun getCardId(): String {
        return "HERO_01bp"
    }

    override fun createNewInstance(): CardAction {
        return WarriorPower()
    }
}