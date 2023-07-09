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

    STARTUP("STARTUP", "准备界面", StartupAbstractModeStrategy.class),
    LOGIN("LOGIN", "登录界面", LoginAbstractModeStrategy.class),
    HUB("HUB", "主界面", HubAbstractModeStrategy.class),
    TOURNAMENT("TOURNAMENT", "传统对战界面", TournamentAbstractModeStrategy.class),
    PACKOPENING("PACKOPENING", "开包界面", PackopeningAbstractModeStrategy.class),
    COLLECTIONMANAGER("COLLECTIONMANAGER", "我的收藏界面", CollectionmanagerAbstractModeStrategy.class),
    ADVENTURE("ADVENTURE", "冒险模式界面", AdventureAbstractModeStrategy.class),
    LETTUCE_MAP("LETTUCE_MAP", "佣兵战纪界面", LettuceMapAbstractModeStrategy.class),
    BACON("BACON", "酒馆战棋界面", BaconAbstractModeStrategy.class),
    GAMEPLAY("GAMEPLAY", "游戏界面", GameplayAbstractModeStrategy.class),
    GAME_MODE("GAME_MODE", "其他模式", GameAbstractModeStrategy.class),
    FATAL_ERROR("FATAL_ERROR", "致命错误", FatalErrorAbstractModeStrategy.class),
    DRAFT("DRAFT", "竞技场界面", DraftAbstractModeStrategy.class),
    PVP_DUNGEON_RUN("PVP_DUNGEON_RUN", "对决界面", PvpDungeonRunAbstractModeStrategy.class),
    TAVERN_BRAWL("TAVERN_BRAWL", "乱斗界面", TavernBrawlAbstractModeStrategy.class)
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
