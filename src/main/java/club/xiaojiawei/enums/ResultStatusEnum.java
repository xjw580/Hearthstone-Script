package club.xiaojiawei.enums;

/**
 * @author 肖嘉威
 * @date 2022/12/4 14:33
 */
public enum ResultStatusEnum {

    SUCCESS(200, "成功"),
    FAIL(400, "失败"),
    ERROR(500, "错误")
    ;
    private final int code;
    private final String comment;

    ResultStatusEnum(int code, String comment) {
        this.code = code;
        this.comment = comment;
    }

    public int getCode() {
        return code;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public String toString() {
        return "ResultStatusEnum{" +
                "code=" + code +
                ", comment='" + comment + '\'' +
                '}';
    }
}
