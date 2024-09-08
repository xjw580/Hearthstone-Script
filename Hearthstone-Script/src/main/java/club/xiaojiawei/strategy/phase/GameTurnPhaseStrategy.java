package club.xiaojiawei.strategy.phase;

import club.xiaojiawei.bean.log.TagChangeEntity;
import club.xiaojiawei.bean.LogThread;
import club.xiaojiawei.interfaces.closer.GameThreadCloser;
import club.xiaojiawei.status.War;
import club.xiaojiawei.strategy.AbstractPhaseStrategy;
import club.xiaojiawei.strategy.DeckStrategyActuator;
import club.xiaojiawei.utils.SystemUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static club.xiaojiawei.enums.StepEnum.MAIN_ACTION;
import static club.xiaojiawei.enums.StepEnum.MAIN_END;
import static club.xiaojiawei.enums.TagEnum.STEP;

/**
 * 游戏回合阶段
 * @author 肖嘉威
 * @date 2022/11/26 17:24
 */
@Slf4j
@Component
public class GameTurnPhaseStrategy extends AbstractPhaseStrategy implements GameThreadCloser {


    private Thread thread;
    @Resource
    private DeckStrategyActuator deckStrategyActuator;

    @SuppressWarnings("all")
    private void stopThread(){
        try{
            if (thread != null && thread.isAlive()){
                thread.interrupt();
                SystemUtil.delayShortMedium();
                thread.stop();
            }
        }catch (UnsupportedOperationException e){
//            log.warn("出牌线程已停止", e);
        }
    }

    @Override
    protected boolean dealTagChangeThenIsOver(String line, TagChangeEntity tagChangeEntity) {
        if (tagChangeEntity.getTag() == STEP){
            if (Objects.equals(tagChangeEntity.getValue(), MAIN_ACTION.name())){
                if (War.INSTANCE.getMe() == War.INSTANCE.getCurrentPlayer()){
                    log.info("我方回合");
                    SystemUtil.updateGameRect();
                    stopThread();
                    War.INSTANCE.setMyTurn(true);
                    // 异步执行出牌策略，以便监听出牌后的卡牌变动
                    (thread = new LogThread(() -> {
                        // 等待动画结束
                        SystemUtil.delay(4000);
                        deckStrategyActuator.outCard();
                    }, "OutCard Thread")).start();
                }else {
                    log.info("对方回合");
                    War.INSTANCE.setMyTurn(false);
                    stopThread();
                }
            }else if (Objects.equals(tagChangeEntity.getValue(), MAIN_END.name())){
                War.INSTANCE.setMyTurn(false);
                stopThread();
            }
        }
        return false;
    }

    @Override
    public void closeGameThread() {
        stopThread();
    }

}
