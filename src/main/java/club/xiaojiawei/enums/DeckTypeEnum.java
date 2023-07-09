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

    STANDARD("STANDARD", "标准"),
    WILD("WILD", "狂野"),
    CLASSIC("CLASSIC", "经典"),
    CASUAL("CASUAL", "休闲"),
    TWIST("TWIST", "幻变")
    ;
    private final String value;
    private final String comment;

}
