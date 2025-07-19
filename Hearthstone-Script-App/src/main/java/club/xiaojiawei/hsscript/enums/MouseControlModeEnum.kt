package club.xiaojiawei.hsscript.enums

/**
 * 鼠标控制模式
 * @author 肖嘉威
 * @date 2025/3/4 20:09
 */
enum class MouseControlModeEnum(val code: Int, val comment: String) {

    MESSAGE(0, "不真实控制鼠标"),

    EVENT(1, "真实控制鼠标"),

    DRIVE(2, "通过驱动真实控制鼠标"),

    ;


    companion object {
        fun fromString(tagEnumName: String?): MouseControlModeEnum {
            if (tagEnumName.isNullOrBlank()) return MESSAGE
            return try {
                MouseControlModeEnum.valueOf(tagEnumName.uppercase())
            } catch (_: IllegalArgumentException) {
                MESSAGE
            }
        }
    }

}