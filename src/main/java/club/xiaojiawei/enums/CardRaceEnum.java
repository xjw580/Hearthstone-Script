package club.xiaojiawei.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 卡牌种族
 * @author 肖嘉威
 * @date 2023/9/19 13:17
 */
@Getter
@ToString
@AllArgsConstructor
public enum CardRaceEnum {
    ALL("全部"),
    TOTEM("图腾"),
    PET("野兽"),
    PIRATE("海盗"),
    DEMON("恶魔"),
    MECHANICAL("机械"),
    UNDEAD("亡灵"),
    DRAGON("龙"),
    ELEMENTAL("元素"),
    QUILBOAR("野猪人"),
    NAGA("娜迦"),
    UNKNOWN("未知"),
    ;
    private final String comment;
}
