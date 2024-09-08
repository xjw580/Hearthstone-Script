package club.xiaojiawei.bean.log;

import club.xiaojiawei.bean.BaseCard;
import club.xiaojiawei.bean.area.Area;
import club.xiaojiawei.bean.Card;
import club.xiaojiawei.enums.ZoneEnum;
import javafx.beans.property.ObjectProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author 肖嘉威
 * @date 2022/11/30 12:33
 */
@Data
@ToString(callSuper = true)
public class ExtraCard{

    public BaseCard card = new BaseCard();

    private ZoneEnum zone;

    private int zonePos;

    private String controllerPlayerId;

}
