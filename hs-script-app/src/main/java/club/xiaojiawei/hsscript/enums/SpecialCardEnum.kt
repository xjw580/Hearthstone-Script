package club.xiaojiawei.hsscript.enums

/**
 * 开局触发特殊效果的卡牌
 * @author 肖嘉威
 * @date 2022/11/29 22:04
 */
enum class SpecialCardEnum(val cardId: String, val comment: String) {

    PRINCE_RENATHAL("REV_018", "雷纳索尔王子"),
    C_THUN_THE_SHATTERED("DMF_254", "克苏恩，破碎之劫"),
    BAKU_THE_MOONEATER("GIL_826", "噬月者巴库"),
    GENN_GREYMANE("GIL_692", "吉恩·格雷迈恩"),
    ;

}
