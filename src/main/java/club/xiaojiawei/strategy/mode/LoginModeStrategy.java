package club.xiaojiawei.strategy.mode;

import club.xiaojiawei.custom.LogRunnable;
import club.xiaojiawei.data.GameRationStaticData;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.strategy.AbstractModeStrategy;
import club.xiaojiawei.utils.SystemUtil;
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
public class LoginModeStrategy extends AbstractModeStrategy<Object> {

    private static ScheduledFuture<?> scheduledFuture;

    public static void cancelTask(){
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
                SystemUtil.updateGameRect();
                mouseUtil.leftButtonClick(
                        (ScriptStaticData.GAME_RECT.right + ScriptStaticData.GAME_RECT.left) >> 1,
                        (int) (ScriptStaticData.GAME_RECT.bottom - (ScriptStaticData.GAME_RECT.bottom - ScriptStaticData.GAME_RECT.top) * GameRationStaticData.CONFIRM_OR_CLOSE_BUTTON_VERTICAL_TO_BOTTOM_RATION)
                );
            }
        }), 3000, 2000, TimeUnit.MILLISECONDS);
    }

}
