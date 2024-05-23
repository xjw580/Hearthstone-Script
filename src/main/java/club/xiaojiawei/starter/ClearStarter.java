package club.xiaojiawei.starter;

import club.xiaojiawei.status.Mode;
import club.xiaojiawei.utils.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 关闭任务，状态重置
 * @author 肖嘉威
 * @date 2023/7/6 10:46
 */
@Component
@Slf4j
public class ClearStarter extends AbstractStarter{

    @Override
    public void exec() {
        SystemUtil.closeAll();
        Mode.reset();
        startNextStarter();
    }
}
