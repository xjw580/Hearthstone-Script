package club.xiaojiawei.strategy.mode;

import club.xiaojiawei.bean.Deck;
import club.xiaojiawei.closer.ModeTaskCloser;
import club.xiaojiawei.core.Core;
import club.xiaojiawei.custom.LogRunnable;
import club.xiaojiawei.data.GameRationStaticData;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.enums.ConfigurationEnum;
import club.xiaojiawei.enums.DeckEnum;
import club.xiaojiawei.enums.ModeEnum;
import club.xiaojiawei.enums.RunModeEnum;
import club.xiaojiawei.listener.log.DeckLogListener;
import club.xiaojiawei.listener.log.PowerLogListener;
import club.xiaojiawei.status.Mode;
import club.xiaojiawei.status.Work;
import club.xiaojiawei.strategy.AbstractModeStrategy;
import club.xiaojiawei.utils.RandomUtil;
import club.xiaojiawei.utils.SystemUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static club.xiaojiawei.data.GameRationStaticData.CANCEL_MATCH_BUTTON_VERTICAL_TO_BOTTOM_RATION;
import static club.xiaojiawei.data.GameRationStaticData.FIRST_ROW_DECK_VERTICAL_TO_BOTTOM_RATIO;


/**
 * 传统对战
 * @author 肖嘉威
 * @date 2022/11/25 12:39
 */
@Slf4j
@Component
public class TournamentModeStrategy extends AbstractModeStrategy<Object> implements ModeTaskCloser {

    @Resource
    private Properties scriptConfiguration;
    @Resource
    private PowerLogListener powerLogListener;
    @Resource
    private Core core;
    private ScheduledFuture<?> scheduledFuture;
    private ScheduledFuture<?> errorScheduledFuture;
    private static final float TOURNAMENT_MODE_BUTTON_VERTICAL_TO_BOTTOM_RATIO = 0.7F;
    private static final float FIRST_DECK_BUTTON_HORIZONTAL_TO_CENTER_RATIO = 0.333F;
    private static final float ERROR_BUTTON_VERTICAL_TO_BOTTOM_RATION = 0.395F;
    private static final float CHANGE_MODE_BUTTON_VERTICAL_TO_BOTTOM_RATION = 0.963F;
    private static final float CHANGE_MODE_BUTTON_HORIZONTAL_TO_CENTER_RATION = 0.313F;
    private static final float CLASSIC_BUTTON_VERTICAL_TO_BOTTOM_RATION = 0.581F;
    private static final float CLASSIC_BUTTON_HORIZONTAL_TO_CENTER_RATION = 0.34F;
    private static final float STANDARD_BUTTON_VERTICAL_TO_BOTTOM_RATION = 0.714F;
    private static final float STANDARD_BUTTON_HORIZONTAL_TO_CENTER_RATION = 0.11F;

    private void cancelTask(){
        if (scheduledFuture != null && !scheduledFuture.isDone()){
            scheduledFuture.cancel(true);
        }
        if (errorScheduledFuture != null && !errorScheduledFuture.isDone()){
            errorScheduledFuture.cancel(true);
        }
    }

    @Override
    public void wantEnter() {
        cancelTask();
        scheduledFuture = extraThreadPool.scheduleWithFixedDelay(new LogRunnable(() -> {
            if (isPause.get().get()){
                cancelTask();
            } else if (Mode.getCurrMode() == ModeEnum.HUB){
                SystemUtil.updateGameRect();
                mouseUtil.leftButtonClick(
                        ((ScriptStaticData.GAME_RECT.right + ScriptStaticData.GAME_RECT.left) >> 1) + RandomUtil.getRandom(-15, 15),
                        (int) (ScriptStaticData.GAME_RECT.bottom - (ScriptStaticData.GAME_RECT.bottom - ScriptStaticData.GAME_RECT.top) * TOURNAMENT_MODE_BUTTON_VERTICAL_TO_BOTTOM_RATIO) + RandomUtil.getRandom(-5, 5)
                );
            }else if (Mode.getCurrMode() == ModeEnum.GAME_MODE){
                cancelTask();
                SystemUtil.updateGameRect();
                gameUtil.clickBackButton();
            }else {
                cancelTask();
            }
        }), DELAY_TIME, INTERVAL_TIME, TimeUnit.MILLISECONDS);
    }
    @Override
    protected void afterEnter(Object o) {
        if (Work.isDuringWorkDate()){
            SystemUtil.updateGameRect();
            if (ModeEnum.TOURNAMENT == RunModeEnum.valueOf(scriptConfiguration.getProperty(ConfigurationEnum.RUN_MODE.getKey())).getModeEnum()){
                DeckEnum currentDeck = DeckEnum.valueOf(scriptConfiguration.getProperty(ConfigurationEnum.DECK.getKey()));
                if (!currentDeck.getRunMode().isEnable()){
                    log.warn("不可用或不支持的模式：" + currentDeck.name());
                    return;
                }
                if (!(checkPowerLogSize())){
                    return;
                }
                SystemUtil.delayMedium();
                clickModeChangeButton();
                SystemUtil.delayMedium();
                changeMode(currentDeck);
                SystemUtil.delayMedium();
                selectDeck(currentDeck);
                SystemUtil.delayShort();
                startMatching();
            }else {
//            退出该界面
                gameUtil.clickBackButton();
            }
        }else {
            Work.stopWork();
        }
    }
    private static final int RESERVE_SIZE = 1500 * 1024;
    private static final int MAX_SIZE = 10000 * 1024;
    private boolean checkPowerLogSize(){
        try {
            if (powerLogListener.getAccessFile() != null && powerLogListener.getAccessFile().length() + RESERVE_SIZE >= MAX_SIZE){
                log.info("power.log即将达到" + (MAX_SIZE / 1024) + "KB，准备重启游戏");
                core.restart();
                return false;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    private void clickModeChangeButton(){
        log.info("点击切换模式按钮");
        mouseUtil.leftButtonClick(
                (int) (((ScriptStaticData.GAME_RECT.right + ScriptStaticData.GAME_RECT.left) >> 1) + (ScriptStaticData.GAME_RECT.bottom - ScriptStaticData.GAME_RECT.top) * CHANGE_MODE_BUTTON_HORIZONTAL_TO_CENTER_RATION * GameRationStaticData.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO + RandomUtil.getRandom(-15, -5)),
                (int) (ScriptStaticData.GAME_RECT.bottom - (ScriptStaticData.GAME_RECT.bottom - ScriptStaticData.GAME_RECT.top) * CHANGE_MODE_BUTTON_VERTICAL_TO_BOTTOM_RATION) + RandomUtil.getRandom(10, 20)
        );
    }

    private void changeMode(DeckEnum currentDeck){
        switch (currentDeck.getDeckType()){
            case CLASSIC -> changeModeToClassic();
            case STANDARD -> changeModeToStandard();
            case WILD -> changeModeToWild();
            case CASUAL -> changeModeToCasual();
            default -> throw new RuntimeException("没有此模式：" + currentDeck.getDeckType().getComment());
        }
    }

    public void selectDeck(DeckEnum currentDeck){
        List<Deck> decks = DeckLogListener.getDECKS();
        for (int i = decks.size() - 1; i >= 0; i--) {
            Deck d = decks.get(i);
            if (Objects.equals(d.getCode(), currentDeck.getDeckCode()) || Objects.equals(d.getName(), currentDeck.getComment())){
                log.info("找到套牌:" + currentDeck.getComment());
                break;
            }
        }
        log.info("选择套牌");

        int x = (int) (((ScriptStaticData.GAME_RECT.right + ScriptStaticData.GAME_RECT.left) >> 1) - (ScriptStaticData.GAME_RECT.bottom - ScriptStaticData.GAME_RECT.top) * FIRST_DECK_BUTTON_HORIZONTAL_TO_CENTER_RATIO * GameRationStaticData.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO + RandomUtil.getRandom(-10, 10));
        int y = (int) (ScriptStaticData.GAME_RECT.bottom - (ScriptStaticData.GAME_RECT.bottom - ScriptStaticData.GAME_RECT.top) * FIRST_ROW_DECK_VERTICAL_TO_BOTTOM_RATIO) + RandomUtil.getRandom(-5, 5);
        mouseUtil.leftButtonClick(x, y);
        SystemUtil.delayShort();
        mouseUtil.leftButtonClick(x, y);
    }

    private void changeModeToClassic(){
        log.info("切换至经典模式");
        clickRight(CLASSIC_BUTTON_HORIZONTAL_TO_CENTER_RATION, CLASSIC_BUTTON_VERTICAL_TO_BOTTOM_RATION);
    }

    private void clickRight(float classicButtonHorizontalToCenterRation, float classicButtonVerticalToBottomRation) {
        mouseUtil.leftButtonClick(
                (int) (((ScriptStaticData.GAME_RECT.right + ScriptStaticData.GAME_RECT.left) >> 1) - (ScriptStaticData.GAME_RECT.bottom - ScriptStaticData.GAME_RECT.top) * classicButtonHorizontalToCenterRation * GameRationStaticData.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO + RandomUtil.getRandom(-5, 5)),
                (int) (ScriptStaticData.GAME_RECT.bottom - (ScriptStaticData.GAME_RECT.bottom - ScriptStaticData.GAME_RECT.top) * classicButtonVerticalToBottomRation) + RandomUtil.getRandom(-5, 5)
        );
    }

    private void changeModeToStandard(){
        log.info("切换至标准模式");
        clickRight(STANDARD_BUTTON_HORIZONTAL_TO_CENTER_RATION, STANDARD_BUTTON_VERTICAL_TO_BOTTOM_RATION);
    }

    private void changeModeToWild(){
        log.info("切换至狂野模式");
        clickLeft(STANDARD_BUTTON_HORIZONTAL_TO_CENTER_RATION, STANDARD_BUTTON_VERTICAL_TO_BOTTOM_RATION);
    }

    private void changeModeToCasual(){
        log.info("切换至休闲模式");
        clickLeft(CLASSIC_BUTTON_HORIZONTAL_TO_CENTER_RATION, CLASSIC_BUTTON_VERTICAL_TO_BOTTOM_RATION);
    }

    private void clickLeft(float classicButtonHorizontalToCenterRation, float classicButtonVerticalToBottomRation) {
        mouseUtil.leftButtonClick(
                (int) (((ScriptStaticData.GAME_RECT.right + ScriptStaticData.GAME_RECT.left) >> 1) + (ScriptStaticData.GAME_RECT.bottom - ScriptStaticData.GAME_RECT.top) * classicButtonHorizontalToCenterRation * GameRationStaticData.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO + RandomUtil.getRandom(-5, 5)),
                (int) (ScriptStaticData.GAME_RECT.bottom - (ScriptStaticData.GAME_RECT.bottom - ScriptStaticData.GAME_RECT.top) * classicButtonVerticalToBottomRation) + RandomUtil.getRandom(-5, 5)
        );
    }

    public void startMatching(){
        log.info("开始匹配");
        //        重置游戏
        mouseUtil.leftButtonClick(
                (int) (((ScriptStaticData.GAME_RECT.right + ScriptStaticData.GAME_RECT.left) >> 1) + (ScriptStaticData.GAME_RECT.bottom - ScriptStaticData.GAME_RECT.top) * GameRationStaticData.START_BUTTON_HORIZONTAL_TO_CENTER_RATIO * GameRationStaticData.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO + RandomUtil.getRandom(-10, 10)),
                (int) (ScriptStaticData.GAME_RECT.bottom - (ScriptStaticData.GAME_RECT.bottom - ScriptStaticData.GAME_RECT.top) * GameRationStaticData.START_BUTTON_VERTICAL_TO_BOTTOM_RATIO) + RandomUtil.getRandom(-10, 10)
        );
        generateTimer();
    }

    /**
     * 生成匹配失败时兜底的定时器
     */
    private void generateTimer(){
        errorScheduledFuture = extraThreadPool.schedule(new LogRunnable(() -> {
            if (isPause.get().get()){
                errorScheduledFuture.cancel(true);
            }else {
                log.info("匹配失败，再次匹配中");
                SystemUtil.notice("匹配失败，再次匹配中");
                SystemUtil.updateGameRect();
//                点击取消匹配按钮
                mouseUtil.leftButtonClick(
                        ((ScriptStaticData.GAME_RECT.right + ScriptStaticData.GAME_RECT.left) >> 1) + RandomUtil.getRandom(-10, 10),
                        (int) (ScriptStaticData.GAME_RECT.bottom - (ScriptStaticData.GAME_RECT.bottom - ScriptStaticData.GAME_RECT.top) * CANCEL_MATCH_BUTTON_VERTICAL_TO_BOTTOM_RATION) + RandomUtil.getRandom(-5, 5)
                );
                SystemUtil.delayLong();
//                点击错误按钮
                mouseUtil.leftButtonClick(
                        ((ScriptStaticData.GAME_RECT.right + ScriptStaticData.GAME_RECT.left) >> 1) + RandomUtil.getRandom(-10, 10),
                        (int) (ScriptStaticData.GAME_RECT.bottom - (ScriptStaticData.GAME_RECT.bottom - ScriptStaticData.GAME_RECT.top) * ERROR_BUTTON_VERTICAL_TO_BOTTOM_RATION) + RandomUtil.getRandom(-5, 5)
                );
                SystemUtil.delayMedium();
                afterEnter(null);
            }
        }), 60, TimeUnit.SECONDS);
    }

    @Override
    public void closeModeTask() {
        cancelTask();
    }

}
