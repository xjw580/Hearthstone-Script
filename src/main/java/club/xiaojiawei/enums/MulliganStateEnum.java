package club.xiaojiawei.enums;

import lombok.Getter;

/**
 * @author 肖嘉威
 * @date 2023/9/18 20:43
 */
@Getter
public enum MulliganStateEnum {
    INPUT("INPUT", "等待用户输入"),
    DEALING("DEALING", "处理中"),
    WAITING("WAITING", "等待中"),
    DONE("DONE", "结束"),
    ;
    private final String value;
    private final String comment;

    MulliganStateEnum(String value, String comment) {
        this.value = value;
        this.comment = comment;
    }
}
