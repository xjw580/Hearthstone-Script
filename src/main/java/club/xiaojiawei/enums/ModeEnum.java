package club.xiaojiawei.enums;

import club.xiaojiawei.strategy.AbstractModeStrategy;
import club.xiaojiawei.strategy.mode.*;

import java.util.function.Supplier;

/**
 * @author 肖嘉威
 * @date 2022/11/25 0:10
 */
public enum ModeEnum {

    STARTUP("STARTUP", "准备界面", null),
    LOGIN("LOGIN", "登录界面", LoginAbstractModeStrategy::new),
    HUB("HUB", "主界面", HubAbstractModeStrategy::new),
    TOURNAMENT("TOURNAMENT", "传统对战界面", TournamentAbstractModeStrategy::new),
    PACKOPENING("PACKOPENING", "开包界面", PackopeningAbstractModeStrategy::new),
    COLLECTIONMANAGER("COLLECTIONMANAGER", "我的收藏界面", CollectionmanagerAbstractModeStrategy::new),
    ADVENTURE("ADVENTURE", "冒险模式界面", AdventureAbstractModeStrategy::new),
    LETTUCE_MAP("LETTUCE_MAP", "佣兵战纪界面", LettuceMapAbstractModeStrategy::new),
    BACON("BACON", "酒馆战棋界面", BaconAbstractModeStrategy::new),
    GAMEPLAY("GAMEPLAY", "游戏界面", GameplayAbstractModeStrategy::new),
    GAME_MODE("GAME_MODE", "其他模式", GameAbstractModeStrategy::new),
    FATAL_ERROR("FATAL_ERROR", "致命错误", FatalErrorAbstractModeStrategy::new),
    UNKNOWN("UNKNOWN", "未知模式", UnknownAbstractModeStrategy::new)
    ;
    private final String value;

    private final String comment;

    private final Supplier<AbstractModeStrategy> modeStrategy;
    ModeEnum(String value, String comment, Supplier<AbstractModeStrategy> modeStrategy) {
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

    public Supplier<AbstractModeStrategy> getModeStrategy() {
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
