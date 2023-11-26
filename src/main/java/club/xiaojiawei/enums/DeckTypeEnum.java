package club.xiaojiawei.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author 肖嘉威
 * @date 2022/11/29 17:19
 */
@ToString
@Getter
@AllArgsConstructor
public enum DeckTypeEnum {

    STANDARD("标准"),
    WILD("狂野"),
    CLASSIC("经典"),
    CASUAL("休闲"),
    TWIST("幻变")
    ;
    private final String comment;

}
