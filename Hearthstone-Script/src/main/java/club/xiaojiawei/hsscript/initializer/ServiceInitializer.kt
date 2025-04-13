package club.xiaojiawei.hsscript.initializer

import club.xiaojiawei.hsscript.enums.ConfigEnum

/**
 * @author 肖嘉威
 * @date 2025/4/1 17:20
 */
class ServiceInitializer : AbstractInitializer() {
    override fun exec() {
        for (configEnum in ConfigEnum.values()) {
            if (configEnum.isEnable) {
                configEnum.service?.intelligentStartStop()
            }
        }
    }

    fun stop() {
        for (configEnum in ConfigEnum.values()) {
            if (configEnum.isEnable) {
                configEnum.service?.stop()
            }
        }
    }
}
