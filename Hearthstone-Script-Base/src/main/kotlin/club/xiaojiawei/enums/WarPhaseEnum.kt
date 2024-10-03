package club.xiaojiawei.enums

import club.xiaojiawei.interfaces.PhaseStrategy

/**
 * @author 肖嘉威
 * @date 2022/11/26 17:18
 */
enum class WarPhaseEnum(val comment: String) {

    FILL_DECK_PHASE("填充牌库阶段"),
    DRAWN_INIT_CARD_PHASE("摸起始手牌阶段"),
    REPLACE_CARD_PHASE("更换手牌阶段"),
    SPECIAL_EFFECT_TRIGGER_PHASE("特殊效果触发阶段"),
    GAME_TURN_PHASE("游戏回合阶段"),
    GAME_OVER_PHASE("游戏结束阶段")
    ;

    var phaseStrategy: PhaseStrategy? = null

    companion object{
        fun find(phaseStrategy: PhaseStrategy):WarPhaseEnum? {
            return entries.find { it.phaseStrategy === phaseStrategy }
        }
    }

}
