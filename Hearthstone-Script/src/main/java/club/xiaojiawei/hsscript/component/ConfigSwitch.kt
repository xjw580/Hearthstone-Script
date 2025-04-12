package club.xiaojiawei.hsscript.component

import club.xiaojiawei.controls.NotificationManager
import club.xiaojiawei.controls.Switch
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.service.Service
import club.xiaojiawei.hsscript.utils.ConfigUtil
import club.xiaojiawei.hsscript.utils.runUI
import club.xiaojiawei.util.isFalse

/**
 * @author 肖嘉威
 * @date 2025/3/24 17:46
 */
open class ConfigSwitch : Switch() {
    var config: ConfigEnum? = null
        set(value) {
            isDisable = value?.isEnable != true
            value?.let {
                status = ConfigUtil.getBoolean(it)
            }
            field = value
        }

    var notificationManager: NotificationManager<Any>? = null

    private var interceptChange = false

    init {
        statusProperty().addListener { _, oldValue, newValue ->
            config?.let {
                interceptChange.isFalse {
                    statusChangeCallback(oldValue, newValue)
                }
            }
        }
    }

    protected open fun statusChangeCallback(
        oldValue: Boolean,
        newValue: Boolean,
    ) {
        config?.let {
            ConfigUtil.putBoolean(it, newValue)
        }
        var res = true
        config?.service?.let { service ->
            res = (service as Service<Boolean>).intelligentStartStop(newValue)
            service.valueChanged(oldValue, newValue)
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
                interceptChange = true
                status = oldValue
                interceptChange = false
            }
            notificationManager?.let { nm ->
                runUI {
                    nm.showInfo("设置失败", 2)
                }
            }
        }
    }
}
