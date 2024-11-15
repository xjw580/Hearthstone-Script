package club.xiaojiawei.enums

import club.xiaojiawei.interfaces.PhaseStrategy

/**
 * @author 肖嘉威
 * @date 2022/11/26 17:18
 */
enum class WarPhaseEnum(val comment: String) {

    FILL_DECK("填充牌库阶段"),
    DRAWN_INIT_CARD("摸起始手牌阶段"),
    REPLACE_CARD("更换手牌阶段"),
    SPECIAL_EFFECT_TRIGGER("特殊效果触发阶段"),
    GAME_TURN("游戏回合阶段"),
    GAME_OVER("游戏结束阶段")
    ;

    var phaseStrategy: PhaseStrategy? = null

    companion object {
        fun find(phaseStrategy: PhaseStrategy): WarPhaseEnum? {
            return values().find { it.phaseStrategy === phaseStrategy }
        }
    }

}
