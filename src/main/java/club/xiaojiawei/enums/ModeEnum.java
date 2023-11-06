package club.xiaojiawei.enums;

import club.xiaojiawei.strategy.AbstractModeStrategy;
import club.xiaojiawei.strategy.mode.*;
import lombok.Getter;
import lombok.ToString;

/**
 * @author 肖嘉威
 * @date 2022/11/25 0:10
 */
@Getter
@ToString
public enum ModeEnum {

    STARTUP("STARTUP", "准备界面", StartupModeStrategy.class),
    LOGIN("LOGIN", "登录界面", LoginModeStrategy.class),
    HUB("HUB", "主界面", HubModeStrategy.class),
    TOURNAMENT("TOURNAMENT", "传统对战界面", TournamentModeStrategy.class),
    PACKOPENING("PACKOPENING", "开包界面", PackopeningModeStrategy.class),
    COLLECTIONMANAGER("COLLECTIONMANAGER", "我的收藏界面", CollectionmanagerModeStrategy.class),
    ADVENTURE("ADVENTURE", "冒险模式界面", AdventureModeStrategy.class),
    LETTUCE_MAP("LETTUCE_MAP", "佣兵战纪界面", LettuceMapModeStrategy.class),
    BACON("BACON", "酒馆战棋界面", BaconModeStrategy.class),
    GAMEPLAY("GAMEPLAY", "游戏界面", GameplayModeStrategy.class),
    GAME_MODE("GAME_MODE", "其他模式", GameModeStrategy.class),
    FATAL_ERROR("FATAL_ERROR", "致命错误", FatalErrorModeStrategy.class),
    DRAFT("DRAFT", "竞技场界面", DraftModeStrategy.class),
    PVP_DUNGEON_RUN("PVP_DUNGEON_RUN", "对决界面", PvpDungeonRunModeStrategy.class),
    FRIENDLY("FRIENDLY", "友谊赛界面", FriendlyModeStrategy.class),
    TAVERN_BRAWL("TAVERN_BRAWL", "乱斗界面", TavernBrawlModeStrategy.class)
    ;
    private final String value;
    private final String comment;
    private final Class<? extends AbstractModeStrategy<Object>> modeStrategyClass;
    private AbstractModeStrategy<Object> abstractModeStrategy;

    ModeEnum(String value, String comment, Class<? extends AbstractModeStrategy<Object>> modeStrategyClass) {
        this.value = value;
        this.comment = comment;
        this.modeStrategyClass = modeStrategyClass;
    }

    public void setAbstractModeStrategy(AbstractModeStrategy<Object> abstractModeStrategy) {
        this.abstractModeStrategy = abstractModeStrategy;
    }
}
