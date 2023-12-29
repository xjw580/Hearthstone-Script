package club.xiaojiawei.strategy.phase;

import club.xiaojiawei.bean.entity.TagChangeEntity;
import club.xiaojiawei.enums.TagEnum;
import club.xiaojiawei.enums.WarPhaseEnum;
import club.xiaojiawei.status.War;
import club.xiaojiawei.strategy.AbstractPhaseStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static club.xiaojiawei.enums.StepEnum.MAIN_READY;

/**
 * 特殊效果触发阶段（如开局的狼王、巴库、大主教等）
 * @author 肖嘉威
 * @date 2022/11/26 17:23
 */
@Slf4j
@Component
public class SpecialEffectTriggerPhaseStrategy extends AbstractPhaseStrategy{

    @Override
    protected boolean dealTagChangeThenIsOver(String line, TagChangeEntity tagChangeEntity) {
        if (tagChangeEntity.getTag() == TagEnum.STEP && Objects.equals(tagChangeEntity.getValue(), MAIN_READY.name())){
            War.setCurrentPhase(WarPhaseEnum.GAME_TURN_PHASE);
            return true;
        }
        return false;
    }

}
