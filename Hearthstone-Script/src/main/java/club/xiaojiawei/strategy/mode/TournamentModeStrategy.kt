package club.xiaojiawei.strategy.mode;

import club.xiaojiawei.DeckStrategy;
import club.xiaojiawei.bean.Deck;
import club.xiaojiawei.bean.GameRect;
import club.xiaojiawei.config.ThreadPoolConfigKt;
import club.xiaojiawei.interfaces.closer.ModeTaskCloser;
import club.xiaojiawei.core.Core;
import club.xiaojiawei.bean.LogRunnable;
import club.xiaojiawei.enums.ModeEnum;
import club.xiaojiawei.enums.RunModeEnum;
import club.xiaojiawei.listener.log.DeckLogListener;
import club.xiaojiawei.listener.log.PowerLogListener;
import club.xiaojiawei.status.DeckStrategyManager;
import club.xiaojiawei.status.Mode;
import club.xiaojiawei.status.Work;
import club.xiaojiawei.strategy.AbstractModeStrategy;
import club.xiaojiawei.utils.GameUtil;
import club.xiaojiawei.utils.SystemUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static club.xiaojiawei.data.ScriptStaticData.MAX_LOG_SIZE;


/**
 * 传统对战
 * @author 肖嘉威
 * @date 2022/11/25 12:39
 */
@Slf4j
@Component
public class TournamentModeStrategy extends AbstractModeStrategy<Object> implements ModeTaskCloser {

    public static final GameRect START_RECT = new GameRect(0.2586D, 0.3459D, 0.2706D, 0.3794D);

    public static final GameRect ERROR_RECT = new GameRect(-0.0397D, 0.0325D, 0.0856D, 0.1249D);


    public static final GameRect CHANGE_MODE_RECT = new GameRect(0.2868D, 0.3256D, -0.4672D, -0.4279D);

    public static final GameRect STANDARD_MODE_RECT = new GameRect(-0.2012D, -0.0295D, -0.2156D, -0.0400D);

    public static final GameRect WILD_MODE_RECT = new GameRect(0.0295D, 0.2012D, -0.2156D, -0.0400D);

    public static final GameRect CASUAL_MODE_RECT = new GameRect(0.2557D, 0.4278D, -0.1769D, 0.0014D);

    public static final GameRect CLASSIC_MODE_RECT = new GameRect(-0.4278D, -0.2557D, -0.1769D, 0.0014D);

    public static final GameRect FIRST_DECK_RECT = new GameRect(-0.4108D, -0.2487D, -0.2811D, -0.1894D);

    /**
     * 顶栏有限时借用套牌时使用
     */
    public static final GameRect FIRST_DECK_RECT_BACK = new GameRect(-0.4072D, -0.2516D, -0.0696D, 0.0139D);

    public static final GameRect BACK_RECT = new GameRect(0.4041D, 0.4575D, 0.4083D, 0.4410D);

    public static final GameRect CANCEL_RECT = new GameRect(-0.0251D, 0.0530D, 0.3203D, 0.3802D);

    @Resource
    private PowerLogListener powerLogListener;
    @Resource
    private Core core;

    private ScheduledFuture<?> scheduledFuture;

    private ScheduledFuture<?> errorScheduledFuture;

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
        scheduledFuture = ThreadPoolConfigKt.getEXTRA_THREAD_POOL().scheduleWithFixedDelay(new LogRunnable(() -> {
            if (isPause.get().get()){
                cancelTask();
            } else if (Mode.getCurrMode() == ModeEnum.HUB){
                HubModeStrategy.TOURNAMENT_MODE_RECT.lClick();
            }else if (Mode.getCurrMode() == ModeEnum.GAME_MODE){
                cancelTask();
                SystemUtil.updateGameRect();
                BACK_RECT.lClick();
            }else {
                cancelTask();
            }
        }), DELAY_TIME, INTERVAL_TIME, TimeUnit.MILLISECONDS);
    }
    @Override
    protected void afterEnter(Object o) {
        if (Work.isDuringWorkDate()){
            SystemUtil.updateGameRect();

            DeckStrategy deckStrategy = DeckStrategyManager.CURRENT_DECK_STRATEGY.get();
            if (deckStrategy == null){
                SystemUtil.notice("未选择卡组");
                log.info("未选择卡组");
                isPause.get().set(true);
                return;
            }
            RunModeEnum runModeEnum;
            if (((runModeEnum = deckStrategy.getRunModes()[0]) == RunModeEnum.CASUAL || runModeEnum == RunModeEnum.CLASSIC || runModeEnum == RunModeEnum.WILD || runModeEnum == RunModeEnum.STANDARD) && runModeEnum.isEnable()){
                if (!(checkPowerLogSize())){
                    return;
                }
                SystemUtil.delayMedium();
                clickModeChangeButton();
                SystemUtil.delayMedium();
                changeMode(runModeEnum);
                SystemUtil.delayMedium();
                selectDeck(deckStrategy);
                SystemUtil.delayShort();
                startMatching();
            }else {
//            退出该界面
                BACK_RECT.lClick();
            }
        }else {
            Work.stopWork();
        }
    }

    private static final int RESERVE_SIZE = 4 * 1024 * 1024;

    private boolean checkPowerLogSize(){
        try {
            if (powerLogListener.getInnerLogFile() != null && powerLogListener.getInnerLogFile().length() + RESERVE_SIZE >= MAX_LOG_SIZE){
                log.info("power.log即将达到" + (MAX_LOG_SIZE / 1024) + "KB，准备重启游戏");
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
        CHANGE_MODE_RECT.lClick();
    }

    private void changeMode(RunModeEnum runModeEnum){
        switch (runModeEnum){
            case CLASSIC -> changeModeToClassic();
            case STANDARD -> changeModeToStandard();
            case WILD -> changeModeToWild();
            case CASUAL -> changeModeToCasual();
            default -> throw new RuntimeException("不支持此模式：" + runModeEnum.getComment());
        }
    }

    public void selectDeck(DeckStrategy deckStrategy){
        if (deckStrategy != null) {
            List<Deck> decks = DeckLogListener.getDECKS();
            for (int i = decks.size() - 1; i >= 0; i--) {
                Deck d = decks.get(i);
                if (Objects.equals(d.getCode(), deckStrategy.deckCode()) || Objects.equals(d.getName(), deckStrategy.name())){
                    log.info("找到套牌:" + deckStrategy.name());
                    break;
                }
            }
        }
        log.info("选择套牌");

        FIRST_DECK_RECT_BACK.lClick();
        SystemUtil.delayShort();
        FIRST_DECK_RECT.lClick();
        SystemUtil.delayShort();
        FIRST_DECK_RECT.lClick();
    }

    private void changeModeToClassic(){
        log.info("切换至经典模式");
        CLASSIC_MODE_RECT.lClick();
    }

    private void changeModeToStandard(){
        log.info("切换至标准模式");
        STANDARD_MODE_RECT.lClick();
    }

    private void changeModeToWild(){
        log.info("切换至狂野模式");
        WILD_MODE_RECT.lClick();
    }

    private void changeModeToCasual(){
        log.info("切换至休闲模式");
        CASUAL_MODE_RECT.lClick();
    }

    public void startMatching(){
        log.info("开始匹配");
        START_RECT.lClick();
        generateTimer();
    }

    /**
     * 生成匹配失败时兜底的定时器
     */
    private void generateTimer(){
        if (errorScheduledFuture != null && !errorScheduledFuture.isDone()){
            errorScheduledFuture.cancel(true);
        }
        errorScheduledFuture = ThreadPoolConfigKt.getEXTRA_THREAD_POOL().schedule(new LogRunnable(() -> {
            if (isPause.get().get() || Thread.currentThread().isInterrupted()){
                errorScheduledFuture.cancel(true);
            }else {
                log.info("匹配失败，再次匹配中");
                SystemUtil.notice("匹配失败，再次匹配中");
//                点击取消匹配按钮
                CANCEL_RECT.lClick();
                SystemUtil.delayLong();
//                点击错误按钮
                ERROR_RECT.lClick();
                SystemUtil.delayMedium();
                GameUtil.reconnect();
                afterEnter(null);
            }
        }), 90, TimeUnit.SECONDS);
    }

    @Override
    public void closeModeTask() {
        cancelTask();
    }

}
