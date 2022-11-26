package club.xiaojiawei.hearthstone.strategy.mode;

import club.xiaojiawei.hearthstone.enums.ModeEnum;
import club.xiaojiawei.hearthstone.run.Core;
import club.xiaojiawei.hearthstone.status.Mode;
import club.xiaojiawei.hearthstone.strategy.ModeStrategy;
import club.xiaojiawei.hearthstone.utils.GameUtil;
import club.xiaojiawei.hearthstone.utils.MouseUtil;
import club.xiaojiawei.hearthstone.utils.RandomUtil;
import club.xiaojiawei.hearthstone.utils.SystemUtil;
import com.sun.jna.platform.win32.WinDef;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Timer;
import java.util.TimerTask;

import static club.xiaojiawei.hearthstone.constant.GameConst.*;
import static club.xiaojiawei.hearthstone.constant.SystemConst.PROPERTIES;


/**
 * @author 肖嘉威
 * @date 2022/11/25 12:39
 * 传统对战
 */
@Slf4j
public class TournamentMode extends ModeStrategy {

    public static final float TOURNAMENT_MODE_BUTTON_VERTICAL_TO_BOTTOM_RATIO = (float) 0.7;
    public static final float DECK_BUTTON_HORIZONTAL_TO_CENTER_RATIO = (float) (0.24);
    public static final float ERROR_BUTTON_VERTICAL_TO_BOTTOM_RATION = (float) 0.395;
    public static final float CHANGE_MODE_BUTTON_VERTICAL_TO_BOTTOM_RATION = (float) 0.963;
    public static final float CHANGE_MODE_BUTTON_HORIZONTAL_TO_CENTER_RATION = (float) 0.23;
    public static final float CLASSIC_BUTTON_VERTICAL_TO_BOTTOM_RATION = (float) 0.591;
    public static final float CLASSIC_BUTTON_HORIZONTAL_TO_CENTER_RATION = (float) 0.25;
    public static Timer errorTimer;
    public static Timer closeTimer;
    public static void cancelTimer(){
        if (errorTimer != null){
            errorTimer.cancel();
            errorTimer = null;
        }
        if (closeTimer != null){
            closeTimer.cancel();
            closeTimer = null;
        }
    }

    @Override
    public void intoMode() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (Mode.getCurrMode() == ModeEnum.HUB){
                    WinDef.RECT gameRECT = SystemUtil.getRect(Core.getGameHWND());
                    SystemUtil.frontWindow(Core.getGameHWND());
                    MouseUtil.mouseLeftButtonClick(
                            ((gameRECT.right + gameRECT.left) >> 1) + RandomUtil.getRandom(-15, 15),
                            (int) (gameRECT.bottom - (gameRECT.bottom - gameRECT.top) * TOURNAMENT_MODE_BUTTON_VERTICAL_TO_BOTTOM_RATIO) + RandomUtil.getRandom(-5, 5)
                    );
                }else if (Mode.getCurrMode() == ModeEnum.GAME_MODE){
                    timer.cancel();
                    WinDef.RECT gameRECT = SystemUtil.getRect(Core.getGameHWND());
                    SystemUtil.frontWindow(Core.getGameHWND());
                    GameUtil.clickBackButton(gameRECT);
                }else {
                    timer.cancel();
                }
            }
        }, delayTime, intervalTime);
    }

    @Override
    protected void log() {
        Mode.setCurrMode(ModeEnum.TOURNAMENT);
        log.info("切換到" + ModeEnum.TOURNAMENT.getComment());
    }

    @Override
    protected void nextStep() {
        WinDef.RECT gameRECT = SystemUtil.getRect(Core.getGameHWND());
        if (ModeEnum.TOURNAMENT.getName().equals(PROPERTIES.getProperty("mode"))){
            clickModeChangeButton(gameRECT);
            SystemUtil.delayMedium();
            changeMode(gameRECT);
            SystemUtil.delayMedium();
            selectDeck(gameRECT);
            SystemUtil.delayMedium();
            startMatching(gameRECT);
            generateTimer();
        }else {
            SystemUtil.frontWindow(Core.getGameHWND());
            GameUtil.clickBackButton(gameRECT);
        }
    }

    public void selectDeck(WinDef.RECT gameRECT){
        log.info("选择套牌");
        SystemUtil.frontWindow(Core.getGameHWND());
        MouseUtil.mouseLeftButtonClick(
                (int) (((gameRECT.right + gameRECT.left) >> 1) - (gameRECT.bottom - gameRECT.top) * DECK_BUTTON_HORIZONTAL_TO_CENTER_RATIO * GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO + RandomUtil.getRandom(-10, 10)),
                (int) (gameRECT.bottom - (gameRECT.bottom - gameRECT.top) * FIRST_ROW_DECK_VERTICAL_TO_BOTTOM_RATIO) + RandomUtil.getRandom(-5, 5)
        );
    }

    public void clickModeChangeButton(WinDef.RECT gameRECT){
        log.info("点击切换模式按钮");
        SystemUtil.frontWindow(Core.getGameHWND());
        MouseUtil.mouseLeftButtonClick(
                (int) (((gameRECT.right + gameRECT.left) >> 1) + (gameRECT.bottom - gameRECT.top) * CHANGE_MODE_BUTTON_HORIZONTAL_TO_CENTER_RATION * GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO + RandomUtil.getRandom(-5, 5)),
                (int) (gameRECT.bottom - (gameRECT.bottom - gameRECT.top) * CHANGE_MODE_BUTTON_VERTICAL_TO_BOTTOM_RATION) + RandomUtil.getRandom(-5, 5)
        );
    }

    public void changeMode(WinDef.RECT gameRECT){
        SystemUtil.frontWindow(Core.getGameHWND());
        switch (PROPERTIES.getProperty("ranked")){
            case "CLASSIC" -> changeModeToClassic(gameRECT);
            case "STANDARD" -> changeModeToStandard(gameRECT);
            case "WILD" -> changeModeToWild(gameRECT);
            case "CASUAL" -> changeModeToCasual(gameRECT);
            default -> throw new RuntimeException("没有此天梯模式");
        }
    }

    public void changeModeToClassic(WinDef.RECT gameRECT){
        log.info("切换至经典模式");
        MouseUtil.mouseLeftButtonClick(
                (int) (((gameRECT.right + gameRECT.left) >> 1) - (gameRECT.bottom - gameRECT.top) * CLASSIC_BUTTON_HORIZONTAL_TO_CENTER_RATION * GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO + RandomUtil.getRandom(-5, 5)),
                (int) (gameRECT.bottom - (gameRECT.bottom - gameRECT.top) * CLASSIC_BUTTON_VERTICAL_TO_BOTTOM_RATION) + RandomUtil.getRandom(-5, 5)
        );
    }

    public void changeModeToStandard(WinDef.RECT gameRECT){
        log.info("切换至标准模式");
    }

    public void changeModeToWild(WinDef.RECT gameRECT){
        log.info("切换至狂野模式");
    }

    public void changeModeToCasual(WinDef.RECT gameRECT){
        log.info("切换至休闲模式");
    }

    public void startMatching( WinDef.RECT gameRECT){
        log.info("开始匹配");
        SystemUtil.frontWindow(Core.getGameHWND());
        MouseUtil.mouseLeftButtonClick(
                (int) (((gameRECT.right + gameRECT.left) >> 1) + ((gameRECT.bottom - gameRECT.top) * START_BUTTON_HORIZONTAL_TO_CENTER_RATIO) + RandomUtil.getRandom(-10, 10)),
                (int) (gameRECT.bottom - (gameRECT.bottom - gameRECT.top) * START_BUTTON_VERTICAL_TO_BOTTOM_RATIO) + RandomUtil.getRandom(-10, 10)
        );
    }

    /**
     * 生成匹配失败时兜底的定时器
     */
    public void generateTimer(){
        errorTimer = new Timer();
        errorTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                log.info("游戏网络出现问题，匹配失败，再次匹配中");
                SystemUtil.frontWindow(Core.getGameHWND());
                WinDef.RECT gameRECT = SystemUtil.getRect(Core.getGameHWND());
                MouseUtil.mouseLeftButtonClick(
                        ((gameRECT.right + gameRECT.left) >> 1) + RandomUtil.getRandom(-10, 10),
                        (int) (gameRECT.bottom - (gameRECT.bottom - gameRECT.top) * ERROR_BUTTON_VERTICAL_TO_BOTTOM_RATION) + RandomUtil.getRandom(-5, 5)
                );
                SystemUtil.delayShort();
                MouseUtil.mouseLeftButtonClick(
                        (int) (((gameRECT.right + gameRECT.left) >> 1) + ((gameRECT.bottom - gameRECT.top) * START_BUTTON_HORIZONTAL_TO_CENTER_RATIO) + RandomUtil.getRandom(-10, 10)),
                        (int) (gameRECT.bottom - (gameRECT.bottom - gameRECT.top) * START_BUTTON_VERTICAL_TO_BOTTOM_RATIO) + RandomUtil.getRandom(-10, 10)
                );
            }
        }, 15000);
        closeTimer = new Timer();
        closeTimer.schedule(new TimerTask() {
            @SneakyThrows
            @Override
            public void run() {
                log.info("无法匹配成功，准备重启游戏");
                Runtime runtime = Runtime.getRuntime();
                runtime.exec("taskkill /f /im Hearthstone.exe");
            }
        }, 30000);
    }

}
