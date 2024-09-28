package club.xiaojiawei.starter

import club.xiaojiawei.status.Mode
import club.xiaojiawei.utils.SystemUtil
import lombok.extern.slf4j.Slf4j
import org.springframework.stereotype.Component

/**
 * 关闭任务，状态重置
 * @author 肖嘉威
 * @date 2023/7/6 10:46
 */

object ClearStarter : AbstractStarter() {

    public override fun execStart() {
        SystemUtil.closeAll()
        Mode.reset()
        startNextStarter()
    }

}
