package club.xiaojiawei.strategy.phase;

import club.xiaojiawei.listener.ScreenFileListener;
import club.xiaojiawei.status.War;
import club.xiaojiawei.strategy.AbstractPhaseStrategy;
import club.xiaojiawei.utils.PowerLogUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.RandomAccessFile;

/**
 * @author 肖嘉威
 * @date 2022/11/27 13:44
 */
@Slf4j
@Component
public class GameOverAbstractPhaseStrategy extends AbstractPhaseStrategy<String> {

    @SneakyThrows
    @Override
    protected void execute(String l, RandomAccessFile accessFile) {
        systemUtil.delayMedium();
//        宣布本局游戏胜者，败者
        while (true) {
            if (isPause.get().get()){
                return;
            }
            if ((l = accessFile.readLine()) == null) {
                if (accessFile.getFilePointer() > accessFile.length()){
                    accessFile.seek(0);
                }
                break;
            }else if (powerFileListener.isRelevance(l)){
                ScreenFileListener.setMark(System.currentTimeMillis());
                if (l.contains("TAG_CHANGE")){
                    PowerLogUtil.dealTagChange(PowerLogUtil.parseTagChange(l));
                }
            }
        }
        War.increaseWarCount();
        gameUtil.clickGameEndPageTask();
    }

    @Override
    protected void afterExecute() {
        super.afterExecute();
        War.reset();
    }
}
