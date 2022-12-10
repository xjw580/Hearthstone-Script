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
    LOGIN("LOGIN", "登录界面", LoginModeStrategy::new),
    HUB("HUB", "主界面", HubModeStrategy::new),
    TOURNAMENT("TOURNAMENT", "传统对战界面", TournamentModeStrategy::new),
    PACKOPENING("PACKOPENING", "开包界面", PackopeningModeStrategy::new),
    COLLECTIONMANAGER("COLLECTIONMANAGER", "我的收藏界面", CollectionmanagerModeStrategy::new),
    ADVENTURE("ADVENTURE", "冒险模式界面", AdventureModeStrategy::new),
    LETTUCE_MAP("LETTUCE_MAP", "佣兵战纪界面", LettuceMapModeStrategy::new),
    BACON("BACON", "酒馆战棋界面", BaconModeStrategy::new),
    GAMEPLAY("GAMEPLAY", "游戏界面", GameplayModeStrategy::new),
    GAME_MODE("GAME_MODE", "其他模式", GameModeStrategy::new),
    UNKNOWN("UNKNOWN", "未知模式", UnknownModeStrategy::new)
    ;
    private final String value;

    private final String comment;

    private final Supplier<ModeStrategy> modeStrategy;
    ModeEnum(String value, String comment, Supplier<ModeStrategy> modeStrategy) {
        this.value = value;
        this.comment = comment;
        this.modeStrategy = modeStrategy;
    }

    public String getValue() {
        return value;
    }

    public String getComment() {
        return comment;
    }

    public Supplier<ModeStrategy> getModeStrategy() {
        return modeStrategy;
    }

    @Override
    public String toString() {
        return "ModeEnum{" +
                "value='" + value + '\'' +
                ", comment='" + comment + '\'' +
                ", modeStrategy=" + modeStrategy +
                '}';
    }
}
