package club.xiaojiawei.strategy.phase;

import club.xiaojiawei.bean.entity.TagChangeEntity;
import club.xiaojiawei.custom.LogRunnable;
import club.xiaojiawei.enums.ConfigurationKeyEnum;
import club.xiaojiawei.enums.DeckEnum;
import club.xiaojiawei.enums.StepEnum;
import club.xiaojiawei.enums.WarPhaseEnum;
import club.xiaojiawei.status.War;
import club.xiaojiawei.strategy.AbstractPhaseStrategy;
import club.xiaojiawei.utils.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.Properties;

import static club.xiaojiawei.enums.MulliganStateEnum.INPUT;
import static club.xiaojiawei.enums.TagEnum.MULLIGAN_STATE;
import static club.xiaojiawei.enums.TagEnum.NEXT_STEP;

/**
 * @author 肖嘉威
 * @date 2022/11/26 17:24
 */
@Slf4j
@Component
public class ReplaceCardPhaseStrategy extends AbstractPhaseStrategy{

    @Resource
    private Properties scriptConfiguration;
    private static Thread thread;

    @SuppressWarnings("all")
    public static void stopThread(){
        try{
            if (thread != null && thread.isAlive()){
                thread.stop();
                log.info("换牌线程已停止");
            }
        }catch (Exception e){
            log.warn("换牌线程已停止");
        }
    }

    @Override
    protected boolean dealTagChangeThenIsOver(String line, TagChangeEntity tagChangeEntity) {
        if (tagChangeEntity.getTag() == MULLIGAN_STATE && Objects.equals(tagChangeEntity.getValue(), INPUT.getValue())){
            String gameId = tagChangeEntity.getEntity();
            if (Objects.equals(War.getMe().getGameId(), gameId) || (War.getRival().getGameId() != null && !Objects.equals(War.getRival().getGameId(), gameId))){
                //            等待动画
                stopThread();
                //        执行换牌策略
                (thread = new Thread(new LogRunnable(() -> {
//                    因为傻逼畸变模式导致开局动画增加，这又加了4.5秒
                    SystemUtil.delay(24_500);
                    DeckEnum.valueOf(scriptConfiguration.getProperty(ConfigurationKeyEnum.DECK_KEY.getKey())).getAbstractDeckStrategy().changeCard();
                }))).start();
            }
        }else if (tagChangeEntity.getTag() == NEXT_STEP && Objects.equals(StepEnum.MAIN_READY.getValue(), tagChangeEntity.getValue())){
            War.setCurrentPhase(WarPhaseEnum.SPECIAL_EFFECT_TRIGGER_PHASE);
            return true;
        }
        return false;
    }

}
