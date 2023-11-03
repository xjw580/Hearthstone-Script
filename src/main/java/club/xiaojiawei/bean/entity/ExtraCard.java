package club.xiaojiawei.bean.entity;

import club.xiaojiawei.enums.ZoneEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author 肖嘉威
 * @date 2022/11/30 12:33
 */
@Data
@ToString(callSuper = true)
public class ExtraCard{

    private Card card = new Card();

    private ZoneEnum zone;

    private int zonePos;

    private String controllerPlayerId;

}
