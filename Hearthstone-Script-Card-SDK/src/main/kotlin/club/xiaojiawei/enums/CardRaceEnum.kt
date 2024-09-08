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
    /**
     * 全部
     */
    ALL,
    /**
     * 图腾
     */
    TOTEM,
    /**
     * 野兽
     */
    PET,
    /**
     * 海盗
     */
    PIRATE,
    /**
     * 恶魔
     */
    DEMON,
    /**
     * 机械
     */
    MECHANICAL,
    /**
     * 亡灵
     */
    UNDEAD,
    /**
     * 龙
     */
    DRAGON,
    /**
     * 元素
     */
    ELEMENTAL,
    /**
     * 野猪人
     */
    QUILBOAR,
    /**
     * 娜迦
     */
    NAGA,
    /**
     * 未知
     */
    UNKNOWN,
    ;
}
