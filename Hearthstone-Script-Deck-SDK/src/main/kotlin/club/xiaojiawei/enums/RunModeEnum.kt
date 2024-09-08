package club.xiaojiawei.enums

/**
 * @author 肖嘉威
 * @date 2023/7/6 20:20
 */
enum class RunModeEnum(val comment: String, val modeEnum: ModeEnum, var isEnable: Boolean) {

    STANDARD("标准模式", ModeEnum.TOURNAMENT, true),
    WILD("狂野模式", ModeEnum.TOURNAMENT, true),
    CASUAL("休闲模式", ModeEnum.TOURNAMENT, true),
    CLASSIC("经典模式", ModeEnum.TOURNAMENT, false),
    TWIST("幻变模式", ModeEnum.TOURNAMENT, false),
    BACON("酒馆战棋", ModeEnum.BACON, false),
    ;

}
