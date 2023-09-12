package club.xiaojiawei.strategy.phase;

import club.xiaojiawei.data.GameStaticData;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.entity.TagChangeEntity;
import club.xiaojiawei.enums.StepEnum;
import club.xiaojiawei.listener.ScreenFileListener;
import club.xiaojiawei.status.War;
import club.xiaojiawei.strategy.AbstractPhaseStrategy;
import club.xiaojiawei.utils.PowerLogUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.RandomAccessFile;
import java.util.Objects;

import static club.xiaojiawei.enums.WarPhaseEnum.GAME_OVER_PHASE;


/**
 * @author 肖嘉威
 * @date 2022/11/27 13:35
 */
@Slf4j
@Component
public class FillDeckAbstractPhaseStrategy extends AbstractPhaseStrategy<String> {


    @SneakyThrows
    @Override
    protected void execute(String l, RandomAccessFile accessFile) {
        TagChangeEntity tagChangeEntity;
        while (!isPause.get().get()) {
            l = accessFile.readLine();
            if (l == null) {
                if (accessFile.getFilePointer() > accessFile.length()) {
                    accessFile.seek(0);
                } else {
                    ScriptStaticData.ROBOT.delay(1000);
                }
            } else if (powerFileListener.isRelevance(l)) {
                ScreenFileListener.setMark(System.currentTimeMillis());
                if (l.contains(GameStaticData.FULL_ENTITY)) {
                    PowerLogUtil.dealFullEntity(l, accessFile);
                } else if (l.contains(GameStaticData.TAG_CHANGE)) {
                    tagChangeEntity = PowerLogUtil.parseTagChange(l);
                    if (Objects.equals(tagChangeEntity.getValue(), StepEnum.FINAL_GAMEOVER.getValue())){
                        War.setCurrentPhase(GAME_OVER_PHASE, l);
                    }
                    break;
                }
            }
        }
    }

}
