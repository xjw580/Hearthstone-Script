package club.xiaojiawei.hearthstone.strategy;

import club.xiaojiawei.hearthstone.run.Core;
import club.xiaojiawei.hearthstone.strategy.mode.LoginModeStrategy;
import club.xiaojiawei.hearthstone.strategy.mode.TournamentModeStrategy;
import club.xiaojiawei.hearthstone.strategy.phase.GameOverPhaseStrategy;
import club.xiaojiawei.hearthstone.utils.RandomUtil;
import club.xiaojiawei.hearthstone.utils.SystemUtil;

import static club.xiaojiawei.hearthstone.constant.SystemConst.ROBOT;

/**
 * @author 肖嘉威
 * @date 2022/11/26 17:39
 */
public abstract class ModeStrategy implements Strategy<Object>{

    protected final static int intervalTime = 5000;

    protected final static int delayTime = 1000;

    @Override
    public final void afterInto(){
        afterInto(null);
    }

    @Override
    public void afterInto(Object o) {
        LoginModeStrategy.cancelTimer();
        TournamentModeStrategy.cancelTimer();
        GameOverPhaseStrategy.cancelTimer();
        log();
        ROBOT.delay(RandomUtil.getMediumRandom());
        SystemUtil.frontWindow(Core.getGameHWND());
        nextStep();
    }

    public abstract void intoMode();

    protected abstract void log();

    protected abstract void nextStep();
}
