package club.xiaojiawei.hearthstone.enums;

import club.xiaojiawei.hearthstone.strategy.ModeStrategy;
import club.xiaojiawei.hearthstone.strategy.mode.*;

import java.util.function.Supplier;

/**
 * @author 肖嘉威
 * @date 2022/11/25 0:10
 */
public enum ModeEnum {

    STARTUP("STARTUP", "准备界面", null),
    LOGIN("LOGIN", "登录界面", LoginMode::new),
    HUB("HUB", "主界面", HubMode::new),
    TOURNAMENT("TOURNAMENT", "传统对战界面", TournamentMode::new),
    PACKOPENING("PACKOPENING", "开包界面", PackopeningMode::new),
    COLLECTIONMANAGER("COLLECTIONMANAGER", "我的收藏界面", CollectionmanagerMode::new),
    ADVENTURE("ADVENTURE", "冒险模式界面", AdventureMode::new),
    LETTUCE_MAP("LETTUCE_MAP", "佣兵战纪界面", LettuceMapMode::new),
    BACON("BACON", "酒馆战棋界面", BaconMode::new),
    GAMEPLAY("GAMEPLAY", "游戏界面", GameplayMode::new),
    GAME_MODE("GAME_MODE", "其他模式", GameMode::new),
    UNKNOWN("UNKNOWN", "未知模式", UnknownMode::new)
    ;
    private final String name;

    private final String comment;

    private final Supplier<ModeStrategy> modeStrategy;
    ModeEnum(String name, String comment, Supplier<ModeStrategy> modeStrategy) {
        this.name = name;
        this.comment = comment;
        this.modeStrategy = modeStrategy;
    }

    public String getName() {
        return name;
    }

    public String getComment() {
        return comment;
    }

    public Supplier<ModeStrategy> getModeStrategy() {
        return modeStrategy;
    }

}
