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
    MINION("MINION", "随从"),
    SPELL("SPELL", "法术"),
    HERO("HERO", "英雄"),
    WEAPON("WEAPON", "武器"),
    HERO_POWER("HERO_POWER", "技能")
    ;
    private final String value;
    private final String comment;

}
