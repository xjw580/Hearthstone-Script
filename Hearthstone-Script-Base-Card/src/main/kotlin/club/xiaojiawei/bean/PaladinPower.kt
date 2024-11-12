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

    companion object {
        val cardIds = arrayOf<String>(
            "HERO_04%bp",
            "HERO_04%hp",
            "CS2_101_H%",
            "RLK_Prologue_Arthas_001p",
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
        return PaladinPower()
    }
}