package club.xiaojiawei.hsscript.component

import club.xiaojiawei.controls.NotificationManager
import club.xiaojiawei.controls.Switch
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.utils.ConfigUtil
import club.xiaojiawei.util.isTrue

/**
 * @author 肖嘉威
 * @date 2025/3/24 17:46
 */
open class ConfigSwitch : Switch() {

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
            statusChangeCallback(newValue)
        }
    }

    protected open fun statusChangeCallback(status: Boolean) {
        config?.let {
            var res = true
            it.valueChangeCallback?.let { callback ->
                res = callback(status.toString())
            }
            res.isTrue {
                ConfigUtil.putBoolean(it, status)
            }
        }

    }

}