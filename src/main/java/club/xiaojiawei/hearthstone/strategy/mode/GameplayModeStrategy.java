package club.xiaojiawei.hearthstone.strategy.mode;

import club.xiaojiawei.hearthstone.enums.ModeEnum;
import club.xiaojiawei.hearthstone.listen.PowerFileListen;
import club.xiaojiawei.hearthstone.status.Mode;
import club.xiaojiawei.hearthstone.strategy.ModeStrategy;
import club.xiaojiawei.hearthstone.strategy.PhaseStrategy;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 肖嘉威
 * @date 2022/11/25 12:43
 */
@Slf4j
public class GameplayModeStrategy extends ModeStrategy {



    @Override
    public void intoMode() {

    }

    @Override
    protected void log() {
        Mode.setCurrMode(ModeEnum.GAMEPLAY);
        log.info("切換到" + ModeEnum.GAMEPLAY.getComment());
    }

    @Override
    protected void nextStep() {
        PhaseStrategy.dealing = false;
    }

}
