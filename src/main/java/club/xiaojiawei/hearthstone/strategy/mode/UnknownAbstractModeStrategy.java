package club.xiaojiawei.hearthstone.strategy.mode;

import club.xiaojiawei.hearthstone.strategy.AbstractModeStrategy;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 肖嘉威
 * @date 2022/11/25 13:00
 */
@Slf4j
public class UnknownAbstractModeStrategy extends AbstractModeStrategy {

    @Override
    public void intoMode() {

    }

    @Override
    protected void log() {
        log.warn("未知模式，请联系作者更新，email：xjw580@qq.com");
    }

    @Override
    protected void nextStep() {

    }
}
