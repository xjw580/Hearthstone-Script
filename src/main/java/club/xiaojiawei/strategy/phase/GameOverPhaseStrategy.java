package club.xiaojiawei.strategy.phase;

import club.xiaojiawei.bean.entity.ExtraEntity;
import club.xiaojiawei.bean.entity.TagChangeEntity;
import club.xiaojiawei.status.War;
import club.xiaojiawei.strategy.AbstractPhaseStrategy;
import club.xiaojiawei.utils.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 游戏结束阶段
 * @author 肖嘉威
 * @date 2022/11/27 13:44
 */
@Slf4j
@Component
public class GameOverPhaseStrategy extends AbstractPhaseStrategy{

    @Override
    protected boolean dealTagChangeThenIsOver(String line, TagChangeEntity tagChangeEntity) {
        over();
        return true;
    }

    @Override
    protected boolean dealShowEntityThenIsOver(String line, ExtraEntity extraEntity) {
        over();
        return true;
    }

    @Override
    protected boolean dealFullEntityThenIsOver(String line, ExtraEntity extraEntity) {
        over();
        return true;
    }

    @Override
    protected boolean dealChangeEntityThenIsOver(String line, ExtraEntity extraEntity) {
        over();
        return true;
    }

    @Override
    protected boolean dealOtherThenIsOver(String line) {
        over();
        return true;
    }

    private void over(){
        War.setMyTurn(false);
        SystemUtil.delay(1000);
        SystemUtil.stopAllThread();
        War.increaseWarCount();
        try {
            SystemUtil.delay(1000);
            RandomAccessFile accessFile = powerLogListener.getAccessFile();
            accessFile.seek(accessFile.length());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        gameUtil.clickGameEndPageTask();
    }
}
