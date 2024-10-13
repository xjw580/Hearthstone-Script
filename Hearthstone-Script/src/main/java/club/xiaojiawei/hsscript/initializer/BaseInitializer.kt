package club.xiaojiawei.hsscript.initializer

import club.xiaojiawei.enums.ModeEnum
import club.xiaojiawei.enums.WarPhaseEnum
import club.xiaojiawei.hsscript.utils.ConfigUtil
import club.xiaojiawei.interfaces.ModeStrategy
import club.xiaojiawei.interfaces.PhaseStrategy

/**
 * 开启游戏日志输出
 * @author 肖嘉威
 * @date 2023/7/4 11:33
 */
object BaseInitializer : AbstractInitializer() {

    private fun toCamelCase(snakeCase: String): String {
        return snakeCase.split("_")
            .joinToString("") { it.lowercase().replaceFirstChar { char -> char.uppercase() } }
    }

    override fun exec() {
        ModeEnum.values().forEach {
            it.modeStrategy =
                Class.forName("club.xiaojiawei.hsscript.strategy.mode." + toCamelCase(it.name) + "ModeStrategy").kotlin.objectInstance as ModeStrategy<*>?
        }
        WarPhaseEnum.values().forEach {
            it.phaseStrategy =
                Class.forName("club.xiaojiawei.hsscript.strategy.phase." + toCamelCase(it.name) + "PhaseStrategy").kotlin.objectInstance as PhaseStrategy?
        }
        ConfigUtil
    }

}
