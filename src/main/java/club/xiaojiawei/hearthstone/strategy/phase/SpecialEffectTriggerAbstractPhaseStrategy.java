package club.xiaojiawei.hearthstone.strategy.phase;

import club.xiaojiawei.hearthstone.enums.WarPhaseEnum;
import club.xiaojiawei.hearthstone.listener.PowerFileListener;
import club.xiaojiawei.hearthstone.status.War;
import club.xiaojiawei.hearthstone.strategy.AbstractPhaseStrategy;
import club.xiaojiawei.hearthstone.utils.PowerLogUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.RandomAccessFile;

import static club.xiaojiawei.hearthstone.constant.GameKeyWordConst.*;
import static club.xiaojiawei.hearthstone.constant.SystemConst.ROBOT;

/**
 * @author 肖嘉威
 * @date 2022/11/26 17:23
 */
@Slf4j
public class SpecialEffectTriggerAbstractPhaseStrategy extends AbstractPhaseStrategy {

    @SneakyThrows
    @Override
    public void dealing(String l) {
        War.setCurrentPhase(WarPhaseEnum.SPECIAL_EFFECT_TRIGGER_PHASE);
        log.info("当前处于：" + War.getCurrentPhase().getComment());
        RandomAccessFile accessFile = PowerFileListener.getAccessFile();
        long mark = accessFile.getFilePointer();
        while (true) {
            if ((l = accessFile.readLine()) == null) {
                if (accessFile.getFilePointer() > accessFile.length()){
                    accessFile.seek(0);
                }else {
                    ROBOT.delay(1000);
                }
            }else if (PowerFileListener.isRelevance(l)){
                PowerFileListener.setMark(System.currentTimeMillis());
               if (l.contains("MAIN_READY")){
                    accessFile.seek(mark);
                   log.info(War.getCurrentPhase().getComment() + " -> 结束");
                    break;
                }else if (l.contains(SHOW_ENTITY)){
                   PowerLogUtil.dealShowEntity(l, accessFile);
               }else if (l.contains(FULL_ENTITY)){
                   PowerLogUtil.dealFullEntity(l, accessFile);
               }else if (l.contains(TAG_CHANGE)){
                   PowerLogUtil.dealTagChange(PowerLogUtil.parseTagChange(l));
               }
            }
            mark = accessFile.getFilePointer();
        }
    }


}
