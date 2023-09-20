package club.xiaojiawei.strategy;

import club.xiaojiawei.bean.entity.Card;
import club.xiaojiawei.bean.entity.ExtraEntity;
import club.xiaojiawei.bean.entity.TagChangeEntity;
import club.xiaojiawei.enums.ConfigurationKeyEnum;
import club.xiaojiawei.enums.DeckEnum;
import club.xiaojiawei.enums.StepEnum;
import club.xiaojiawei.listener.PowerLogListener;
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
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import static club.xiaojiawei.data.ScriptStaticData.*;

/**
 * @author 肖嘉威
 * @date 2022/11/26 17:59
 */
@Slf4j
public abstract class AbstractPhaseStrategy{
    @Resource
    protected PowerLogListener powerLogListener;
    @Resource
    protected GameUtil gameUtil;
    @Resource
    protected AtomicReference<BooleanProperty> isPause;
    @Resource
    protected Properties scriptProperties;
    /**
     * 告诉Power.log监听器，AbstractPhaseStrategy是否正在处理日志
     */
    @Getter
    private volatile static boolean dealing;

    public void deal(String line) {
        dealing = true;
        beforeDeal();
        dealLog(line);
        afterDeal();
        dealing = false;
    }
    private void dealLog(String line){
        RandomAccessFile accessFile = powerLogListener.getAccessFile();
        long mark;
        while (true) {
            if (isPause.get().get()){
                return;
            }
            try {
                if (line == null) {
                    mark = accessFile.getFilePointer();
                    SystemUtil.delay(1000);
                    if (accessFile.length() <= mark){
                        List<Card> cards = War.getMe().getSetasideArea().getCards();
                        int size = cards.size();
                        if (size >= 3
                                && Objects.equals(cards.get(size - 1).getCreator(), cards.get(size - 2).getCreator())
                                && Objects.equals(cards.get(size - 1).getCreator(), cards.get(size - 3).getCreator())
                        ){
                            log.info("触发发现动作");
                            DeckEnum.valueOf(scriptProperties.getProperty(ConfigurationKeyEnum.DECK_KEY.getKey())).getAbstractDeckStrategy().discoverChooseCard(cards.get(size - 3), cards.get(size - 2), cards.get(size - 1));
                        }
                    }
                }else if (powerLogListener.isRelevance(line)){
                    if (log.isDebugEnabled()){
                        log.debug(line);
                    }
                    if (line.contains(TAG_CHANGE)){
                        if (dealTagChangeThenIsOver(line, PowerLogUtil.dealTagChange(line)) || War.getCurrentTurnStep() == StepEnum.FINAL_GAMEOVER){
                            break;
                        }
                    }else if (line.contains(SHOW_ENTITY)){
                        if (dealShowEntityThenIsOver(line, PowerLogUtil.dealShowEntity(line, accessFile))){
                            break;
                        }
                    }else if (line.contains(FULL_ENTITY)){
                        if (dealFullEntityThenIsOver(line, PowerLogUtil.dealFullEntity(line, accessFile))){
                            break;
                        }
                    }else {
                        if (dealOtherThenIsOver(line)){
                            break;
                        }
                    }
                }
                line = accessFile.readLine();
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

    protected abstract boolean dealTagChangeThenIsOver(String line, TagChangeEntity tagChangeEntity);
    protected abstract boolean dealShowEntityThenIsOver(String line, ExtraEntity extraEntity);
    protected abstract boolean dealFullEntityThenIsOver(String line, ExtraEntity extraEntity);
    protected abstract boolean dealOtherThenIsOver(String line);
}
