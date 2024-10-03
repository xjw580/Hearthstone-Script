package club.xiaojiawei.strategy.mode;

import club.xiaojiawei.bean.GameRect;
import club.xiaojiawei.bean.LogRunnable;
import club.xiaojiawei.config.ThreadPoolConfigKt;
import club.xiaojiawei.interfaces.closer.ModeTaskCloser;
import club.xiaojiawei.strategy.AbstractModeStrategy;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 乱斗
 * @author 肖嘉威
 * @date 2023/7/8 15:29
 */
@Component
public class TavernBrawlModeStrategy extends AbstractModeStrategy<Object> implements ModeTaskCloser {

    public static final GameRect BACK_RECT = new GameRect(0.4040D, 0.4591D, 0.4146D, 0.4474D);

    private ScheduledFuture<?> scheduledFuture;

    @Override
    public void wantEnter() {
    }

    @Override
    protected void afterEnter(Object o) {
        cancelTask();
        scheduledFuture = ThreadPoolConfigKt.getEXTRA_THREAD_POOL().scheduleWithFixedDelay(new LogRunnable(BACK_RECT::lClick), DELAY_TIME, 500, TimeUnit.MILLISECONDS);
    }

    private void cancelTask() {
        if (scheduledFuture != null && !scheduledFuture.isDone()){
            scheduledFuture.cancel(true);
        }
    }

    @Override
    public void closeModeTask() {
        cancelTask();
    }

}
