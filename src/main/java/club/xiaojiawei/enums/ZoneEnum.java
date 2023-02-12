package club.xiaojiawei.enums;

/**
 * @author 肖嘉威
 * @date 2022/11/28 23:16
 */
public enum ZoneEnum {

    DECK("DECK", "牌库区"),
    HAND("HAND", "手牌区"),
    PLAY("PLAY", "战场"),
    SETASIDE("SETASIDE", "除外区"),
    SECRET("SECRET", "奥秘区"),
    GRAVEYARD("GRAVEYARD", "墓地"),
    REMOVEDFROMGAME("REMOVEDFROMGAME", "从游戏中移除")
    ;
    private final String value;
    private final String comment;

    ZoneEnum(String value, String comment) {
        this.value = value;
        this.comment = comment;
    }

    public String getValue() {
        return value;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public String toString() {
        return "ZoneEnum{" +
                "value='" + value + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}
