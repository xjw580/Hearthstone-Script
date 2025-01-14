package club.xiaojiawei.bean

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.abs.PointPower
import club.xiaojiawei.status.WAR

/**
 * 法师技能
 * @author 肖嘉威
 * @date 2024/9/22 18:13
 */
class MagePower : PointPower() {

    companion object {
        val cardIds = arrayOf<String>(
            "HERO_08%bp",
            "HERO_08%hp",
            "DS1h_034_H%",
            "TB_MagicalGuardians_Fireblast",
            "TUTR_HERO_08%bp",
            "VAN_HERO_08%bp",
        )
    }

    override fun getCardId(): Array<String> {
        return cardIds
    }

    override fun execPower(): Boolean {
        if (!lClick()) return false
        return pointTo(WAR.rival.playArea.hero) != null
    }

    override fun createNewInstance(): CardAction {
        return MagePower()
    }
}