package club.xiaojiawei.hearthstone.strategy.phase;

import club.xiaojiawei.hearthstone.entity.Block;
import club.xiaojiawei.hearthstone.entity.Player;
import club.xiaojiawei.hearthstone.entity.TagChangeEntity;
import club.xiaojiawei.hearthstone.enums.StepEnum;
import club.xiaojiawei.hearthstone.enums.WarPhaseEnum;
import club.xiaojiawei.hearthstone.listen.PowerFileListen;
import club.xiaojiawei.hearthstone.pool.MyThreadPool;
import club.xiaojiawei.hearthstone.status.Deck;
import club.xiaojiawei.hearthstone.status.War;
import club.xiaojiawei.hearthstone.strategy.PhaseStrategy;
import club.xiaojiawei.hearthstone.utils.PowerLogUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static club.xiaojiawei.hearthstone.constant.GameConst.*;
import static club.xiaojiawei.hearthstone.constant.SystemConst.ROBOT;
import static club.xiaojiawei.hearthstone.enums.BlockTypeEnum.TRIGGER;
import static club.xiaojiawei.hearthstone.enums.StepEnum.*;
import static club.xiaojiawei.hearthstone.enums.TagEnum.*;
import static club.xiaojiawei.hearthstone.enums.WarPhaseEnum.GAME_OVER_PHASE;

/**
 * @author 肖嘉威
 * @date 2022/11/26 17:24
 */
@Slf4j
public class GameTurnPhaseStrategy extends PhaseStrategy {

    private StepEnum currentStep = MAIN_READY;

    private static Player currentPlayer;

    public static Player getCurrentPlayer() {
        return currentPlayer;
    }

    @SneakyThrows
    @Override
    public void dealing(String l) {
        War.setCurrentPhase(WarPhaseEnum.GAME_TURN_PHASE);
        log.info("当前处于：" + War.getCurrentPhase().getComment());
        RandomAccessFile accessFile = PowerFileListen.getAccessFile();
        log.info(currentStep.getComment());
        while (true) {
            if ((l = accessFile.readLine()) == null) {
                if (accessFile.getFilePointer() > accessFile.length() + 10000){
                    accessFile.seek(accessFile.length());
                }
                ROBOT.delay(1000);
            }else if (PowerFileListen.isRelevance(l)){
                PowerFileListen.setMark(System.currentTimeMillis());
                switch (currentStep){
                    case MAIN_READY -> mainReady(l);
                    case MAIN_START_TRIGGERS -> mainStartTriggers(l, accessFile);
                    case MAIN_START -> mainStart(l, accessFile);
                    case MAIN_ACTION -> mainAction(l, accessFile);
                    case MAIN_END -> mainEnd(l, accessFile);
                    case MAIN_CLEANUP -> mainCleanup(l, accessFile);
                    case MAIN_NEXT -> mainNext(l, accessFile);
                    default -> {
                        log.info(War.getCurrentPhase().getComment() + " -> 结束");
                        GAME_OVER_PHASE.getPhaseStrategySupplier().get().afterInto(l);
                        return;
                    }
                }
            }
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
            if (block.getBlockType() == TRIGGER){
                String gameId = new String(block.getEntity().getEntity().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                if (Strings.isNotBlank(gameId)){
                    if (Objects.equals(War.getMe().getGameId(), gameId) || (War.getRival().getGameId() != null && !Objects.equals(War.getRival().getGameId(), gameId))){
                        currentPlayer = War.getMe();
                    }else {
                        currentPlayer = War.getRival();
                    }
                    log.info(gameId + " 的回合");
                }
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
//                          等待动画结束
                            log.info("我方出牌");
                            ROBOT.delay(4000);
//                            异步执行出牌策略，以便监听出牌后的卡牌变动
                            MyThreadPool.myThreadPool.execute(() -> {
                                log.info("执行出牌策略");
                                Deck.getCurrentDeck().getStrategySupplier().get().afterIntoMyTurn();
                                log.info("出牌策略执行完毕");
                            });
                        }else {
                            log.info("对方出牌");
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
//                        MyThreadPool.reset();
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
