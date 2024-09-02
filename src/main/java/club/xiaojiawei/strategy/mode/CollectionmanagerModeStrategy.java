package club.xiaojiawei.strategy.mode;

import club.xiaojiawei.bean.GameRect;
import club.xiaojiawei.interfaces.closer.ModeTaskCloser;
import club.xiaojiawei.custom.LogRunnable;
import club.xiaojiawei.strategy.AbstractModeStrategy;
import club.xiaojiawei.utils.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 我的收藏
 * @author 肖嘉威
 * @date 2022/11/25 12:40
 */
@Slf4j
@Component
public class CollectionmanagerModeStrategy extends AbstractModeStrategy<Object> implements ModeTaskCloser {

    public static final GameRect BACK_RECT = new GameRect(0.4041D, 0.4604D, 0.4122D, 0.4489D);

    private ScheduledFuture<?> scheduledFuture;

    @Override
    public void wantEnter() {
        SystemUtil.copyToClipboard(" ");
    }

    @Override
    protected void afterEnter(Object o) {
        cancelTask();
        scheduledFuture = extraThreadPool.scheduleWithFixedDelay(new LogRunnable(() -> {
            SystemUtil.updateGameRect();
            BACK_RECT.lClick();
        }), DELAY_TIME, 500, TimeUnit.MILLISECONDS);
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
