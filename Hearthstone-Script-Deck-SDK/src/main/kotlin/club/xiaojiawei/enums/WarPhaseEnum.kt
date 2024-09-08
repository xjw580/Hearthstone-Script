package club.xiaojiawei.enums;

import club.xiaojiawei.interfaces.PhaseStrategy;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 肖嘉威
 * @date 2022/11/26 17:18
 */
@Getter
@ToString
public enum WarPhaseEnum {

    FILL_DECK_PHASE("填充牌库阶段", "FillDeckPhaseStrategy"),
    DRAWN_INIT_CARD_PHASE("摸起始手牌阶段", "DrawnInitCardPhaseStrategy"),
    REPLACE_CARD_PHASE("更换手牌阶段", "ReplaceCardPhaseStrategy"),
    SPECIAL_EFFECT_TRIGGER_PHASE("特殊效果触发阶段", "SpecialEffectTriggerPhaseStrategy"),
    GAME_TURN_PHASE("游戏回合阶段", "GameTurnPhaseStrategy"),
    GAME_OVER_PHASE("游戏结束阶段", "GameOverPhaseStrategy");

    private static final String PACKAGE_NAME = "club.xiaojiawei.strategy.phase";

    private final String comment;
    private final String phaseStrategyClassName;
    @Setter
    private PhaseStrategy phaseStrategy;

    WarPhaseEnum(String comment, String phaseStrategyClassName) {
        this.comment = comment;
        this.phaseStrategyClassName = PACKAGE_NAME + "." + phaseStrategyClassName;
    }

}
