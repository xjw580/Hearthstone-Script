package club.xiaojiawei.strategy;

import club.xiaojiawei.interfaces.ModeStrategy;
import club.xiaojiawei.status.Mode;
import club.xiaojiawei.utils.GameUtil;
import club.xiaojiawei.utils.MouseUtil;
import club.xiaojiawei.utils.SystemUtil;
import jakarta.annotation.Resource;
import javafx.beans.property.BooleanProperty;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 游戏模式抽象类
 * @author 肖嘉威
 * @date 2022/11/26 17:39
 */
@Slf4j
public abstract class AbstractModeStrategy<T> implements ModeStrategy<T> {

    @Resource
    protected MouseUtil mouseUtil;
    @Resource
    protected GameUtil gameUtil;
    @Resource
    protected AtomicReference<BooleanProperty> isPause;
    protected final static int INTERVAL_TIME = 5000;
    protected final static int DELAY_TIME = 1000;

    @Override
    public abstract void wantEnter();
    @Override
    public void afterLeave(){}
    @Override
    public final void entering(){
        entering(null);
    }
    @Override
    public final void entering(T t) {
        beforeEnter();
        log();
        afterEnter(t);
    }
    protected abstract void afterEnter(T t);
    protected void beforeEnter(){
        SystemUtil.closeModeTask();
    }
    protected void log(){
        log.info("切換到" + Mode.getCurrMode().getComment());
    }

}
