package club.xiaojiawei.hearthstone.strategy;

import club.xiaojiawei.hearthstone.utils.RandomUtil;

import static club.xiaojiawei.hearthstone.constant.SystemConst.ROBOT;

/**
 * @author 肖嘉威
 * @date 2022/11/26 17:39
 */
public abstract class ModeStrategy implements Strategy{

    protected final static int intervalTime = 5000;

    protected final static int delayTime = 1000;

    @Override
    public final void afterInto(){
        log();
        ROBOT.delay(RandomUtil.getMediumRandom());
        nextStep();
    }

    public abstract void intoMode();

    protected abstract void log();

    protected abstract void nextStep();
}
