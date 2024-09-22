package club.xiaojiawei.strategy.mode;

import club.xiaojiawei.bean.GameRect;
import club.xiaojiawei.bean.LogRunnable;
import club.xiaojiawei.config.ThreadPoolConfigKt;
import club.xiaojiawei.interfaces.ModeStrategy;
import club.xiaojiawei.interfaces.closer.ModeTaskCloser;
import club.xiaojiawei.strategy.AbstractModeStrategy;
import club.xiaojiawei.utils.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 酒馆
 * @author 肖嘉威
 * @date 2022/11/25 12:43
 */
@Slf4j
@Component
public class BaconModeStrategy extends AbstractModeStrategy<Object> implements ModeTaskCloser {

    public static final GameRect BACK_RECT = new GameRect(0.3869D, 0.4429D, 0.4211D, 0.4507D);

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
