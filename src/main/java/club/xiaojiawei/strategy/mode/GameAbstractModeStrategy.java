package club.xiaojiawei.strategy.mode;

import club.xiaojiawei.custom.LogRunnable;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.status.Mode;
import club.xiaojiawei.strategy.AbstractModeStrategy;
import club.xiaojiawei.utils.RandomUtil;
import club.xiaojiawei.utils.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static club.xiaojiawei.enums.ModeEnum.GAME_MODE;

/**
 * @author 肖嘉威
 * @date 2022/11/26 21:44
 */
@Slf4j
@Component
public class GameAbstractModeStrategy extends AbstractModeStrategy<Object> {

    public static final float GAME_MODE_BUTTON_VERTICAL_TO_BOTTOM_RATIO = (float) 0.475;
    ScheduledFuture<?> scheduledFuture;
    @Override
    public void wantEnter() {
        scheduledFuture = extraThreadPool.scheduleWithFixedDelay(() -> {
            if (isPause.get().get()){
                scheduledFuture.cancel(true);
            }else if (Mode.getCurrMode() != GAME_MODE){
                SystemUtil.updateGameRect();
                mouseUtil.leftButtonClick(
                        ((ScriptStaticData.GAME_RECT.right + ScriptStaticData.GAME_RECT.left) >> 1) + RandomUtil.getRandom(-15, 15),
                        (int) (ScriptStaticData.GAME_RECT.bottom - (ScriptStaticData.GAME_RECT.bottom - ScriptStaticData.GAME_RECT.top) * GAME_MODE_BUTTON_VERTICAL_TO_BOTTOM_RATIO) + RandomUtil.getRandom(-5, 5)
                );
            }else {
                scheduledFuture.cancel(true);
            }
        }, DELAY_TIME, INTERVAL_TIME, TimeUnit.MILLISECONDS);
    }
    @Override
    protected void afterEnter(Object o) {

    }
}
