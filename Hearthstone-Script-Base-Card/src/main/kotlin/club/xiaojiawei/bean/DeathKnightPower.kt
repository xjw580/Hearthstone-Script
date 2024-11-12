package club.xiaojiawei.bean

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.abs.ClickPower
import club.xiaojiawei.status.War.me

/**
 * 巫妖王技能
 * @author 肖嘉威
 * @date 2024/9/22 18:13
 */
open class DeathKnightPower : ClickPower() {

    companion object {
        val cardIds = arrayOf<String>(
            "HERO_11%bp",
            "HERO_11%hp",
            "RLK_Prologue_Arthas_%p",
            "RLK_Prologue_MalGanis_%p",
            "TUTR_HERO_11%bp"
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
        return DeathKnightPower()
    }
}