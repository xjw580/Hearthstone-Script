package club.xiaojiawei.hearthstone.strategy.mode;

import club.xiaojiawei.hearthstone.enums.ModeEnum;
import club.xiaojiawei.hearthstone.status.Mode;
import club.xiaojiawei.hearthstone.status.War;
import club.xiaojiawei.hearthstone.strategy.AbstractModeStrategy;
import club.xiaojiawei.hearthstone.strategy.AbstractPhaseStrategy;
import club.xiaojiawei.hearthstone.utils.GameUtil;
import lombok.extern.slf4j.Slf4j;

import static club.xiaojiawei.hearthstone.constant.SystemConst.ROBOT;

/**
 * @author 肖嘉威
 * @date 2022/11/25 12:43
 */
@Slf4j
public class GameplayAbstractModeStrategy extends AbstractModeStrategy {

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
        if (Mode.getPrevMode() == ModeEnum.LOGIN || War.getCurrentPhase() != null){
//            投降
            log.info("当前对局不完整，直接投降");
            ROBOT.delay(3000);
            GameUtil.surrender();
        }
        AbstractPhaseStrategy.dealing = false;
    }

}
