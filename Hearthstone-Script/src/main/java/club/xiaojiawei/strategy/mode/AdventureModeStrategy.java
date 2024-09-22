package club.xiaojiawei.strategy.mode;

import club.xiaojiawei.bean.GameRect;
import club.xiaojiawei.bean.LogRunnable;
import club.xiaojiawei.config.ThreadPoolConfigKt;
import club.xiaojiawei.enums.ModeEnum;
import club.xiaojiawei.interfaces.closer.ModeTaskCloser;
import club.xiaojiawei.status.Mode;
import club.xiaojiawei.strategy.AbstractModeStrategy;
import club.xiaojiawei.utils.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static club.xiaojiawei.enums.ModeEnum.GAME_MODE;


/**
 * 冒险模式
 * @author 肖嘉威
 * @date 2022/11/25 12:41
 */
@Slf4j
@Component
public class AdventureModeStrategy extends AbstractModeStrategy<Object> implements ModeTaskCloser {

//    todo add
    public static final GameRect ADVENTURE_RECT = GameRect.INVALID;
    public static final GameRect CHOOSE_RECT = GameRect.INVALID;
    public static final GameRect START_RECT = GameRect.INVALID;
    public static final GameRect SELECT_DECK_RECT = GameRect.INVALID;
    private static ScheduledFuture<?> wantEnterSchedule;

    @Override
    public void wantEnter() {
        wantEnterSchedule = ThreadPoolConfigKt.getEXTRA_THREAD_POOL().scheduleWithFixedDelay(new LogRunnable(() -> {
            if (isPause.get().get()){
                cancelTask();
            }else if (Mode.getCurrMode() == ModeEnum.HUB) {
                cancelTask();
                GAME_MODE.getModeStrategy().wantEnter();
            } else if (Mode.getCurrMode() == GAME_MODE) {
                SystemUtil.updateGameRect();
//                    点击冒险模式
                ADVENTURE_RECT.lClick();
                SystemUtil.delayMedium();
//                    点击选择按钮进入冒险模式
                CHOOSE_RECT.lClick();
            } else {
                wantEnterSchedule.cancel(true);
            }
        }), DELAY_TIME, INTERVAL_TIME, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void afterEnter(Object o) {
        SystemUtil.updateGameRect();
        clickStart();
        SystemUtil.delayLong();
        selectDeck();
        SystemUtil.delayMedium();
        clickStart();
        SystemUtil.delayLong();
        selectHero();
        SystemUtil.delayLong();
        clickStart();
    }

    private void clickStart(){
        log.info("点击开始");
        START_RECT.lClick();
    }

    private void selectDeck(){
        log.info("选择套牌");
        SELECT_DECK_RECT.lClick();
    }

    public void selectHero(){
    }

    private void cancelTask(){
        if (wantEnterSchedule != null && !wantEnterSchedule.isDone()){
            wantEnterSchedule.cancel(true);
        }
    }

    @Override
    public void closeModeTask() {
        cancelTask();
    }
}
