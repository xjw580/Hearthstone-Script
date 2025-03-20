package club.xiaojiawei.hsscript.starter

import club.xiaojiawei.hsscript.dll.CSystemDll
import club.xiaojiawei.hsscript.status.Mode

/**
 * 关闭任务，状态重置
 * @author 肖嘉威
 * @date 2023/7/6 10:46
 */

class ClearStarter : AbstractStarter() {

    public override fun execStart() {
        Mode.reset()
        CSystemDll.setWakeUpTimer(0)
        startNextStarter()
    }

}
