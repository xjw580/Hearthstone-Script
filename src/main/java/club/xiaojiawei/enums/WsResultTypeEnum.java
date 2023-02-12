package club.xiaojiawei.enums;

/**
 * @author 肖嘉威
 * @date 2022/12/4 11:53
 */
public enum WsResultTypeEnum {

    DECK_TYPE(100, "套牌类型"),
    SCRIPT_LOG(200, "脚本日志"),
    ;
    private final int value;
    private final String comment;

    WsResultTypeEnum(int value, String comment) {
        this.value = value;
        this.comment = comment;
    }

    public int getValue() {
        return value;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public String toString() {
        return "WsResultTypeEnum{" +
                "value=" + value +
                ", comment='" + comment + '\'' +
                '}';
    }
}
