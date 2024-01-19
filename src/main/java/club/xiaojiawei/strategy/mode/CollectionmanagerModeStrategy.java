package club.xiaojiawei.strategy.mode;

import club.xiaojiawei.strategy.AbstractModeStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 我的收藏
 * @author 肖嘉威
 * @date 2022/11/25 12:40
 */
@Slf4j
@Component
public class CollectionmanagerModeStrategy extends AbstractModeStrategy<Object> {

    @Override
    public void wantEnter() {
    }

    @Override
    protected void afterEnter(Object o) {
    }
}
