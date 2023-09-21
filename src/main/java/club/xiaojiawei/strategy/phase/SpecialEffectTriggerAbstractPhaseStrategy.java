package club.xiaojiawei.strategy.phase;

import club.xiaojiawei.bean.entity.ExtraEntity;
import club.xiaojiawei.bean.entity.TagChangeEntity;
import club.xiaojiawei.enums.TagEnum;
import club.xiaojiawei.strategy.AbstractPhaseStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static club.xiaojiawei.enums.StepEnum.MAIN_READY;

/**
 * @author 肖嘉威
 * @date 2022/11/26 17:23
 */
@Slf4j
@Component
public class SpecialEffectTriggerAbstractPhaseStrategy extends AbstractPhaseStrategy{

    @Override
    protected boolean dealTagChangeThenIsOver(String line, TagChangeEntity tagChangeEntity) {
        return tagChangeEntity.getTag() == TagEnum.STEP && Objects.equals(tagChangeEntity.getValue(), MAIN_READY.getValue());
    }

}
