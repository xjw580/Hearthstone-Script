package club.xiaojiawei.enums;

/**
 * @author 肖嘉威
 * @date 2022/11/27 19:17
 */
public enum CardTypeEnum {
    MINION("MINION", "随从"),
    SPELL("SPELL", "法术"),
    HERO("HERO", "英雄"),
    WEAPON("WEAPON", "武器"),
    HERO_POWER("HERO_POWER", "技能")
    ;
    private final String value;
    private final String comment;

    CardTypeEnum(String value, String comment) {
        this.value = value;
        this.comment = comment;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "CardTypeEnum{" +
                "value='" + value + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }

    public String getComment() {

        return comment;
    }
}
