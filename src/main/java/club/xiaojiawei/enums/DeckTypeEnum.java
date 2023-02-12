package club.xiaojiawei.enums;

/**
 * @author 肖嘉威
 * @date 2022/11/29 17:19
 */
public enum DeckTypeEnum {

    STANDARD("STANDARD", "标准"),
    WILD("WILD", "狂野"),
    CLASSIC("CLASSIC", "经典"),
    ROOKIE("ROOKIE", "新手"),
    GENERAL("GENERAL", "通用")
    ;
    private final String value;
    private final String comment;

    DeckTypeEnum(String value, String comment) {
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
        return "GameTypeEnum{" +
                "comment='" + comment + '\'' +
                '}';
    }
}
