package club.xiaojiawei.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author 肖嘉威
 * @date 2022/12/4 11:53
 */
@Getter
@ToString
@AllArgsConstructor
public enum WsResultTypeEnum {

    DECK_TYPE(100, "套牌类型"),
    SCRIPT_LOG(200, "脚本日志"),
    ;
    private final int value;
    private final String comment;
}
