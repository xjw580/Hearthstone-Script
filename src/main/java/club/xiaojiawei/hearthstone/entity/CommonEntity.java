package club.xiaojiawei.hearthstone.entity;

import club.xiaojiawei.hearthstone.enums.ZoneEnum;
import lombok.Data;
import lombok.ToString;

/**
 * @author 肖嘉威
 * @date 2022/11/30 12:38
 */
@Data
@ToString(callSuper = true)
public class CommonEntity extends Entity{

    private String entity;

    private ZoneEnum zone;

    private int zonePos;

    private String playerId;

}
