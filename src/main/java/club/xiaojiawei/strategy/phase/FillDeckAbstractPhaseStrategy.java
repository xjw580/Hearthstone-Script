package club.xiaojiawei.strategy.phase;

import club.xiaojiawei.listener.PowerFileListener;
import club.xiaojiawei.strategy.AbstractPhaseStrategy;
import club.xiaojiawei.utils.PowerLogUtil;
import club.xiaojiawei.data.GameStaticData;
import club.xiaojiawei.data.ScriptStaticData;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.RandomAccessFile;


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
        while (!isPause.get().get()) {
            l = accessFile.readLine();
            if (l == null) {
                if (accessFile.getFilePointer() > accessFile.length()) {
                    accessFile.seek(0);
                } else {
                    ScriptStaticData.ROBOT.delay(1000);
                }
            } else if (powerFileListener.isRelevance(l)) {
                PowerFileListener.setMark(System.currentTimeMillis());
                if (l.contains(GameStaticData.FULL_ENTITY)) {
                    PowerLogUtil.dealFullEntity(l, accessFile);
                } else if (l.contains(GameStaticData.TAG_CHANGE)) {
                    break;
                }
            }
        }
    }

}
