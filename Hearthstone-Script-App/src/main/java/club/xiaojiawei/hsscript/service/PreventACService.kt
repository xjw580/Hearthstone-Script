package club.xiaojiawei.hsscript.service

import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.utils.ConfigExUtil
import club.xiaojiawei.hsscript.utils.ConfigUtil

/**
 * @author 肖嘉威
 * @date 2025/4/2 23:12
 */
object PreventACService : Service<Boolean>() {
    override fun execStart(): Boolean = true

    override fun execStop(): Boolean = true

    override fun execValueChanged(
        oldValue: Boolean,
        newValue: Boolean,
    ) {
        ConfigExUtil.storePreventAntiCheat(newValue)
    }

    override fun execIntelligentStartStop(value: Boolean?): Boolean = (value ?: ConfigUtil.getBoolean(ConfigEnum.PREVENT_AC))
}
