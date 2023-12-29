package club.xiaojiawei.enums;

import club.xiaojiawei.strategy.AbstractModeStrategy;
import club.xiaojiawei.strategy.mode.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 肖嘉威
 * @date 2022/11/25 0:10
 */
@Getter
@ToString
public enum ModeEnum {

    STARTUP("准备界面", StartupModeStrategy.class),
    LOGIN("登录界面", LoginModeStrategy.class),
    HUB("主界面", HubModeStrategy.class),
    TOURNAMENT("传统对战界面", TournamentModeStrategy.class),
    PACKOPENING("开包界面", PackopeningModeStrategy.class),
    COLLECTIONMANAGER("我的收藏界面", CollectionmanagerModeStrategy.class),
    ADVENTURE("冒险模式界面", AdventureModeStrategy.class),
    LETTUCE_MAP("佣兵战纪界面", LettuceMapModeStrategy.class),
    BACON("酒馆战棋界面", BaconModeStrategy.class),
    GAMEPLAY("游戏界面", GameplayModeStrategy.class),
    GAME_MODE("其他模式", GameModeStrategy.class),
    FATAL_ERROR("致命错误", FatalErrorModeStrategy.class),
    DRAFT("竞技场界面", DraftModeStrategy.class),
    PVP_DUNGEON_RUN("对决界面", PvpDungeonRunModeStrategy.class),
    FRIENDLY("友谊赛界面", FriendlyModeStrategy.class),
    TAVERN_BRAWL("乱斗界面", TavernBrawlModeStrategy.class)
    ;
    private final String comment;
    private final Class<? extends AbstractModeStrategy<Object>> modeStrategyClass;
    @Setter
    private AbstractModeStrategy<Object> abstractModeStrategy;

    ModeEnum(String comment, Class<? extends AbstractModeStrategy<Object>> modeStrategyClass) {
        this.comment = comment;
        this.modeStrategyClass = modeStrategyClass;
    }

}
