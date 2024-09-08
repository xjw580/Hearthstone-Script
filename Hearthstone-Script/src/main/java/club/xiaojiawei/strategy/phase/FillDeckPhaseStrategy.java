package club.xiaojiawei.strategy.phase;

import club.xiaojiawei.bean.log.TagChangeEntity;
import club.xiaojiawei.controller.javafx.MainController;
import club.xiaojiawei.enums.TagEnum;
import club.xiaojiawei.enums.WarPhaseEnum;
import club.xiaojiawei.status.DeckStrategyManager;
import club.xiaojiawei.status.War;
import club.xiaojiawei.strategy.AbstractPhaseStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;


/**
 * 起始填充牌库阶段
 * @author 肖嘉威
 * @date 2022/11/27 13:35
 */
@Slf4j
@Component
public class FillDeckPhaseStrategy extends AbstractPhaseStrategy {

    @Override
    protected boolean dealTagChangeThenIsOver(String line, TagChangeEntity tagChangeEntity) {
        if (tagChangeEntity.getTag() == TagEnum.TURN && Objects.equals(tagChangeEntity.getValue(), "1")){
            War.setCurrentPhase(WarPhaseEnum.DRAWN_INIT_CARD_PHASE);
            return true;
        }
        return false;
    }

    @Override
    protected boolean dealOtherThenIsOver(String line) {
        if (line.contains("CREATE_GAME")){
            deckStrategyActuator.setDeckStrategy(DeckStrategyManager.CURRENT_DECK_STRATEGY.get());
            War.startWar(DeckStrategyManager.CURRENT_DECK_STRATEGY.get().runMode[0]);
        }
        return super.dealOtherThenIsOver(line);
    }
}
