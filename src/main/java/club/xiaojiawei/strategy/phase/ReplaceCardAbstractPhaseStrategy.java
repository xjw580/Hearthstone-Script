package club.xiaojiawei.strategy.phase;

import club.xiaojiawei.entity.TagChangeEntity;
import club.xiaojiawei.enums.WarPhaseEnum;
import club.xiaojiawei.listener.PowerFileListener;
import club.xiaojiawei.status.Deck;
import club.xiaojiawei.status.War;
import club.xiaojiawei.strategy.AbstractPhaseStrategy;
import club.xiaojiawei.utils.PowerLogUtil;
import club.xiaojiawei.utils.SystemUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.RandomAccessFile;
import java.util.Objects;

import static club.xiaojiawei.constant.GameKeyWordConst.SHOW_ENTITY;
import static club.xiaojiawei.constant.GameKeyWordConst.TAG_CHANGE;
import static club.xiaojiawei.constant.SystemConst.ROBOT;
import static club.xiaojiawei.enums.StepEnum.FINAL_GAMEOVER;
import static club.xiaojiawei.enums.StepEnum.MAIN_READY;
import static club.xiaojiawei.enums.TagEnum.NEXT_STEP;
import static club.xiaojiawei.enums.WarPhaseEnum.GAME_OVER_PHASE;

/**
 * @author 肖嘉威
 * @date 2022/11/26 17:24
 */
@Slf4j
public class ReplaceCardAbstractPhaseStrategy extends AbstractPhaseStrategy {

    private static final float CONFIRM_BUTTON_VERTICAL_TO_BOTTOM_RATION = (float) 0.20;

    @SneakyThrows
    @Override
    public void dealing(String l) {
        War.setCurrentPhase(WarPhaseEnum.REPLACE_CARD_PHASE);
        log.info("当前处于：" + War.getCurrentPhase().getComment());
//        等待动画结束
        ROBOT.delay(17000);
//        执行换牌策略
        Deck.getCurrentDeck().getStrategySupplier().get().afterInto();
        SystemUtil.delayMedium();
        RandomAccessFile accessFile = PowerFileListener.getAccessFile();
        TagChangeEntity tagChangeEntity;
        while (true) {
            if ((l = accessFile.readLine()) == null) {
                if (accessFile.getFilePointer() > accessFile.length()){
                    accessFile.seek(0);
                }else {
                    ROBOT.delay(1000);
                }
            }else if (PowerFileListener.isRelevance(l)){
                PowerFileListener.setMark(System.currentTimeMillis());
                if (l.contains(SHOW_ENTITY)){
                    PowerLogUtil.dealShowEntity(l, accessFile);
                }else if (l.contains(TAG_CHANGE)){
                    tagChangeEntity = PowerLogUtil.parseTagChange(l);
                    if (!PowerLogUtil.dealTagChange(tagChangeEntity)){
                        if (tagChangeEntity.getTag() == NEXT_STEP && Objects.equals(MAIN_READY.getValue(), tagChangeEntity.getValue())){
                            log.info(War.getCurrentPhase().getComment() + " -> 结束");
                            break;
                        }else if (Objects.equals(tagChangeEntity.getValue(), FINAL_GAMEOVER.getValue())){
                            log.info(War.getCurrentPhase().getComment() + " -> 结束");
                            GAME_OVER_PHASE.getPhaseStrategySupplier().get().afterInto(l);
                            break;
                        }
                    }
                }
            }
        }
    }

}
