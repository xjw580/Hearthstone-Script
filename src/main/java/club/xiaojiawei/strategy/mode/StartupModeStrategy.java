package club.xiaojiawei.strategy.mode;

import club.xiaojiawei.strategy.AbstractModeStrategy;
import org.springframework.stereotype.Component;

/**
 * 准备界面
 * @author 肖嘉威
 * @date 2023/7/6 16:28
 */
@Component
public class StartupModeStrategy extends AbstractModeStrategy<Object> {
    @Override
    public void wantEnter() {
    }

    @Override
    protected void afterEnter(Object o) {
    }
}
