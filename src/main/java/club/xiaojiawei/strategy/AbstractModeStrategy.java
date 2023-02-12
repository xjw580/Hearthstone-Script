package club.xiaojiawei.strategy;

import club.xiaojiawei.run.Core;
import club.xiaojiawei.utils.RandomUtil;
import club.xiaojiawei.utils.SystemUtil;

import static club.xiaojiawei.constant.SystemConst.ROBOT;

/**
 * @author 肖嘉威
 * @date 2022/11/26 17:39
 */
public abstract class AbstractModeStrategy implements Strategy<Object>{

    protected final static int intervalTime = 5000;

    protected final static int delayTime = 1000;

    @Override
    public final void afterInto(){
        afterInto(null);
    }

    @Override
    public void afterInto(Object o) {
        SystemUtil.cancelAllTask();
        log();
        ROBOT.delay(RandomUtil.getMediumRandom());
        SystemUtil.frontWindow(Core.getGameHWND());
        nextStep();
    }

    public abstract void intoMode();

    protected abstract void log();

    protected abstract void nextStep();
}
