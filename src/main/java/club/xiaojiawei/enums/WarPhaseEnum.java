package club.xiaojiawei.enums;

import club.xiaojiawei.strategy.AbstractPhaseStrategy;
import club.xiaojiawei.strategy.phase.*;
import lombok.Getter;
import lombok.ToString;

/**
 * @author 肖嘉威
 * @date 2022/11/26 17:18
 */
@Getter
@ToString
public enum WarPhaseEnum {

    FILL_DECK_PHASE("填充牌库阶段", FillDeckAbstractPhaseStrategy.class),
    DRAWN_INIT_CARD_PHASE("摸起始手牌阶段", DrawnInitCardAbstractPhaseStrategy.class),
    REPLACE_CARD_PHASE("更换手牌阶段", ReplaceCardAbstractPhaseStrategy.class),
    SPECIAL_EFFECT_TRIGGER_PHASE("特殊效果触发阶段", SpecialEffectTriggerAbstractPhaseStrategy.class),
    GAME_TURN_PHASE("游戏回合阶段", GameTurnAbstractPhaseStrategy.class),
    GAME_OVER_PHASE("游戏结束阶段", GameOverAbstractPhaseStrategy.class);

    private final String comment;
    private final  Class<? extends AbstractPhaseStrategy<String>> phaseStrategyClass;
    private AbstractPhaseStrategy<String> abstractPhaseStrategy;

    public void setAbstractPhaseStrategy(AbstractPhaseStrategy<String> abstractPhaseStrategy) {
        this.abstractPhaseStrategy = abstractPhaseStrategy;
    }

    WarPhaseEnum(String comment, Class<? extends AbstractPhaseStrategy<String>> phaseStrategyClass) {
        this.comment = comment;
        this.phaseStrategyClass = phaseStrategyClass;
    }

}
