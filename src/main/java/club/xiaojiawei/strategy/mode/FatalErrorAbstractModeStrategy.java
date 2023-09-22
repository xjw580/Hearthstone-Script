package club.xiaojiawei.strategy.mode;

import club.xiaojiawei.core.Core;
import club.xiaojiawei.strategy.AbstractModeStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author 肖嘉威
 * @date 2022/12/10 22:35
 */
@Slf4j
@Component
public class FatalErrorAbstractModeStrategy extends AbstractModeStrategy<Object> {
    @Resource
    private Core core;
    @Override
    public void wantEnter() {

    }
    @Override
    protected void afterEnter(Object o) {
        log.info("发生致命错误，准备重启游戏");
        core.restart();
    }
}
