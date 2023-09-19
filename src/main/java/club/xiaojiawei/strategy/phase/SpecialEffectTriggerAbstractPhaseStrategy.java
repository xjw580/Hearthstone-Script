package club.xiaojiawei.strategy.phase;

import club.xiaojiawei.entity.Card;
import club.xiaojiawei.entity.ExtraEntity;
import club.xiaojiawei.entity.TagChangeEntity;
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
    protected boolean dealTagChangeThenIsOver(String s, TagChangeEntity tagChangeEntity) {
        return tagChangeEntity.getTag() == TagEnum.STEP && Objects.equals(tagChangeEntity.getValue(), MAIN_READY.getValue());
    }

    @Override
    protected boolean dealShowEntityThenIsOver(String s, ExtraEntity extraEntity) {
        return false;
    }

    @Override
    protected boolean dealFullEntityThenIsOver(String s, ExtraEntity extraEntity) {
        return false;
    }

    @Override
    protected boolean dealOtherThenIsOver(String s) {
        return false;
    }
}
