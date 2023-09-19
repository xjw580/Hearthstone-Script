package club.xiaojiawei.strategy.mode;

import club.xiaojiawei.enums.ModeEnum;
import club.xiaojiawei.status.Mode;
import club.xiaojiawei.strategy.AbstractModeStrategy;
import club.xiaojiawei.utils.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author 肖嘉威
 * @date 2022/11/25 12:43
 */
@Slf4j
@Component
public class GameplayAbstractModeStrategy extends AbstractModeStrategy<Object> {

    @Override
    public void wantEnter() {
    }

    @Override
    protected void afterEnter(Object o) {
        if (Mode.getPrevMode() == ModeEnum.LOGIN || Mode.getPrevMode() == null){
//            投降
            log.info("当前对局不完整，准备投降");
            gameUtil.surrender();
        }
    }

}
