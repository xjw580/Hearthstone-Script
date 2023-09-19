package club.xiaojiawei.strategy.phase;

import club.xiaojiawei.entity.Card;
import club.xiaojiawei.entity.ExtraEntity;
import club.xiaojiawei.entity.TagChangeEntity;
import club.xiaojiawei.strategy.AbstractPhaseStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * @author 肖嘉威
 * @date 2022/11/27 13:35
 */
@Slf4j
@Component
public class FillDeckAbstractPhaseStrategy extends AbstractPhaseStrategy {
    @Override
    protected boolean dealTagChangeThenIsOver(String s, TagChangeEntity tagChangeEntity) {
        return false;
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
        return s.contains("Block End");
    }

}
