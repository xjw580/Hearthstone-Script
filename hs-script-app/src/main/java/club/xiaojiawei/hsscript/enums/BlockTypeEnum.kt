package club.xiaojiawei.hsscript.enums

/**
 * @author 肖嘉威
 * @date 2022/11/30 17:56
 */
enum class BlockTypeEnum(val comment: String = "") {
    /**
     * 触发
     */
    TRIGGER("触发"),

    /**
     * 打出
     */
    PLAY("打出"),

    /**
     * 卡牌效果结算
     */
    POWER("卡牌效果结算"),

    /**
     * 攻击
     */
    ATTACK("攻击"),

    /**
     * 死亡
     */
    DEATHS("死亡"),

    /**
     * 疲劳
     */
    FATIGUE("疲劳"),

    /**
     * 行动
     */
    ACTION("行动"),

    /**
     * 继续
     */
    CONTINUOUS("继续"),

    /**
     * 游戏复位
     */
    GAME_RESET("游戏复位"),

    /**
     * 无效
     */
    INVALID("无效"),

    UNKNOWN("未知")
    ;

    companion object {
        fun fromString(blockTypeName: String?): BlockTypeEnum {
            if (blockTypeName == null || blockTypeName.isBlank()) return UNKNOWN
            return try {
                BlockTypeEnum.valueOf(blockTypeName)
            } catch (_: IllegalArgumentException) {
                UNKNOWN
            }
        }
    }
}
