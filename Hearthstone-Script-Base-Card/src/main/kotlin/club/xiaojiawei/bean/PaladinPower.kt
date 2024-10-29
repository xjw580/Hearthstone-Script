package club.xiaojiawei.bean

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.abs.ClickPower
import club.xiaojiawei.bean.area.Area
import club.xiaojiawei.status.War.me

/**
 * 圣骑士技能
 * @author 肖嘉威
 * @date 2024/9/22 18:13
 */
class PaladinPower : ClickPower(){

    override fun getCardId(): String {
        return "HERO_04bp"
    }

    override fun execPower(): Boolean {
        if (me.playArea.isFull) return false
        return super.execPower()
    }

    override fun createNewInstance(): CardAction {
        return PaladinPower()
    }
}