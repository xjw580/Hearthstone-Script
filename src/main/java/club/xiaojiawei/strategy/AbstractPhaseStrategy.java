package club.xiaojiawei.strategy;

import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.listener.PowerFileListener;
import club.xiaojiawei.status.War;
import club.xiaojiawei.utils.GameUtil;
import club.xiaojiawei.utils.SystemUtil;
import javafx.beans.property.BooleanProperty;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.io.RandomAccessFile;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author 肖嘉威
 * @date 2022/11/26 17:59
 */
@Slf4j
public abstract class AbstractPhaseStrategy<T>{
    @Resource
    protected PowerFileListener powerFileListener;
    @Resource
    protected SystemUtil systemUtil;
    @Resource
    protected GameUtil gameUtil;
    @Resource
    protected AtomicReference<BooleanProperty> isPause;
    private volatile static boolean dealing = false;

    public static boolean isDealing() {
        return dealing;
    }
    public static void setDealing(boolean dealing) {
        AbstractPhaseStrategy.dealing = dealing;
    }

    public void dealing() {
        dealing(null);
    }

    public void dealing(T t) {
        dealing = true;
        beforeExecute();
        systemUtil.frontWindow(ScriptStaticData.getGameHWND());
        execute(t, PowerFileListener.getAccessFile());
        afterExecute();
        if (War.getCurrentPhase() != null){
            dealing = false;
        }
    }
    protected void beforeExecute(){
        log.info("当前处于：" + War.getCurrentPhase().getComment());
    }
    protected void afterExecute(){
        if (War.getCurrentPhase() != null){
            log.info(War.getCurrentPhase().getComment() + " -> 结束");
        }
    }

    /**
     * 进入到该时期后应该如何处理
     * @param t
     */
    protected abstract void execute(T t, RandomAccessFile accessFile);

}
