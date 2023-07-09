package club.xiaojiawei.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author 肖嘉威
 * @date 2022/12/4 14:33
 */
@Getter
@ToString
@AllArgsConstructor
public enum ResultStatusEnum {

    SUCCESS(200, "成功"),
    FAIL(400, "失败"),
    ERROR(500, "错误")
    ;
    private final int code;
    private final String comment;
}
