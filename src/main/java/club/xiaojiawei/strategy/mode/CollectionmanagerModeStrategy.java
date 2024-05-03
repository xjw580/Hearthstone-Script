package club.xiaojiawei.strategy.mode;

import club.xiaojiawei.strategy.AbstractModeStrategy;
import club.xiaojiawei.utils.MouseUtil;
import club.xiaojiawei.utils.SystemUtil;
import club.xiaojiawei.utils.WindowUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Properties;

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
        SystemUtil.updateGameRect();
        SystemUtil.delayShort();
        gameUtil.clickBackButton();
    }
}
