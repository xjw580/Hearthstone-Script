package club.xiaojiawei.hsscript.component

import club.xiaojiawei.controls.NotificationManager
import club.xiaojiawei.controls.Switch
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.service.Service
import club.xiaojiawei.hsscript.utils.ConfigUtil
import club.xiaojiawei.hsscript.utils.runUI

/**
 * @author 肖嘉威
 * @date 2025/3/24 17:46
 */
open class ConfigSwitch : Switch() {

    var config: ConfigEnum? = null
        set(value) {
            value?.let {
                status = ConfigUtil.getBoolean(it)
            }
            field = value
        }

    var notificationManager: NotificationManager<Any>? = null

    init {
        statusProperty().addListener { _, oldValue, newValue ->
            config?.let {
                statusChangeCallback(oldValue, newValue)
            }
        }
    }

    protected open fun statusChangeCallback(oldValue: Boolean, newValue: Boolean) {
        config?.let {
            ConfigUtil.putBoolean(it, newValue)
        }
        var res = true
        if (newValue) {
            config?.service?.let { service ->
                res = service.start()
                (service as Service<Boolean>).valueChanged(oldValue, newValue)
            }
        } else {
            config?.service?.let { service ->
                res = service.stop()
                (service as Service<Boolean>).valueChanged(oldValue, newValue)

            }
        }
        if (res) {
            notificationManager?.let { nm ->
                runUI {
                    nm.showInfo("设置成功", 2)
                }
            }
        } else {
            config?.let {
                ConfigUtil.putBoolean(it, oldValue)
            }
            notificationManager?.let { nm ->
                runUI {
                    nm.showInfo("设置失败", 2)
                }
            }
        }
    }

}