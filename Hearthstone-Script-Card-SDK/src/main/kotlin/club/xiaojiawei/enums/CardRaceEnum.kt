package club.xiaojiawei.enums

/**
 * 卡牌种族
 * @author 肖嘉威
 * @date 2023/9/19 13:17
 */
enum class CardRaceEnum {
    /**
     * 全部
     */
    ALL,

    /**
     * 图腾
     */
    TOTEM,

    /**
     * 野兽
     */
    PET,

    /**
     * 海盗
     */
    PIRATE,

    /**
     * 恶魔
     */
    DEMON,

    /**
     * 机械
     */
    MECHANICAL,

    /**
     * 亡灵
     */
    UNDEAD,

    /**
     * 龙
     */
    DRAGON,

    /**
     * 元素
     */
    ELEMENTAL,

    /**
     * 野猪人
     */
    QUILBOAR,

    /**
     * 娜迦
     */
    NAGA,

    /**
     * 未知
     */
    UNKNOWN,

    ;

    companion object {
        fun fromString(str: String?): CardRaceEnum {
            if (str == null || str.isBlank()) return UNKNOWN
            return try {
                valueOf(str.uppercase())
            }catch (_:Exception){
                UNKNOWN
            }
        }
    }
}
