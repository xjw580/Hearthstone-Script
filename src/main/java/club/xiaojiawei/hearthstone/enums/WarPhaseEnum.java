package club.xiaojiawei.hearthstone.enums;

import club.xiaojiawei.hearthstone.strategy.AbstractPhaseStrategy;
import club.xiaojiawei.hearthstone.strategy.phase.*;

import java.util.function.Supplier;

/**
 * @author 肖嘉威
 * @date 2022/11/26 17:18
 */
public enum WarPhaseEnum {

    FILL_DECK_PHASE("填充牌库阶段", FillDeckAbstractPhaseStrategy::new),
    DRAWN_INIT_CARD_PHASE("摸起始手牌阶段", DrawnInitCardAbstractPhaseStrategy::new),
    REPLACE_CARD_PHASE("更换手牌阶段", ReplaceCardAbstractPhaseStrategy::new),
    SPECIAL_EFFECT_TRIGGER_PHASE("特殊效果触发阶段", SpecialEffectTriggerAbstractPhaseStrategy::new),
    GAME_TURN_PHASE("游戏回合阶段", GameTurnAbstractPhaseStrategy::new),
    GAME_OVER_PHASE("游戏结束阶段", GameOverAbstractPhaseStrategy::new);

    private final String comment;
    private final Supplier<AbstractPhaseStrategy> phaseStrategySupplier;

    WarPhaseEnum(String comment, Supplier<AbstractPhaseStrategy> phaseStrategySupplier) {
        this.comment = comment;
        this.phaseStrategySupplier = phaseStrategySupplier;
    }

    public Supplier<AbstractPhaseStrategy> getPhaseStrategySupplier() {
        return phaseStrategySupplier;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public String toString() {
        return "WarPhaseEnum{" +
                "comment='" + comment + '\'' +
                ", phaseStrategySupplier=" + phaseStrategySupplier +
                '}';
    }
}
