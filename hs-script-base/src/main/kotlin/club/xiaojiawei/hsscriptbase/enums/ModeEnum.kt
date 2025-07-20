package club.xiaojiawei.hsscriptbase.enums

import club.xiaojiawei.hsscriptbase.config.log
import club.xiaojiawei.hsscriptbase.interfaces.ModeStrategy

/**
 * @author 肖嘉威
 * @date 2022/11/25 0:10
 */
enum class ModeEnum(val comment: String) {

    STARTUP("准备界面"),
    LOGIN("登录界面"),
    HUB("主界面"),
    TOURNAMENT("传统对战界面"),
    PACKOPENING("开包界面"),
    COLLECTIONMANAGER("我的收藏界面"),
    ADVENTURE("冒险模式界面"),
    LETTUCE_MAP("佣兵战纪界面"),
    BACON("酒馆战棋界面"),
    GAMEPLAY("游戏界面"),
    GAME_MODE("其他模式"),
    FATAL_ERROR("致命错误"),
    DRAFT("竞技场界面"),
    PVP_DUNGEON_RUN("对决界面"),
    FRIENDLY("友谊赛界面"),
    TAVERN_BRAWL("乱斗界面")
    ;

    var modeStrategy: ModeStrategy<*>? = null

    companion object {
        fun fromString(string: String): ModeEnum? {
            return try {
                valueOf(string.trim().uppercase())
            } catch (_: Exception) {
                log.warn { "未适配${string}" }
                null
            }
        }
    }

}
