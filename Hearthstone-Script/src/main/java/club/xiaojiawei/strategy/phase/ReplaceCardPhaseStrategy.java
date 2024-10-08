package club.xiaojiawei.strategy.phase;

import club.xiaojiawei.bean.log.TagChangeEntity;
import club.xiaojiawei.bean.LogThread;
import club.xiaojiawei.enums.StepEnum;
import club.xiaojiawei.enums.WarPhaseEnum;
import club.xiaojiawei.interfaces.closer.GameThreadCloser;
import club.xiaojiawei.status.War;
import club.xiaojiawei.strategy.AbstractPhaseStrategy;
import club.xiaojiawei.strategy.DeckStrategyActuator;
import club.xiaojiawei.utils.SystemUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Properties;

import static club.xiaojiawei.enums.MulliganStateEnum.INPUT;
import static club.xiaojiawei.enums.TagEnum.MULLIGAN_STATE;
import static club.xiaojiawei.enums.TagEnum.NEXT_STEP;

/**
 * 换牌阶段
 *
 * @author 肖嘉威
 * @date 2022/11/26 17:24
 */
@Slf4j
@Component
public class ReplaceCardPhaseStrategy extends AbstractPhaseStrategy implements GameThreadCloser {

    @Resource
    private Properties scriptConfiguration;
    private Thread thread;

    @SuppressWarnings("all")
    private void stopThread() {
        try {
            if (thread != null && thread.isAlive()) {
                thread.interrupt();
                SystemUtil.delayShortMedium();
                thread.stop();
            }
        } catch (UnsupportedOperationException e) {
//            log.warn("换牌线程已停止", e);
        }
    }

    @Override
    protected boolean dealTagChangeThenIsOver(String line, TagChangeEntity tagChangeEntity) {
        if (tagChangeEntity.getTag() == MULLIGAN_STATE && Objects.equals(tagChangeEntity.getValue(), INPUT.name())) {
            String gameId = tagChangeEntity.getEntity();
            if (Objects.equals(War.INSTANCE.getMe().getGameId(), gameId) || (!War.INSTANCE.getRival().getGameId().isBlank() && !Objects.equals(War.INSTANCE.getRival().getGameId(), gameId))) {
                stopThread();
                //        执行换牌策略
                (thread = new LogThread(() -> {
                    log.info("1号玩家牌库数量：" + War.INSTANCE.getPlayer1().getDeckArea().getCards().size());
                    log.info("2号玩家牌库数量：" + War.INSTANCE.getPlayer2().getDeckArea().getCards().size());
//                    因为傻逼畸变模式导致开局动画增加，这又加了4.5秒
                    SystemUtil.delay(24_500);
                    DeckStrategyActuator.INSTANCE.changeCard();
                }, "Change Card Thread")).start();
            }
        } else if (tagChangeEntity.getTag() == NEXT_STEP && Objects.equals(StepEnum.MAIN_READY.name(), tagChangeEntity.getValue())) {
            War.INSTANCE.setCurrentPhase(WarPhaseEnum.SPECIAL_EFFECT_TRIGGER_PHASE);
            return true;
        }
        return false;
    }

    @Override
    public void closeGameThread() {
        stopThread();
    }
}
