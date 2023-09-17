package club.xiaojiawei.strategy.phase;

import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.entity.Block;
import club.xiaojiawei.entity.Player;
import club.xiaojiawei.entity.TagChangeEntity;
import club.xiaojiawei.enums.DeckEnum;
import club.xiaojiawei.enums.StepEnum;
import club.xiaojiawei.listener.ScreenFileListener;
import club.xiaojiawei.status.War;
import club.xiaojiawei.strategy.AbstractDeckStrategy;
import club.xiaojiawei.strategy.AbstractPhaseStrategy;
import club.xiaojiawei.utils.PowerLogUtil;
import club.xiaojiawei.utils.SystemUtil;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

import static club.xiaojiawei.data.GameStaticData.*;
import static club.xiaojiawei.data.ScriptStaticData.ROBOT;
import static club.xiaojiawei.enums.BlockTypeEnum.TRIGGER;
import static club.xiaojiawei.enums.ConfigurationKeyEnum.DECK_KEY;
import static club.xiaojiawei.enums.StepEnum.*;
import static club.xiaojiawei.enums.TagEnum.CURRENT_PLAYER;
import static club.xiaojiawei.enums.TagEnum.STEP;
import static club.xiaojiawei.enums.WarPhaseEnum.GAME_OVER_PHASE;

/**
 * @author 肖嘉威
 * @date 2022/11/26 17:24
 */
@Slf4j
@Component
public class GameTurnAbstractPhaseStrategy extends AbstractPhaseStrategy<String> {
    @Resource
    private Properties scriptProperties;
    private static StepEnum currentStep = MAIN_READY;
    @Getter
    private static Player currentPlayer;
    private static Thread thread;

    @SuppressWarnings("all")
    public static void stopThread(){
        try{
            if (thread != null && thread.isAlive()){
                thread.stop();
                log.info("出牌线程已停止");
            }
        }catch (Exception e){
            log.warn("出牌线程已停止");
        }
    }

    public static void reset(){
        currentStep = MAIN_READY;
    }
    @SneakyThrows
    @Override
    protected void execute(String l, RandomAccessFile accessFile) {
        log.info(currentStep.getComment());
        while (true) {
            if (isPause.get().get()){
                return;
            }
            if ((l = accessFile.readLine()) == null) {
                if (accessFile.getFilePointer() > accessFile.length()){
                    accessFile.seek(0);
                }else {
                    ROBOT.delay(1000);
                }
            }else if (powerFileListener.isRelevance(l)){
                ScreenFileListener.setMark(System.currentTimeMillis());
                switch (currentStep){
                    case MAIN_READY -> mainReady(l);
                    case MAIN_START_TRIGGERS -> mainStartTriggers(l, accessFile);
                    case MAIN_START -> mainStart(l, accessFile);
                    case MAIN_ACTION -> mainAction(l, accessFile);
                    case MAIN_END -> mainEnd(l, accessFile);
                    case MAIN_CLEANUP -> mainCleanup(l, accessFile);
                    case MAIN_NEXT -> mainNext(l, accessFile);
                    default -> {
                        return;
                    }
                }
            }
        }
    }

    @Override
    protected void afterExecute() {
        super.afterExecute();
        if (!isPause.get().get()){
            War.setCurrentPhase(GAME_OVER_PHASE);
        }
    }

    public void mainReady(String l){
        if (l.contains(TAG_CHANGE)){
            TagChangeEntity tagChangeEntity = PowerLogUtil.parseTagChange(l);
            if (!PowerLogUtil.dealTagChange(tagChangeEntity)){
                if (tagChangeEntity.getTag() == STEP && Objects.equals(MAIN_START_TRIGGERS.getValue(), tagChangeEntity.getValue())){
                    currentStep = MAIN_START_TRIGGERS;
                    log.info(currentStep.getComment());
                }
            }
        }else if (l.contains("BLOCK_START")){
            Block block = PowerLogUtil.parseBlock(l);
//            匹配战网id后缀正则
            if (block.getBlockType() == TRIGGER && block.getEntity().getEntity().matches("^.+#\\d+$")){
                String gameId = new String(block.getEntity().getEntity().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                if (Objects.equals(War.getMe().getGameId(), gameId) || (War.getRival().getGameId() != null && !Objects.equals(War.getRival().getGameId(), gameId))){
                    currentPlayer = War.getMe();
                    War.getMe().setGameId(gameId);
                }else {
                    currentPlayer = War.getRival();
                    War.getRival().setGameId(gameId);
                }
                log.info(gameId + " 的回合");
            }
        }
    }

    public void mainStartTriggers(String l, RandomAccessFile accessFile){
        currentPlayer.clear();
        if (l.contains(TAG_CHANGE)){
            TagChangeEntity tagChangeEntity = PowerLogUtil.parseTagChange(l);
            if (!PowerLogUtil.dealTagChange(tagChangeEntity)){
                if (tagChangeEntity.getTag() == STEP){
                    if (Objects.equals(tagChangeEntity.getValue(), MAIN_START.getValue())){
                        currentStep = MAIN_START;
                        log.info(currentStep.getComment());
                    }else if (Objects.equals(tagChangeEntity.getValue(), FINAL_GAMEOVER.getValue())){
                        currentStep = FINAL_GAMEOVER;
                        log.info(currentStep.getComment());
                    }
                }
            }
        }else if (l.contains(SHOW_ENTITY)){
           PowerLogUtil.dealShowEntity(l, accessFile);
        }else if (l.contains(FULL_ENTITY)){
            PowerLogUtil.dealFullEntity(l, accessFile);
        }
    }

    public void mainStart(String l, RandomAccessFile accessFile){
        if (l.contains(TAG_CHANGE)){
            TagChangeEntity tagChangeEntity = PowerLogUtil.parseTagChange(l);
            if (!PowerLogUtil.dealTagChange(tagChangeEntity)){
                if (tagChangeEntity.getTag() == STEP){
                    if (Objects.equals(tagChangeEntity.getValue(), MAIN_ACTION.getValue())){
                        currentStep = MAIN_ACTION;
                        log.info(currentStep.getComment());
                        if (War.getMe() == currentPlayer){
                            log.info("我方回合");
                            SystemUtil.frontWindow(ScriptStaticData.getGameHWND());
                            SystemUtil.updateRect(ScriptStaticData.getGameHWND(), ScriptStaticData.GAME_RECT);
                            AbstractDeckStrategy.setMyTurn(true);
//                          等待动画结束
                            ROBOT.delay(4000);
//                            异步执行出牌策略，以便监听出牌后的卡牌变动
                            (thread = new Thread(() -> {
                                log.info("执行出牌策略");
                                DeckEnum.valueOf(scriptProperties.getProperty(DECK_KEY.getKey())).getAbstractDeckStrategy().afterIntoMyTurn();
                                log.info("出牌策略执行完毕");
                            }, "OutCard Thread")).start();
                        }else {
                            log.info("对方回合");
                            AbstractDeckStrategy.setMyTurn(false);
                        }
                    }else if (Objects.equals(tagChangeEntity.getValue(), FINAL_GAMEOVER.getValue())){
                        currentStep = FINAL_GAMEOVER;
                        log.info(currentStep.getComment());
                    }
                }
            }
        }else if (l.contains(SHOW_ENTITY)){
            PowerLogUtil.dealShowEntity(l, accessFile);
        }else if (l.contains(FULL_ENTITY)){
            PowerLogUtil.dealFullEntity(l, accessFile);
        }
    }

    public void mainAction(String l, RandomAccessFile accessFile){
        if (l.contains(TAG_CHANGE)){
            TagChangeEntity tagChangeEntity = PowerLogUtil.parseTagChange(l);
            if (!PowerLogUtil.dealTagChange(tagChangeEntity)){
                if (tagChangeEntity.getTag() == STEP){
                    if (Objects.equals(tagChangeEntity.getValue(), MAIN_END.getValue())){
                        currentStep = MAIN_END;
                        log.info(currentStep.getComment());
                    }else if (Objects.equals(tagChangeEntity.getValue(), FINAL_GAMEOVER.getValue())){
                        currentStep = FINAL_GAMEOVER;
                        log.info(currentStep.getComment());
                    }
                }
            }
        }else if (l.contains(SHOW_ENTITY)){
            PowerLogUtil.dealShowEntity(l, accessFile);
        }else if (l.contains(FULL_ENTITY)){
            PowerLogUtil.dealFullEntity(l, accessFile);
        }
    }

    public void mainEnd(String l, RandomAccessFile accessFile){
        if (l.contains(TAG_CHANGE)){
            TagChangeEntity tagChangeEntity = PowerLogUtil.parseTagChange(l);
            if (!PowerLogUtil.dealTagChange(tagChangeEntity)){
                if (tagChangeEntity.getTag() == STEP){
                    if (Objects.equals(tagChangeEntity.getValue(), MAIN_CLEANUP.getValue())){
                        stopThread();
                        currentStep = MAIN_CLEANUP;
                        log.info(currentStep.getComment());
                    }else if (Objects.equals(tagChangeEntity.getValue(), FINAL_GAMEOVER.getValue())){
                        currentStep = FINAL_GAMEOVER;
                        log.info(currentStep.getComment());
                    }
                }
            }
        }else if (l.contains(SHOW_ENTITY)){
            PowerLogUtil.dealShowEntity(l, accessFile);
        }else if (l.contains(FULL_ENTITY)){
            PowerLogUtil.dealFullEntity(l, accessFile);
        }
    }

    public void mainCleanup(String l, RandomAccessFile accessFile){
        if (l.contains(TAG_CHANGE)){
            TagChangeEntity tagChangeEntity = PowerLogUtil.parseTagChange(l);
            if (!PowerLogUtil.dealTagChange(tagChangeEntity)){
                if (tagChangeEntity.getTag() == STEP && Objects.equals(tagChangeEntity.getValue(), MAIN_NEXT.getValue())){
                    currentStep = MAIN_NEXT;
                    log.info(currentStep.getComment());
                }
            }
        }else if (l.contains(SHOW_ENTITY)){
            PowerLogUtil.dealShowEntity(l, accessFile);
        }else if (l.contains(FULL_ENTITY)){
            PowerLogUtil.dealFullEntity(l, accessFile);
        }
    }

    public void mainNext(String l, RandomAccessFile accessFile){
        if (l.contains(TAG_CHANGE)){
            TagChangeEntity tagChangeEntity = PowerLogUtil.parseTagChange(l);
            if (!PowerLogUtil.dealTagChange(tagChangeEntity)){
                if (tagChangeEntity.getTag() == CURRENT_PLAYER && Objects.equals(tagChangeEntity.getValue(), "1")){
                    currentPlayer = Objects.equals(War.getPlayer1().getGameId(), tagChangeEntity.getEntityId())? War.getPlayer1() : War.getPlayer2();
                }else if (tagChangeEntity.getTag() == STEP && Objects.equals(tagChangeEntity.getValue(), MAIN_READY.getValue())){
                    currentStep = MAIN_READY;
                    log.info(currentStep.getComment());
                }
            }
        }else if (l.contains(SHOW_ENTITY)){
            PowerLogUtil.dealShowEntity(l, accessFile);
        }else if (l.contains(FULL_ENTITY)){
            PowerLogUtil.dealFullEntity(l, accessFile);
        }
    }

}
