package club.xiaojiawei.strategy.mode;

import club.xiaojiawei.enums.ModeEnum;
import club.xiaojiawei.status.Mode;
import club.xiaojiawei.strategy.AbstractModeStrategy;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 肖嘉威
 * @date 2022/11/25 12:40
 */
@Slf4j
public class CollectionmanagerAbstractModeStrategy extends AbstractModeStrategy {

    @Override
    public void intoMode() {

    }

    @Override
    protected void log() {
        Mode.setCurrMode(ModeEnum.COLLECTIONMANAGER);
        log.info("切換到" + ModeEnum.COLLECTIONMANAGER.getComment());
    }

    @Override
    protected void nextStep() {

    }
}
