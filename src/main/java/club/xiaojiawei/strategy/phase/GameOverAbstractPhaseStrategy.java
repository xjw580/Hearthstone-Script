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
 * @author 肖嘉威
 * @date 2022/11/27 13:44
 */
@Slf4j
@Component
public class GameOverAbstractPhaseStrategy extends AbstractPhaseStrategy{

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
        RandomAccessFile accessFile = powerLogListener.getAccessFile();
        try {
            accessFile.seek(accessFile.length());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        War.increaseWarCount();
        SystemUtil.stopAllThread();
        gameUtil.clickGameEndPageTask();
    }
}
