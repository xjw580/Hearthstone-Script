package club.xiaojiawei.strategy.mode;

import club.xiaojiawei.entity.WsResult;
import club.xiaojiawei.enums.DeckEnum;
import club.xiaojiawei.enums.DeckTypeEnum;
import club.xiaojiawei.enums.ModeEnum;
import club.xiaojiawei.run.Core;
import club.xiaojiawei.status.Deck;
import club.xiaojiawei.status.Mode;
import club.xiaojiawei.strategy.AbstractModeStrategy;
import club.xiaojiawei.utils.GameUtil;
import club.xiaojiawei.utils.MouseUtil;
import club.xiaojiawei.utils.RandomUtil;
import club.xiaojiawei.utils.SystemUtil;
import com.sun.jna.platform.win32.WinDef;
import lombok.extern.slf4j.Slf4j;

import java.util.Timer;
import java.util.TimerTask;

import static club.xiaojiawei.constant.GameMapConst.DECK_MAP;
import static club.xiaojiawei.constant.GameMapConst.DECK_TYPE_MAP;
import static club.xiaojiawei.constant.GameRatioConst.*;
import static club.xiaojiawei.constant.SystemConst.*;
import static club.xiaojiawei.enums.DeckTypeEnum.GENERAL;


/**
 * @author 肖嘉威
 * @date 2022/11/25 12:39
 * 传统对战
 */
@Slf4j
public class TournamentAbstractModeStrategy extends AbstractModeStrategy {

    public static final float TOURNAMENT_MODE_BUTTON_VERTICAL_TO_BOTTOM_RATIO = (float) 0.7;
    public static final float FIRST_DECK_BUTTON_HORIZONTAL_TO_CENTER_RATIO = (float) (0.333);
    public static final float ERROR_BUTTON_VERTICAL_TO_BOTTOM_RATION = (float) 0.395;
    public static final float CHANGE_MODE_BUTTON_VERTICAL_TO_BOTTOM_RATION = (float) 0.963;
    public static final float CHANGE_MODE_BUTTON_HORIZONTAL_TO_CENTER_RATION = (float) 0.313;
    public static final float CLASSIC_BUTTON_VERTICAL_TO_BOTTOM_RATION = (float) 0.581;
    public static final float CLASSIC_BUTTON_HORIZONTAL_TO_CENTER_RATION = (float) 0.34;
    private static Timer errorTimer;
    public static void cancelTimer(){
        if (errorTimer != null){
            errorTimer.cancel();
            errorTimer = null;
        }
    }

    @Override
    public void intoMode() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (Mode.getCurrMode() == ModeEnum.HUB){
                    SystemUtil.frontWindow(Core.getGameHWND());
                    WinDef.RECT gameRECT = SystemUtil.getRect(Core.getGameHWND());
                    MouseUtil.leftButtonClick(
                            ((gameRECT.right + gameRECT.left) >> 1) + RandomUtil.getRandom(-15, 15),
                            (int) (gameRECT.bottom - (gameRECT.bottom - gameRECT.top) * TOURNAMENT_MODE_BUTTON_VERTICAL_TO_BOTTOM_RATIO) + RandomUtil.getRandom(-5, 5)
                    );
                }else if (Mode.getCurrMode() == ModeEnum.GAME_MODE){
                    timer.cancel();
                    SystemUtil.frontWindow(Core.getGameHWND());
                    WinDef.RECT gameRECT = SystemUtil.getRect(Core.getGameHWND());
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
        if (ModeEnum.TOURNAMENT.getValue().equals(PROPERTIES.getProperty("mode"))){
            SystemUtil.delayMedium();
            clickModeChangeButton(gameRECT);
            SystemUtil.delayMedium();
            changeMode(gameRECT);
            SystemUtil.delayMedium();
            selectDeck(gameRECT);
            SystemUtil.delayShort();
            startMatching(gameRECT);
            generateTimer();
        }else {
            SystemUtil.frontWindow(Core.getGameHWND());
            GameUtil.clickBackButton(gameRECT);
        }
    }



    public void selectDeck(WinDef.RECT gameRECT){
        DeckEnum deck = DECK_MAP.get(PROPERTIES.getProperty("deck"));
        DeckTypeEnum ranked = DECK_TYPE_MAP.get(PROPERTIES.getProperty("ranked"));
        if (deck.getDeckType() != ranked && deck.getDeckType() != GENERAL){
            log.warn("指定套牌类型和当前模式不符");
            return;
        }
//        todo 发送套牌类型到GUI
        Deck.setDeck(deck);
        log.info("选择套牌:" + deck.getComment());
        SystemUtil.frontWindow(Core.getGameHWND());
        MouseUtil.leftButtonClick(
                (int) (((gameRECT.right + gameRECT.left) >> 1) - (gameRECT.bottom - gameRECT.top) * FIRST_DECK_BUTTON_HORIZONTAL_TO_CENTER_RATIO * GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO + RandomUtil.getRandom(-10, 10)),
                (int) (gameRECT.bottom - (gameRECT.bottom - gameRECT.top) * FIRST_ROW_DECK_VERTICAL_TO_BOTTOM_RATIO) + RandomUtil.getRandom(-5, 5)
        );
    }

    public void clickModeChangeButton(WinDef.RECT gameRECT){
        log.info("点击切换模式按钮");
        SystemUtil.frontWindow(Core.getGameHWND());
        MouseUtil.leftButtonClick(
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
        MouseUtil.leftButtonClick(
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

    public void startMatching(WinDef.RECT gameRECT){
        log.info("开始匹配");
        SystemUtil.frontWindow(Core.getGameHWND());
        //        重置游戏
        MouseUtil.leftButtonClick(
                (int) (((gameRECT.right + gameRECT.left) >> 1) + (gameRECT.bottom - gameRECT.top) * START_BUTTON_HORIZONTAL_TO_CENTER_RATIO * GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO + RandomUtil.getRandom(-10, 10)),
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
                if (Core.getPause()){
                    ROBOT.delay(REST_TIME * 1000);
                    run();
                }else {
                    log.info("游戏网络出现问题，匹配失败，再次匹配中");
                    SystemUtil.notice("游戏网络出现问题，匹配失败，再次匹配中");
                    SystemUtil.frontWindow(Core.getGameHWND());
                    WinDef.RECT gameRECT = SystemUtil.getRect(Core.getGameHWND());
                    MouseUtil.leftButtonClick(
                            ((gameRECT.right + gameRECT.left) >> 1) + RandomUtil.getRandom(-10, 10),
                            (int) (gameRECT.bottom - (gameRECT.bottom - gameRECT.top) * ERROR_BUTTON_VERTICAL_TO_BOTTOM_RATION) + RandomUtil.getRandom(-5, 5)
                    );
                    SystemUtil.delayMedium();
                    startMatching(gameRECT);
                }
            }
        }, 60000);
    }

}
