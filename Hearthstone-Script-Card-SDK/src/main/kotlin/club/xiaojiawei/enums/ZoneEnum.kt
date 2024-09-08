package club.xiaojiawei.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author 肖嘉威
 * @date 2022/11/28 23:16
 */
@Getter
@ToString
@AllArgsConstructor
public enum ZoneEnum {

    DECK("牌库区"),
    HAND("手牌区"),
    PLAY("战场"),
    SETASIDE("除外区"),
    SECRET("奥秘区"),
    GRAVEYARD("墓地"),
    REMOVEDFROMGAME("移除区")
    ;
    private final String comment;
}
