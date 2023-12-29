package club.xiaojiawei.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author 肖嘉威
 * @date 2022/11/27 19:17
 */
@Getter
@ToString
@AllArgsConstructor
public enum CardTypeEnum {
    MINION("随从"),
    LOCATION("地标"),
    SPELL("法术"),
    HERO("英雄"),
    WEAPON("武器"),
    HERO_POWER("技能"),
    ENCHANTMENT("效果"),
    GAME("游戏"),
    PLAYER("玩家"),
    INVALID("无效"),
    UNKNOWN("未知"),
    ;
    private final String comment;

}
