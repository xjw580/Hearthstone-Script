package club.xiaojiawei.enums

import club.xiaojiawei.interfaces.ModeStrategy

/**
 * @author 肖嘉威
 * @date 2022/11/25 0:10
 */
enum class ModeEnum(val comment: String, modeStrategyClassName: String) {

    STARTUP("准备界面", "StartupModeStrategy"),
    LOGIN("登录界面", "LoginModeStrategy"),
    HUB("主界面", "HubModeStrategy"),
    TOURNAMENT("传统对战界面", "TournamentModeStrategy"),
    PACKOPENING("开包界面", "PackopeningModeStrategy"),
    COLLECTIONMANAGER("我的收藏界面", "CollectionmanagerModeStrategy"),
    ADVENTURE("冒险模式界面", "AdventureModeStrategy"),
    LETTUCE_MAP("佣兵战纪界面", "LettuceMapModeStrategy"),
    BACON("酒馆战棋界面", "BaconModeStrategy"),
    GAMEPLAY("游戏界面", "GameplayModeStrategy"),
    GAME_MODE("其他模式", "GameModeStrategy"),
    FATAL_ERROR("致命错误", "FatalErrorModeStrategy"),
    DRAFT("竞技场界面", "DraftModeStrategy"),
    PVP_DUNGEON_RUN("对决界面", "PvpDungeonRunModeStrategy"),
    FRIENDLY("友谊赛界面", "FriendlyModeStrategy"),
    TAVERN_BRAWL("乱斗界面", "TavernBrawlModeStrategy");

    val modeStrategyClassName: String = "club.xiaojiawei.strategy.mode.$modeStrategyClassName"

    var modeStrategy: ModeStrategy<Any>? = null

}
