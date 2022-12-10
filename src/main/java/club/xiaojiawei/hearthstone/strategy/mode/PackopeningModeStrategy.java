package club.xiaojiawei.hearthstone.strategy.mode;

import club.xiaojiawei.hearthstone.enums.ModeEnum;
import club.xiaojiawei.hearthstone.status.Mode;
import club.xiaojiawei.hearthstone.strategy.ModeStrategy;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 肖嘉威
 * @date 2022/11/25 12:40
 */
@Slf4j
public class PackopeningModeStrategy extends ModeStrategy {

    @Override
    public void intoMode() {

    }

    @Override
    protected void log() {
        Mode.setCurrMode(ModeEnum.PACKOPENING);
        log.info("切換到" + ModeEnum.PACKOPENING.getComment());
    }

    @Override
    protected void nextStep() {

    }
}
