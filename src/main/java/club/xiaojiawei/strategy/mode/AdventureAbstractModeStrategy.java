package club.xiaojiawei.strategy.mode;

import club.xiaojiawei.custom.LogRunnable;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.enums.ModeEnum;
import club.xiaojiawei.status.Mode;
import club.xiaojiawei.strategy.AbstractModeStrategy;
import club.xiaojiawei.utils.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static club.xiaojiawei.data.GameStaticData.*;
import static club.xiaojiawei.data.ScriptStaticData.GAME_RECT;
import static club.xiaojiawei.enums.ModeEnum.GAME_MODE;


/**
 * @author 肖嘉威
 * @date 2022/11/25 12:41
 * 冒险模式
 */
@Slf4j
@Component
public class AdventureAbstractModeStrategy extends AbstractModeStrategy<Object> {
    private static final float ADVENTURE_MODE_BUTTON_VERTICAL_TO_BOTTOM_RATIO = (float) 0.742;
    private static final float ADVENTURE_MODE_BUTTON_HORIZONTAL_TO_CENTER_RATIO = (float) 0.107;
    private static final float CHOOSE_BUTTON_HORIZONTAL_TO_CENTER_RATION = (float) 0.29;
    private static ScheduledFuture<?> wantEnterSchedule;

    @Override
    public void wantEnter() {
        wantEnterSchedule = extraThreadPool.scheduleWithFixedDelay(new LogRunnable(() -> {
            if (isPause.get().get()){
                wantEnterSchedule.cancel(true);
            }else if (Mode.getCurrMode() == ModeEnum.HUB) {
                wantEnterSchedule.cancel(true);
                GAME_MODE.getAbstractModeStrategy().wantEnter();
            } else if (Mode.getCurrMode() == GAME_MODE) {
                systemUtil.frontWindow(ScriptStaticData.getGameHWND());
                systemUtil.updateRect(ScriptStaticData.getGameHWND(), GAME_RECT);
//                    点击冒险模式
                mouseUtil.leftButtonClick(
                        (int) (((GAME_RECT.right + GAME_RECT.left) >> 1) - (GAME_RECT.bottom - GAME_RECT.top) * ADVENTURE_MODE_BUTTON_HORIZONTAL_TO_CENTER_RATIO * GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO + RandomUtil.getRandom(-15, 15)),
                        (int) (GAME_RECT.bottom - (GAME_RECT.bottom - GAME_RECT.top) * ADVENTURE_MODE_BUTTON_VERTICAL_TO_BOTTOM_RATIO) + RandomUtil.getRandom(-15, 15)
                );
                systemUtil.delayMedium();
//                    点击选择按钮进入冒险模式
                mouseUtil.leftButtonClick(
                        (int) (((GAME_RECT.right + GAME_RECT.left) >> 1) + (GAME_RECT.bottom - GAME_RECT.top) * CHOOSE_BUTTON_HORIZONTAL_TO_CENTER_RATION * GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO + RandomUtil.getRandom(-10, 10)),
                        (int) (GAME_RECT.bottom - (GAME_RECT.bottom - GAME_RECT.top) * SELECT_BUTTON_VERTICAL_TO_BOTTOM_RATIO) + RandomUtil.getRandom(-10, 10)
                );
            } else {
                wantEnterSchedule.cancel(true);
            }
        }), DELAY_TIME, INTERVAL_TIME, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void afterEnter(Object o) {
        systemUtil.updateRect(ScriptStaticData.getGameHWND(), GAME_RECT);
        clickStart();
        systemUtil.delayLong();
        selectDeck();
        systemUtil.delayMedium();
        clickStart();
        systemUtil.delayLong();
        selectHero();
        systemUtil.delayLong();
        clickStart();
    }

    private void clickStart(){
        log.info("点击开始");
        systemUtil.frontWindow(ScriptStaticData.getGameHWND());
        mouseUtil.leftButtonClick(
                (int) (((GAME_RECT.right + GAME_RECT.left) >> 1) + (GAME_RECT.bottom - GAME_RECT.top) * START_BUTTON_HORIZONTAL_TO_CENTER_RATIO * GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO + RandomUtil.getRandom(-10, 10)),
                (int) (GAME_RECT.bottom - (GAME_RECT.bottom - GAME_RECT.top) * START_BUTTON_VERTICAL_TO_BOTTOM_RATIO) + RandomUtil.getRandom(-10, 10)
        );
    }

    private void selectDeck(){
        log.info("选择套牌");
        systemUtil.frontWindow(ScriptStaticData.getGameHWND());
        mouseUtil.leftButtonClick(
                ((GAME_RECT.right + GAME_RECT.left) >> 1)  + RandomUtil.getRandom(-15, 15),
                (int) (GAME_RECT.bottom - (GAME_RECT.bottom - GAME_RECT.top) * FIRST_ROW_DECK_VERTICAL_TO_BOTTOM_RATIO) + RandomUtil.getRandom(-5, 5)
        );
    }

    public void selectHero(){
//        todo 选择要挑战的英雄
    }

}
