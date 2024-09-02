package club.xiaojiawei.strategy.mode;

import club.xiaojiawei.bean.GameRect;
import club.xiaojiawei.enums.ModeEnum;
import club.xiaojiawei.status.Mode;
import club.xiaojiawei.strategy.AbstractModeStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 游戏界面
 *
 * @author 肖嘉威
 * @date 2022/11/25 12:43
 */
@Slf4j
@Component
public class GameplayModeStrategy extends AbstractModeStrategy<Object> {

    @Override
    public void wantEnter() {
    }

    @Override
    protected void afterEnter(Object o) {
        if (Mode.getPrevMode() == ModeEnum.LOGIN || Mode.getPrevMode() == null) {
            log.info("当前对局不完整，准备投降");
            gameUtil.surrender();
        }
    }

}
