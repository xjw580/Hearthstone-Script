package club.xiaojiawei.bean

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.abs.ClickPower
import club.xiaojiawei.status.War.me

/**
 * 萨满技能
 * @author 肖嘉威
 * @date 2024/9/22 18:13
 */
class ShamanPower : ClickPower() {

    companion object {
        val cardIds = arrayOf<String>(
            "HERO_02%bp",
            "HERO_02%hp",
            "CS2_049_H%",
            "HERO_02ajbp_Copy",
            "VAN_HERO_02%bp",
        )
    }

    override fun getCardId(): Array<String> {
        return cardIds
    }

    override fun execPower(): Boolean {
        if (me.playArea.isFull) return false
        return super.execPower()
    }

    override fun createNewInstance(): CardAction {
        return ShamanPower()
    }
}