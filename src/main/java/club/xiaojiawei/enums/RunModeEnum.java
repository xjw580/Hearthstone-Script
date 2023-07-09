package club.xiaojiawei.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;


/**
 * @author 肖嘉威
 * @date 2023/7/6 20:20
 * @msg
 */
@Getter
@ToString
@AllArgsConstructor
public enum RunModeEnum {
    STANDARD("STANDARD", "标准模式", ModeEnum.TOURNAMENT, true),
    WILD("WILD", "狂野模式", ModeEnum.TOURNAMENT, true),
    CLASSIC("CLASSIC", "经典模式", ModeEnum.TOURNAMENT, false),
    CASUAL("CASUAL", "休闲模式", ModeEnum.TOURNAMENT, true),
    BACON("BACON", "酒馆战棋", ModeEnum.BACON, false),
    ;
    final private String value;
    final private String comment;
    final private ModeEnum modeEnum;
    final private boolean enable;

}
