package club.xiaojiawei.strategy.mode;

import club.xiaojiawei.enums.ModeEnum;
import club.xiaojiawei.status.Mode;
import club.xiaojiawei.strategy.AbstractModeStrategy;
import club.xiaojiawei.utils.SystemUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 肖嘉威
 * @date 2022/12/10 22:35
 */
@Slf4j
public class FatalErrorAbstractModeStrategy extends AbstractModeStrategy {
    @Override
    public void intoMode() {

    }

    @Override
    protected void log() {
        Mode.setCurrMode(ModeEnum.FATAL_ERROR);
        log.info("切換到" + ModeEnum.FATAL_ERROR.getComment());
    }

    @Override
    protected void nextStep() {
        SystemUtil.shutdownGame();
    }
}
