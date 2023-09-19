package club.xiaojiawei.strategy.phase;

import club.xiaojiawei.entity.Card;
import club.xiaojiawei.entity.ExtraEntity;
import club.xiaojiawei.entity.TagChangeEntity;
import club.xiaojiawei.listener.PowerFileListener;
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
    protected boolean dealTagChangeThenIsOver(String s, TagChangeEntity tagChangeEntity) {
        over();
        return true;
    }

    @Override
    protected boolean dealShowEntityThenIsOver(String s, ExtraEntity extraEntity) {
        over();
        return true;
    }

    @Override
    protected boolean dealFullEntityThenIsOver(String s, ExtraEntity extraEntity) {
        over();
        return true;
    }

    @Override
    protected boolean dealOtherThenIsOver(String s) {
        over();
        return true;
    }

    private void over(){
        RandomAccessFile accessFile = PowerFileListener.getAccessFile();
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
