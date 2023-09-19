package club.xiaojiawei.strategy;

import club.xiaojiawei.entity.Card;
import club.xiaojiawei.entity.ExtraEntity;
import club.xiaojiawei.entity.TagChangeEntity;
import club.xiaojiawei.enums.StepEnum;
import club.xiaojiawei.listener.PowerFileListener;
import club.xiaojiawei.status.War;
import club.xiaojiawei.utils.GameUtil;
import club.xiaojiawei.utils.PowerLogUtil;
import club.xiaojiawei.utils.SystemUtil;
import javafx.beans.property.BooleanProperty;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.atomic.AtomicReference;

import static club.xiaojiawei.data.ScriptStaticData.*;

/**
 * @author 肖嘉威
 * @date 2022/11/26 17:59
 */
@Slf4j
public abstract class AbstractPhaseStrategy{
    @Resource
    protected PowerFileListener powerFileListener;
    @Resource
    protected GameUtil gameUtil;
    @Resource
    protected AtomicReference<BooleanProperty> isPause;
    /**
     * 告诉Power.log监听器，AbstractPhaseStrategy是否正在处理日志
     */
    @Getter
    private volatile static boolean dealing;

    public void deal(String s) {
        dealing = true;
        beforeDeal();
        dealLog(s);
        afterDeal();
        dealing = false;
    }
    private void dealLog(String s){
        RandomAccessFile accessFile = PowerFileListener.getAccessFile();
        while (true) {
            if (isPause.get().get()){
                return;
            }
            try {
                if (s == null) {
                    SystemUtil.delay(1000);
                }else if (powerFileListener.isRelevance(s)){
                    if (log.isDebugEnabled()){
                        log.debug(s);
                    }
                    if (s.contains(TAG_CHANGE)){
                        if (dealTagChangeThenIsOver(s, PowerLogUtil.dealTagChange(s)) || War.getCurrentTurnStep() == StepEnum.FINAL_GAMEOVER){
                            break;
                        }
                    }else if (s.contains(SHOW_ENTITY)){
                        if (dealShowEntityThenIsOver(s, PowerLogUtil.dealShowEntity(s, accessFile))){
                            break;
                        }
                    }else if (s.contains(FULL_ENTITY)){
                        if (dealFullEntityThenIsOver(s, PowerLogUtil.dealFullEntity(s, accessFile))){
                            break;
                        }
                    }else {
                        if (dealOtherThenIsOver(s)){
                            break;
                        }
                    }
                }
                s = accessFile.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    protected void beforeDeal(){
        log.info("当前处于：" + War.getCurrentPhase().getComment());
    }
    protected void afterDeal(){
        log.info(War.getCurrentPhase().getComment() + " -> 结束");
    }

    protected abstract boolean dealTagChangeThenIsOver(String s, TagChangeEntity tagChangeEntity);
    protected abstract boolean dealShowEntityThenIsOver(String s, ExtraEntity extraEntity);
    protected abstract boolean dealFullEntityThenIsOver(String s, ExtraEntity extraEntity);
    protected abstract boolean dealOtherThenIsOver(String s);
}
