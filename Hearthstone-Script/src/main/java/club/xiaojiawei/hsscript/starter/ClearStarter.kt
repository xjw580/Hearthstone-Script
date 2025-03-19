package club.xiaojiawei.hsscript.starter

import club.xiaojiawei.hsscript.dll.SystemDll
import club.xiaojiawei.hsscript.status.Mode

/**
 * 关闭任务，状态重置
 * @author 肖嘉威
 * @date 2023/7/6 10:46
 */

class ClearStarter : AbstractStarter() {

    public override fun execStart() {
        Mode.reset()
        SystemDll.setWakeUpTimer(0)
        startNextStarter()
    }

}
