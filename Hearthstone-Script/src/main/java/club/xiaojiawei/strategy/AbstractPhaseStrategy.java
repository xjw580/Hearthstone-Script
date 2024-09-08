package club.xiaojiawei.strategy;

import club.xiaojiawei.bean.Card;
import club.xiaojiawei.bean.log.ExtraEntity;
import club.xiaojiawei.bean.log.TagChangeEntity;
import club.xiaojiawei.enums.StepEnum;
import club.xiaojiawei.enums.WarPhaseEnum;
import club.xiaojiawei.interfaces.PhaseStrategy;
import club.xiaojiawei.listener.log.PowerLogListener;
import club.xiaojiawei.status.War;
import club.xiaojiawei.strategy.phase.ReplaceCardPhaseStrategy;
import club.xiaojiawei.utils.GameUtil;
import club.xiaojiawei.utils.PowerLogUtil;
import club.xiaojiawei.utils.SystemUtil;
import jakarta.annotation.Resource;
import javafx.beans.property.BooleanProperty;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import static club.xiaojiawei.data.ScriptStaticData.*;
import static club.xiaojiawei.enums.WarPhaseEnum.REPLACE_CARD_PHASE;

/**
 * 游戏阶段抽象类
 * @author 肖嘉威
 * @date 2022/11/26 17:59
 */
@Slf4j
public abstract class AbstractPhaseStrategy implements PhaseStrategy {

    @Resource
    protected PowerLogListener powerLogListener;
    @Resource
    protected GameUtil gameUtil;
    @Resource
    protected AtomicReference<BooleanProperty> isPause;
    @Resource
    protected Properties scriptConfiguration;
    @Resource
    protected DeckStrategyActuator deckStrategyActuator;
    /**
     * 告诉Power.log监听器，AbstractPhaseStrategy是否正在处理日志
     */
    @Getter
    private volatile static boolean dealing;
    private String lastDiscoverEntityId;

    @Override
    public void deal(String line) {
        dealing = true;
        try{
            beforeDeal();
            dealLog(line);
            afterDeal();
        }finally {
            dealing = false;
        }
    }

    private void dealLog(String line){
        RandomAccessFile accessFile = powerLogListener.getAccessFile();
        long mark;
        while (!isPause.get().get()) {
            try {
                if (line == null) {
                    mark = accessFile.getFilePointer();
                    SystemUtil.delay(1000);
                    if (accessFile.length() <= mark && War.getMe() != null){
                        List<Card> cards = War.getMe().getSetasideArea().getCards();
                        int size = cards.size();
                        if (
                                size >= 3
                                && !Objects.equals(lastDiscoverEntityId, cards.getLast().getEntityId())
                                && Objects.equals(cards.get(size - 1).getCreator(), cards.get(size - 2).getCreator())
                                && Objects.equals(cards.get(size - 1).getCreator(), cards.get(size - 3).getCreator())
                        ){
                            log.info("触发发现动作");
                            lastDiscoverEntityId = cards.getLast().getEntityId();
                            if (War.getCurrentPhase() != REPLACE_CARD_PHASE){
                                deckStrategyActuator.discoverChooseCard(cards.get(size - 3), cards.get(size - 2), cards.get(size - 1));
                            }
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
                    }else if (line.contains(CHANGE_ENTITY)){
                        if (dealChangeEntityThenIsOver(line, PowerLogUtil.dealChangeEntity(line, accessFile))){
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
        for (WarPhaseEnum value : WarPhaseEnum.values()) {
            if (Objects.equals(this.getClass().getName(), value.getPhaseStrategyClassName())){
                log.info("当前处于：" + value.getComment());
            }
        }
    }
    protected void afterDeal(){
        for (WarPhaseEnum value : WarPhaseEnum.values()) {
            if (Objects.equals(this.getClass().getName(), value.getPhaseStrategyClassName())){
                log.info(value.getComment() + " -> 结束");
            }
        }
    }

    protected boolean dealTagChangeThenIsOver(String line, TagChangeEntity tagChangeEntity){
        return false;
    }
    protected boolean dealShowEntityThenIsOver(String line, ExtraEntity extraEntity){
        return false;
    }
    protected boolean dealFullEntityThenIsOver(String line, ExtraEntity extraEntity){
        return false;
    }
    protected boolean dealChangeEntityThenIsOver(String line, ExtraEntity extraEntity){
        return false;
    }
    protected boolean dealOtherThenIsOver(String line){
        return false;
    }

}
