package club.xiaojiawei.strategy;

import club.xiaojiawei.run.Core;
import club.xiaojiawei.status.War;
import club.xiaojiawei.utils.SystemUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 肖嘉威
 * @date 2022/11/26 17:59
 */
@Slf4j
public abstract class AbstractPhaseStrategy implements Strategy<String>{

    public volatile static boolean dealing = true;
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

    /**
     * 进入到该时期后应该如何处理
     * @param l
     */
    public abstract void dealing(String l);

}
