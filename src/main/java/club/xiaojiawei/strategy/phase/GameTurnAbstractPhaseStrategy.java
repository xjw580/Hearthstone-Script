package club.xiaojiawei.strategy.phase;

import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.bean.entity.ExtraEntity;
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

import static club.xiaojiawei.enums.ConfigurationKeyEnum.DECK_KEY;
import static club.xiaojiawei.enums.StepEnum.MAIN_ACTION;
import static club.xiaojiawei.enums.StepEnum.MAIN_END;
import static club.xiaojiawei.enums.TagEnum.STEP;

/**
 * @author 肖嘉威
 * @date 2022/11/26 17:24
 */
@Slf4j
@Component
public class GameTurnAbstractPhaseStrategy extends AbstractPhaseStrategy{
    @Resource
    private Properties scriptProperties;

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
                    SystemUtil.updateRect(ScriptStaticData.getGameHWND(), ScriptStaticData.GAME_RECT);
                    stopThread();
                    War.setMyTurn(true);
//                            异步执行出牌策略，以便监听出牌后的卡牌变动
                    (thread = new Thread(() -> {
                        //                          等待动画结束
                        SystemUtil.delay(4000);
                        DeckEnum.valueOf(scriptProperties.getProperty(DECK_KEY.getKey())).getAbstractDeckStrategy().outCard();
                    }, "OutCard Thread")).start();
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

    @Override
    protected boolean dealShowEntityThenIsOver(String line, ExtraEntity extraEntity) {
        return false;
    }

    @Override
    protected boolean dealFullEntityThenIsOver(String line, ExtraEntity extraEntity) {
        return false;
    }

    @Override
    protected boolean dealOtherThenIsOver(String line) {
        return false;
    }

}
