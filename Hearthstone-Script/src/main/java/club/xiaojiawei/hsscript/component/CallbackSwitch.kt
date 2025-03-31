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
class CallbackSwitch : ConfigSwitch() {

    override fun statusChangeCallback(status: Boolean) {
        super.statusChangeCallback(status)

    }
}