package club.xiaojiawei.strategy.phase;

import club.xiaojiawei.custom.LogRunnable;
import club.xiaojiawei.bean.entity.TagChangeEntity;
import club.xiaojiawei.enums.DeckEnum;
import club.xiaojiawei.status.War;
import club.xiaojiawei.strategy.AbstractPhaseStrategy;
import club.xiaojiawei.utils.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.Properties;

import static club.xiaojiawei.enums.ConfigurationEnum.DECK;
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
public class GameTurnPhaseStrategy extends AbstractPhaseStrategy{
    @Resource
    private Properties scriptConfiguration;

    private static Thread thread;

    @SuppressWarnings("all")
    public static void stopThread(){
        try{
            if (thread != null && thread.isAlive()){
                thread.stop();
                log.info("出牌线程已停止");
            }
        }catch (Exception e){
            log.warn("出牌线程已停止");
        }
    }

    @Override
    protected boolean dealTagChangeThenIsOver(String line, TagChangeEntity tagChangeEntity) {
        if (tagChangeEntity.getTag() == STEP){
            if (Objects.equals(tagChangeEntity.getValue(), MAIN_ACTION.getValue())){
                if (War.getMe() == War.getCurrentPlayer()){
                    log.info("我方回合");
                    SystemUtil.updateGameRect();
                    stopThread();
                    War.setMyTurn(true);
//                            异步执行出牌策略，以便监听出牌后的卡牌变动
                    (thread = new Thread(new LogRunnable(() -> {
                        //                          等待动画结束
                        SystemUtil.delay(4000);
                        DeckEnum.valueOf(scriptConfiguration.getProperty(DECK.getKey())).getAbstractDeckStrategy().outCard();
                    }), "OutCard Thread")).start();
                }else {
                    log.info("对方回合");
                }
            }else if (Objects.equals(tagChangeEntity.getValue(), MAIN_END.getValue())){
                War.setMyTurn(false);
                stopThread();
            }
        }
        return false;
    }

}
