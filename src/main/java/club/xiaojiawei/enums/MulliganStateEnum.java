package club.xiaojiawei.enums;

import lombok.Getter;

/**
 * @author 肖嘉威
 * @date 2023/9/18 20:43
 */
@Getter
public enum MulliganStateEnum {
    INPUT("等待用户输入"),
    DEALING("处理中"),
    WAITING("等待中"),
    DONE("结束"),
    ;
    private final String comment;

    MulliganStateEnum(String comment) {
        this.comment = comment;
    }
}
