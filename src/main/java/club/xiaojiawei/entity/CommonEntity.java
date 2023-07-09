package club.xiaojiawei.entity;

import club.xiaojiawei.enums.ZoneEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author 肖嘉威
 * @date 2022/11/30 12:38
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class CommonEntity extends Entity{

    private String entity;

    private ZoneEnum zone;

    private int zonePos;

    private String playerId;

}
