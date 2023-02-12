package club.xiaojiawei.strategy.mode;

import club.xiaojiawei.enums.ModeEnum;
import club.xiaojiawei.status.Mode;
import club.xiaojiawei.strategy.AbstractModeStrategy;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 肖嘉威
 * @date 2022/11/25 12:42
 */
@Slf4j
public class LettuceMapAbstractModeStrategy extends AbstractModeStrategy {

    @Override
    public void intoMode() {

    }

    @Override
    protected void log() {
        Mode.setCurrMode(ModeEnum.LETTUCE_MAP);
        log.info("切換到" + ModeEnum.LETTUCE_MAP.getComment());
    }

    @Override
    protected void nextStep() {

    }
}
