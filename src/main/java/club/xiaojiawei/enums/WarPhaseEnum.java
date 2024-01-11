package club.xiaojiawei.enums;

import club.xiaojiawei.strategy.AbstractPhaseStrategy;
import club.xiaojiawei.strategy.phase.*;
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

    FILL_DECK_PHASE("填充牌库阶段", FillDeckPhaseStrategy.class),
    DRAWN_INIT_CARD_PHASE("摸起始手牌阶段", DrawnInitCardPhaseStrategy.class),
    REPLACE_CARD_PHASE("更换手牌阶段", ReplaceCardPhaseStrategy.class),
    SPECIAL_EFFECT_TRIGGER_PHASE("特殊效果触发阶段", SpecialEffectTriggerPhaseStrategy.class),
    GAME_TURN_PHASE("游戏回合阶段", GameTurnPhaseStrategy.class),
    GAME_OVER_PHASE("游戏结束阶段", GameOverPhaseStrategy.class);

    private final String comment;
    private final  Class<? extends AbstractPhaseStrategy> phaseStrategyClass;
    @Setter
    private AbstractPhaseStrategy abstractPhaseStrategy;

    WarPhaseEnum(String comment, Class<? extends AbstractPhaseStrategy> phaseStrategyClass) {
        this.comment = comment;
        this.phaseStrategyClass = phaseStrategyClass;
    }

}
