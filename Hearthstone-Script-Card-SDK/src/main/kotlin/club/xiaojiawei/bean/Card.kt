package club.xiaojiawei.bean;

import club.xiaojiawei.CardAction;
import club.xiaojiawei.bean.area.Area;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;


/**
 * @author 肖嘉威
 * @date 2022/11/27 14:56
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class Card extends BaseCard{

    private CardAction action;

    private final ObjectProperty<Area> area = new SimpleObjectProperty<>();

    public Area getArea() {
        return area.get();
    }

    public ObjectProperty<Area> areaProperty() {
        return area;
    }

    public void setArea(Area area) {
        this.area.set(area);
    }

}
