package club.xiaojiawei.strategy;

import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.status.Mode;
import club.xiaojiawei.utils.GameUtil;
import club.xiaojiawei.utils.MouseUtil;
import club.xiaojiawei.utils.SystemUtil;
import javafx.beans.property.BooleanProperty;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author 肖嘉威
 * @date 2022/11/26 17:39
 */
@Slf4j
public abstract class AbstractModeStrategy<T>{
    @Resource
    protected MouseUtil mouseUtil;
    @Resource
    protected GameUtil gameUtil;
    @Resource
    protected AtomicReference<BooleanProperty> isPause;
    @Resource
    protected ScheduledThreadPoolExecutor extraThreadPool;
    protected final static int INTERVAL_TIME = 5000;
    protected final static int DELAY_TIME = 1000;

    public abstract void wantEnter();
    public final void entering(){
        entering(null);
    }
    public final void entering(T t) {
        beforeEnter();
        log();
        afterEnter(t);
    }
    protected void beforeEnter(){
        SystemUtil.stopAllThread();
        SystemUtil.cancelAllTask();
        SystemUtil.frontWindow(ScriptStaticData.getGameHWND());
    }
    protected void log(){
        log.info("切換到" + Mode.getCurrMode().getComment());
    }
    protected abstract void afterEnter(T t);
    public void afterLeave(){
    };
}
