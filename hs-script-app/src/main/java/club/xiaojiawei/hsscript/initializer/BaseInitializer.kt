package club.xiaojiawei.hsscript.initializer

import club.xiaojiawei.data.BaseData
import club.xiaojiawei.hsscriptbase.enums.ModeEnum
import club.xiaojiawei.hsscriptbase.enums.WarPhaseEnum
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.utils.ConfigUtil
import club.xiaojiawei.hsscriptbase.interfaces.ModeStrategy
import club.xiaojiawei.hsscriptbase.interfaces.PhaseStrategy

/**
 * @author 肖嘉威
 * @date 2023/7/4 11:33
 */
class BaseInitializer : AbstractInitializer() {

    private fun toCamelCase(snakeCase: String): String {
        return snakeCase.split("_")
            .joinToString("") { it.lowercase().replaceFirstChar { char -> char.uppercase() } }
    }

    override fun exec() {
        BaseData.enableChangeWeight = ConfigUtil.getBoolean(ConfigEnum.ENABLE_CHANGE_WEIGHT)
        ModeEnum.entries.forEach {
            it.modeStrategy =
                Class.forName("club.xiaojiawei.hsscript.strategy.mode." + toCamelCase(it.name) + "ModeStrategy").kotlin.objectInstance as ModeStrategy<*>?
        }
        WarPhaseEnum.entries.forEach {
            it.phaseStrategy =
                Class.forName("club.xiaojiawei.hsscript.strategy.phase." + toCamelCase(it.name) + "PhaseStrategy").kotlin.objectInstance as PhaseStrategy?
        }
    }

}
