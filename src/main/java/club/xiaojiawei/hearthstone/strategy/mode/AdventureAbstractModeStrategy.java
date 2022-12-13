package club.xiaojiawei.hearthstone.strategy.mode;

import club.xiaojiawei.hearthstone.enums.ModeEnum;
import club.xiaojiawei.hearthstone.run.Core;
import club.xiaojiawei.hearthstone.status.Mode;
import club.xiaojiawei.hearthstone.strategy.AbstractModeStrategy;
import club.xiaojiawei.hearthstone.utils.MouseUtil;
import club.xiaojiawei.hearthstone.utils.RandomUtil;
import club.xiaojiawei.hearthstone.utils.SystemUtil;
import com.sun.jna.platform.win32.WinDef;
import lombok.extern.slf4j.Slf4j;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static club.xiaojiawei.hearthstone.constant.GameRatioConst.*;

/**
 * @author 肖嘉威
 * @date 2022/11/25 12:41
 * 冒险模式
 */
@Slf4j
public class AdventureAbstractModeStrategy extends AbstractModeStrategy {

    private static final float ADVENTURE_MODE_BUTTON_VERTICAL_TO_BOTTOM_RATIO = (float) 0.742;

    private static final float ADVENTURE_MODE_BUTTON_HORIZONTAL_TO_CENTER_RATIO = (float) 0.107;

    private static final float CHOOSE_BUTTON_HORIZONTAL_TO_CENTER_RATION = (float) 0.29;

    @Override
    public void intoMode() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (Mode.getCurrMode() == ModeEnum.HUB){
                    timer.cancel();
                    ModeEnum.GAME_MODE.getModeStrategy().get().intoMode();
                }else if (Mode.getCurrMode() == ModeEnum.GAME_MODE){
                    SystemUtil.frontWindow(Core.getGameHWND());
                    WinDef.RECT gameRECT = SystemUtil.getRect(Core.getGameHWND());
//                    点击冒险模式
                    MouseUtil.leftButtonClick(
                            (int) (((gameRECT.right + gameRECT.left) >> 1) - (gameRECT.bottom - gameRECT.top) * ADVENTURE_MODE_BUTTON_HORIZONTAL_TO_CENTER_RATIO * GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO + RandomUtil.getRandom(-15, 15)),
                            (int) (gameRECT.bottom - (gameRECT.bottom - gameRECT.top) * ADVENTURE_MODE_BUTTON_VERTICAL_TO_BOTTOM_RATIO) + RandomUtil.getRandom(-15, 15)
                    );
                    SystemUtil.delayMedium();
//                    点击选择按钮进入冒险模式
                    MouseUtil.leftButtonClick(
                            (int) (((gameRECT.right + gameRECT.left) >> 1) + (gameRECT.bottom - gameRECT.top) * CHOOSE_BUTTON_HORIZONTAL_TO_CENTER_RATION * GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO + RandomUtil.getRandom(-10, 10)),
                            (int) (gameRECT.bottom - (gameRECT.bottom - gameRECT.top) * SELECT_BUTTON_VERTICAL_TO_BOTTOM_RATIO) + RandomUtil.getRandom(-10, 10)
                    );
                }else {
                    timer.cancel();
                }
            }
        }, delayTime, intervalTime);
    }

    @Override
    protected void log() {
        Mode.setCurrMode(ModeEnum.ADVENTURE);
        log.info("切換到" + ModeEnum.ADVENTURE.getComment());
    }

    @Override
    protected void nextStep() {
        WinDef.RECT gameRECT = SystemUtil.getRect(Core.getGameHWND());
        clickStart(gameRECT);
        SystemUtil.delayLong();
        selectDeck(gameRECT);
        SystemUtil.delayMedium();
        clickStart(gameRECT);
        SystemUtil.delayLong();
        selectHero(gameRECT);
        SystemUtil.delayLong();
        clickStart(gameRECT);
    }

    public void clickStart(WinDef.RECT gameRECT){
        log.info("点击开始");
        SystemUtil.frontWindow(Core.getGameHWND());
        MouseUtil.leftButtonClick(
                (int) (((gameRECT.right + gameRECT.left) >> 1) + (gameRECT.bottom - gameRECT.top) * START_BUTTON_HORIZONTAL_TO_CENTER_RATIO * GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO + RandomUtil.getRandom(-10, 10)),
                (int) (gameRECT.bottom - (gameRECT.bottom - gameRECT.top) * START_BUTTON_VERTICAL_TO_BOTTOM_RATIO) + RandomUtil.getRandom(-10, 10)
        );
    }

    public void selectDeck(WinDef.RECT gameRECT){
        log.info("选择套牌");
        SystemUtil.frontWindow(Core.getGameHWND());
        MouseUtil.leftButtonClick(
                ((gameRECT.right + gameRECT.left) >> 1)  + RandomUtil.getRandom(-15, 15),
                (int) (gameRECT.bottom - (gameRECT.bottom - gameRECT.top) * FIRST_ROW_DECK_VERTICAL_TO_BOTTOM_RATIO) + RandomUtil.getRandom(-5, 5)
        );
    }

    public void selectHero(WinDef.RECT gameRECT){
//        todo 选择要挑战的英雄
    }

}
