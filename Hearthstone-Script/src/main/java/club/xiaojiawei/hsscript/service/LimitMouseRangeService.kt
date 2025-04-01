package club.xiaojiawei.hsscript.service

import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.utils.ConfigUtil

/**
 * @author 肖嘉威
 * @date 2025/4/1 15:20
 */
object LimitMouseRangeService : Service<Boolean>() {


    override fun execStart(): Boolean {
        if (ConfigUtil.getBoolean(ConfigEnum.LIMIT_MOUSE_RANGE)) {

            return true
        }
        return false
    }

    override fun execStop(): Boolean {
        return true
    }


}