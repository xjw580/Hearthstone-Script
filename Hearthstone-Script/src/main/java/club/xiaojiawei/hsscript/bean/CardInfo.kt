package club.xiaojiawei.hsscript.bean

import club.xiaojiawei.hsscript.enums.CardActionEnum
import club.xiaojiawei.hsscript.enums.CardEffectTypeEnum

/**
 * @author 肖嘉威
 * @date 2025/6/9 15:16
 */
class CardInfo {

    /**
     * 效果类型
     */
    var effectType: CardEffectTypeEnum

    /**
     * 行为
     */
    var actions: List<CardActionEnum>

    constructor(
        effectType: CardEffectTypeEnum = CardEffectTypeEnum.UNKNOWN,
        actions: List<CardActionEnum> = emptyList()
    ) {
        this.effectType = effectType
        this.actions = actions
    }

}