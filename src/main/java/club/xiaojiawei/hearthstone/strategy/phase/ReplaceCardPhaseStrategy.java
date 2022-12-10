package club.xiaojiawei.hearthstone.strategy.phase;

import club.xiaojiawei.hearthstone.entity.TagChangeEntity;
import club.xiaojiawei.hearthstone.enums.WarPhaseEnum;
import club.xiaojiawei.hearthstone.listen.PowerFileListen;
import club.xiaojiawei.hearthstone.run.Core;
import club.xiaojiawei.hearthstone.status.Deck;
import club.xiaojiawei.hearthstone.status.War;
import club.xiaojiawei.hearthstone.strategy.PhaseStrategy;
import club.xiaojiawei.hearthstone.utils.MouseUtil;
import club.xiaojiawei.hearthstone.utils.PowerLogUtil;
import club.xiaojiawei.hearthstone.utils.RandomUtil;
import club.xiaojiawei.hearthstone.utils.SystemUtil;
import com.sun.jna.platform.win32.WinDef;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.RandomAccessFile;
import java.util.Objects;

import static club.xiaojiawei.hearthstone.constant.GameConst.*;
import static club.xiaojiawei.hearthstone.constant.SystemConst.ROBOT;
import static club.xiaojiawei.hearthstone.enums.StepEnum.FINAL_GAMEOVER;
import static club.xiaojiawei.hearthstone.enums.StepEnum.MAIN_READY;
import static club.xiaojiawei.hearthstone.enums.TagEnum.NEXT_STEP;
import static club.xiaojiawei.hearthstone.enums.WarPhaseEnum.GAME_OVER_PHASE;

/**
 * @author 肖嘉威
 * @date 2022/11/26 17:24
 */
@Slf4j
public class ReplaceCardPhaseStrategy extends PhaseStrategy {

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
        WinDef.RECT gameRect = SystemUtil.getRect(Core.getGameHWND());
//        点击确认
        MouseUtil.leftButtonClick(
                ((gameRect.right + gameRect.left) >> 1) + RandomUtil.getRandom(-10, 10),
                (int) (gameRect.bottom - (gameRect.bottom - gameRect.top) * CONFIRM_BUTTON_VERTICAL_TO_BOTTOM_RATION + RandomUtil.getRandom(-5, 5))
        );
        RandomAccessFile accessFile = PowerFileListen.getAccessFile();
        TagChangeEntity tagChangeEntity;
        while (true) {
            if ((l = accessFile.readLine()) == null) {
                if (accessFile.getFilePointer() > accessFile.length() + 10000){
                    accessFile.seek(accessFile.length());
                }
                ROBOT.delay(1000);
            }else if (PowerFileListen.isRelevance(l)){
                PowerFileListen.setMark(System.currentTimeMillis());
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
