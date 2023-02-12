package club.xiaojiawei.enums;

/**
 * @author 肖嘉威
 * @date 2022/11/30 17:56
 */
public enum BlockTypeEnum {

    TRIGGER("TRIGGER", "触发"),
    PLAY("PLAY", "打出"),
    POWER("POWER", "卡牌效果结算"),
    ATTACK("ATTACK", "攻击"),
    DEATHS("DEATHS", "死亡"),
    FATIGUE("FATIGUE", "疲劳")
    ;
    private final String value;
    private final String comment;

    public String getValue() {
        return value;
    }

    public String getComment() {
        return comment;
    }

    BlockTypeEnum(String value, String comment) {
        this.value = value;
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "BlockTypeEnum{" +
                "value='" + value + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}
