package club.xiaojiawei.hsscript.component

import club.xiaojiawei.hsscript.utils.ConfigUtil
import club.xiaojiawei.hsscript.utils.runUI
import club.xiaojiawei.util.isTrue

/**
 * @author 肖嘉威
 * @date 2025/3/24 17:46
 */
class ServiceSwitch : ConfigSwitch() {

    override fun statusChangeCallback(status: Boolean) {
        super.statusChangeCallback(status)
        if (status) {
            config?.service?.let { service ->
                service.start().isTrue {
                    notificationManager?.let { nm ->
                        runUI {
                            nm.showInfo("${service.name()}已启动", 2)
                        }
                    }
                }
            }
        } else {
            config?.service?.let { service ->
                service.stop().isTrue {
                    notificationManager?.let { nm ->
                        runUI {
                            nm.showInfo("${service.name()}已停止", 2)
                        }
                    }
                }
            }
        }
    }

}