package club.xiaojiawei.strategy.mode;

import club.xiaojiawei.custom.LogRunnable;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.strategy.AbstractModeStrategy;
import club.xiaojiawei.data.GameStaticData;
import club.xiaojiawei.utils.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


/**
 * @author 肖嘉威
 * @date 2022/11/25 12:27
 */
@Slf4j
@Component
public class LoginAbstractModeStrategy extends AbstractModeStrategy<Object> {
    private static ScheduledFuture<?> scheduledFuture;

    public static void cancelTask(){
        if (scheduledFuture != null && !scheduledFuture.isDone()){
            log.info("已取消国服登陆时的点击任务");
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
            if (!isPause.get().get()){
                SystemUtil.frontWindow(ScriptStaticData.getGameHWND());
                SystemUtil.updateRect(ScriptStaticData.getGameHWND(), ScriptStaticData.GAME_RECT);
                mouseUtil.leftButtonClick(
                        (ScriptStaticData.GAME_RECT.right + ScriptStaticData.GAME_RECT.left) >> 1,
                        (int) (ScriptStaticData.GAME_RECT.bottom - (ScriptStaticData.GAME_RECT.bottom - ScriptStaticData.GAME_RECT.top) * GameStaticData.CONFIRM_OR_CLOSE_BUTTON_VERTICAL_TO_BOTTOM_RATION)
                );
            }else {
                cancelTask();
            }
        }), 3000, 2000, TimeUnit.MILLISECONDS);
    }
}
