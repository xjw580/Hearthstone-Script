package club.xiaojiawei.hsscript.initializer

import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.utils.goByLock

/**
 * @author 肖嘉威
 * @date 2025/4/1 17:20
 */
class ServiceInitializer : AbstractInitializer() {
    override fun exec() {
        goByLock(ServiceInitializer::class.java) {
            for (configEnum in ConfigEnum.entries) {
                if (configEnum.isEnable) {
                    configEnum.service?.intelligentStartStop()
                }
            }
        }

    }

    fun stop() {
        goByLock(ServiceInitializer::class.java) {
            for (configEnum in ConfigEnum.entries) {
                if (configEnum.isEnable) {
                    configEnum.service?.stop()
                }
            }
        }
    }
}
