package club.xiaojiawei.hearthstone.enums;

import club.xiaojiawei.hearthstone.strategy.PhaseStrategy;
import club.xiaojiawei.hearthstone.strategy.phase.*;

import java.util.function.Supplier;

/**
 * @author 肖嘉威
 * @date 2022/11/26 17:18
 */
public enum WarPhaseEnum {

    MY_TURN(MyTurnPhaseStrategy::new),
    RIVAL_TURN(RivalTurnPhaseStrategy::new),
    READY_TURN(ReadyTurnPhaseStrategy::new),
    END_TURN(EndTurnPhaseStrategy::new);

    private final Supplier<PhaseStrategy> phaseStrategySupplier;

    WarPhaseEnum(Supplier<PhaseStrategy> phaseStrategySupplier) {
        this.phaseStrategySupplier = phaseStrategySupplier;
    }

    public Supplier<PhaseStrategy> getPhaseStrategySupplier() {
        return phaseStrategySupplier;
    }
}
