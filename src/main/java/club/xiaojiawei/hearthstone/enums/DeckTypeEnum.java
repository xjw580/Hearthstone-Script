package club.xiaojiawei.hearthstone.enums;

/**
 * @author 肖嘉威
 * @date 2022/11/29 17:19
 */
public enum DeckTypeEnum {

    STANDARD("标准"),
    WILD("狂野"),
    CLASSIC("经典"),
    ROOKIE("新手")
    ;
    private final String comment;

    DeckTypeEnum(String comment) {
        this.comment = comment;
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
