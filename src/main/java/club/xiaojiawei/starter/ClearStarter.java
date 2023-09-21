package club.xiaojiawei.starter;

import club.xiaojiawei.status.Mode;
import club.xiaojiawei.utils.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author 肖嘉威
 * @date 2023/7/6 10:46
 * @msg
 */
@Component
@Slf4j
public class ClearStarter extends AbstractStarter{

    @Override
    public void exec() {
        SystemUtil.cancelAllRunnable();
        Mode.reset();
        if (nextStarter != null){
            nextStarter.start();
        }
    }
}
