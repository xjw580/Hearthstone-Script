package club.xiaojiawei.hearthstone.strategy;

import club.xiaojiawei.hearthstone.run.Core;
import club.xiaojiawei.hearthstone.status.War;
import club.xiaojiawei.hearthstone.utils.SystemUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 肖嘉威
 * @date 2022/11/26 17:59
 */
@Slf4j
public abstract class PhaseStrategy implements Strategy<String>{

    public static boolean dealing = true;
    @Override
    public void afterInto() {
        afterInto("");
    }

    @Override
    public void afterInto(String l) {
        dealing = true;
        SystemUtil.frontWindow(Core.getGameHWND());
        dealing(l);
        if (War.getCurrentPhase() != null){
            dealing = false;
        }
    }

    public abstract void dealing(String l);

}
