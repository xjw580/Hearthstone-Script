package club.xiaojiawei.enums

/**
 * @author 肖嘉威
 * @date 2022/11/28 23:16
 */
enum class ZoneEnum(val comment: String) {

    DECK("牌库区"),
    HAND("手牌区"),
    PLAY("战场"),
    SETASIDE("除外区"),
    SECRET("奥秘区"),
    GRAVEYARD("墓地"),
    REMOVEDFROMGAME("移除区");

}
