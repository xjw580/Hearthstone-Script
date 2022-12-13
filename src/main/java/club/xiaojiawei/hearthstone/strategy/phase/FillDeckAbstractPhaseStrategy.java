package club.xiaojiawei.hearthstone.strategy.phase;

import club.xiaojiawei.hearthstone.enums.WarPhaseEnum;
import club.xiaojiawei.hearthstone.listener.PowerFileListener;
import club.xiaojiawei.hearthstone.status.War;
import club.xiaojiawei.hearthstone.strategy.AbstractPhaseStrategy;
import club.xiaojiawei.hearthstone.utils.PowerLogUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.RandomAccessFile;

import static club.xiaojiawei.hearthstone.constant.GameKeyWordConst.FULL_ENTITY;
import static club.xiaojiawei.hearthstone.constant.GameKeyWordConst.TAG_CHANGE;
import static club.xiaojiawei.hearthstone.constant.SystemConst.ROBOT;

/**
 * @author 肖嘉威
 * @date 2022/11/27 13:35
 */
@Slf4j
public class FillDeckAbstractPhaseStrategy extends AbstractPhaseStrategy {


    @SneakyThrows
    @Override
    public void dealing(String l) {
        War.setCurrentPhase(WarPhaseEnum.FILL_DECK_PHASE);
        log.info("当前处于：" + War.getCurrentPhase().getComment());
        RandomAccessFile accessFile = PowerFileListener.getAccessFile();
        while (true) {
            l = accessFile.readLine();
            if (l == null){
                if (accessFile.getFilePointer() > accessFile.length()){
                    accessFile.seek(0);
                }else {
                    ROBOT.delay(1000);
                }
            }else if (PowerFileListener.isRelevance(l)){
                PowerFileListener.setMark(System.currentTimeMillis());
                if (l.contains(FULL_ENTITY)){
                    PowerLogUtil.dealFullEntity(l, accessFile);
                }else if (l.contains(TAG_CHANGE)){
                    log.info(War.getCurrentPhase().getComment() + " -> 结束");
                    break;
                }
            }
        }
    }

}
