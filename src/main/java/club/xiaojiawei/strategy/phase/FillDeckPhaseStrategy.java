package club.xiaojiawei.strategy.phase;

import club.xiaojiawei.bean.entity.TagChangeEntity;
import club.xiaojiawei.enums.TagEnum;
import club.xiaojiawei.enums.WarPhaseEnum;
import club.xiaojiawei.status.War;
import club.xiaojiawei.strategy.AbstractPhaseStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;


/**
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

}
