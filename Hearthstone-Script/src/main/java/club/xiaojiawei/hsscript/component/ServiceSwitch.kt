package club.xiaojiawei.hsscript.component

import club.xiaojiawei.controls.NotificationManager
import club.xiaojiawei.controls.Switch
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.utils.ConfigUtil
import club.xiaojiawei.hsscript.utils.runUI
import club.xiaojiawei.util.isTrue

/**
 * @author 肖嘉威
 * @date 2025/3/24 17:46
 */
class ServiceSwitch : Switch() {

    var config: ConfigEnum? = null
        set(value) {
            field = value
            value?.let {
                status = ConfigUtil.getBoolean(it)
            }
        }

    var notificationManager: NotificationManager<*>? = null

    init {
        statusProperty().addListener { _, _, newValue ->
            config?.let {
                ConfigUtil.putBoolean(it, newValue)
            }
            if (newValue) {
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

}