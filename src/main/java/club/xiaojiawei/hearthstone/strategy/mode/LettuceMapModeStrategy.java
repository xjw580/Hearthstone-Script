package club.xiaojiawei.hearthstone.strategy.mode;

import club.xiaojiawei.hearthstone.enums.ModeEnum;
import club.xiaojiawei.hearthstone.status.Mode;
import club.xiaojiawei.hearthstone.strategy.ModeStrategy;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 肖嘉威
 * @date 2022/11/25 12:42
 */
@Slf4j
public class LettuceMapModeStrategy extends ModeStrategy {

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
