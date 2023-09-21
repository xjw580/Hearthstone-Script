package club.xiaojiawei.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author 肖嘉威
 * @date 2023/9/19 13:17
 * @msg 卡牌种族
 */
@Getter
@ToString
@AllArgsConstructor
public enum CardRaceEnum {
    ALL("ALL", "全部"),
    TOTEM("TOTEM", "图腾"),
    PET("PET", "野兽"),
    PIRATE("PIRATE", "海盗"),
    DEMON("DEMON", "恶魔"),
    MECHANICAL("MECHANICAL", "机械"),
    UNDEAD("UNDEAD", "亡灵"),
    DRAGON("DRAGON", "龙"),
    ELEMENTAL("ELEMENTAL", "元素"),
    QUILBOAR("QUILBOAR", "野猪人"),
    NAGA("NAGA", "娜迦"),
    UNKNOWN("UNKNOWN", "未知"),
    ;
    private final String value;
    private final String comment;
}
