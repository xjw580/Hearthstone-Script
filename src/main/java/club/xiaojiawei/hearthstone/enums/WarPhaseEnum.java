package club.xiaojiawei.hearthstone.enums;

import club.xiaojiawei.hearthstone.strategy.PhaseStrategy;
import club.xiaojiawei.hearthstone.strategy.phase.*;

import java.util.function.Supplier;

/**
 * @author 肖嘉威
 * @date 2022/11/26 17:18
 */
public enum WarPhaseEnum {

    FILL_DECK_PHASE("填充牌库阶段", FillDeckPhaseStrategy::new),
    DRAWN_INIT_CARD_PHASE("摸起始手牌阶段", DrawnInitCardPhaseStrategy::new),
    REPLACE_CARD_PHASE("更换手牌阶段", ReplaceCardPhaseStrategy::new),
    SPECIAL_EFFECT_TRIGGER_PHASE("特殊效果触发阶段", SpecialEffectTriggerPhaseStrategy::new),
    GAME_TURN_PHASE("游戏回合阶段", GameTurnPhaseStrategy::new),
    GAME_OVER_PHASE("游戏结束阶段", GameOverPhaseStrategy::new);

    private final String comment;
    private final Supplier<PhaseStrategy> phaseStrategySupplier;

    WarPhaseEnum(String comment, Supplier<PhaseStrategy> phaseStrategySupplier) {
        this.comment = comment;
        this.phaseStrategySupplier = phaseStrategySupplier;
    }

    public Supplier<PhaseStrategy> getPhaseStrategySupplier() {
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
