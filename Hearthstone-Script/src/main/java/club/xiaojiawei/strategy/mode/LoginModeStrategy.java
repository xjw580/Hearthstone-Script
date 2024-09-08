package club.xiaojiawei.strategy.mode;

import club.xiaojiawei.interfaces.closer.ModeTaskCloser;
import club.xiaojiawei.bean.LogRunnable;
import club.xiaojiawei.strategy.AbstractModeStrategy;
import club.xiaojiawei.utils.GameUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


/**
 * 登录界面
 * @author 肖嘉威
 * @date 2022/11/25 12:27
 */
@Slf4j
@Component
public class LoginModeStrategy extends AbstractModeStrategy<Object> implements ModeTaskCloser {

    private ScheduledFuture<?> scheduledFuture;

    private void cancelTask(){
        if (scheduledFuture != null && !scheduledFuture.isDone()){
            scheduledFuture.cancel(true);
        }
    }

    @Override
    public void wantEnter() {

    }

    @Override
    protected void afterEnter(Object o) {
        cancelTask();
//        去除国服登陆时恼人的点击开始和进入主界面时弹出的每日任务
        scheduledFuture = extraThreadPool.scheduleWithFixedDelay(new LogRunnable(() -> {
            if (isPause.get().get()){
                cancelTask();
            }else {
                GameUtil.lClickCenter();
            }
        }), 3000, 2000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void closeModeTask() {
        cancelTask();
    }

}
