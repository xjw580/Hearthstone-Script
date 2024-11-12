package club.xiaojiawei.bean

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.abs.PointPower
import club.xiaojiawei.bean.area.PlayArea
import club.xiaojiawei.status.War.me

/**
 * 牧师技能
 * @author 肖嘉威
 * @date 2024/9/22 18:13
 */
class PriestPower : PointPower() {

    companion object {
        val cardIds = arrayOf<String>(
            "HERO_09%bp",
            "HERO_09%hp",
            "CS1h_001_H%",
            "HERO_09dbp_Copy",
            "VAN_HERO_09%bp",
        )
    }

    override fun getCardId(): Array<String> {
        return cardIds
    }

    override fun execPower(): Boolean {
        if (!lClick()) return false
        return pointTo(me.playArea.hero) != null
    }

    override fun createNewInstance(): CardAction {
        return PriestPower()
    }

}