package club.xiaojiawei.enums

import club.xiaojiawei.config.log

/**
 * @author 肖嘉威
 * @date 2023/7/6 20:20
 */
enum class RunModeEnum(
    val comment: String,
    val modeEnum: ModeEnum,
    var isEnable: Boolean,
) {
    STANDARD("标准模式", ModeEnum.TOURNAMENT, true),
    WILD("狂野模式", ModeEnum.TOURNAMENT, true),
    CASUAL("休闲模式", ModeEnum.TOURNAMENT, true),
    PRACTICE("练习模式", ModeEnum.ADVENTURE, true),
    CLASSIC("经典模式", ModeEnum.TOURNAMENT, false),
    TWIST("幻变模式", ModeEnum.TOURNAMENT, false),
    BACON("酒馆战棋", ModeEnum.BACON, false),
    ;

    companion object {
        fun fromString(string: String): RunModeEnum? =
            try {
                valueOf(string)
            } catch (_: Exception) {
                log.warn { "未适配$string" }
                null
            }
    }
}
