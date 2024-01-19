package club.xiaojiawei.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;


/**
 * @author 肖嘉威
 * @date 2023/7/6 20:20
 */
@Getter
@ToString
@AllArgsConstructor
public enum RunModeEnum {

    STANDARD("标准模式", ModeEnum.TOURNAMENT, true),
    WILD("狂野模式", ModeEnum.TOURNAMENT, true),
    CASUAL("休闲模式", ModeEnum.TOURNAMENT, true),
    CLASSIC("经典模式", ModeEnum.TOURNAMENT, false),
    TWIST("幻变模式", ModeEnum.TOURNAMENT, false),
    BACON("酒馆战棋", ModeEnum.BACON, false),
    ;

    private final String comment;
    private final ModeEnum modeEnum;
    private final boolean enable;

}
