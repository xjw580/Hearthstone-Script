package club.xiaojiawei.bean;

import club.xiaojiawei.bean.area.Area;
import club.xiaojiawei.bean.log.CommonEntity;
import club.xiaojiawei.bean.log.ExtraEntity;
import club.xiaojiawei.custom.CustomToStringGenerator;
import club.xiaojiawei.mapper.CardMapper;
import club.xiaojiawei.mapper.EntityMapper;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import static club.xiaojiawei.data.ScriptStaticData.CARD_AREA_MAP;

/**
 * 属性来源于{@link club.xiaojiawei.enums.TagEnum}
 * @author 肖嘉威
 * @date 2022/11/27 14:56
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class Card extends BaseCard implements Cloneable{

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

    public Card() {
        area.addListener((observableValue, area1, t1) -> {
            CARD_AREA_MAP.remove(this.getEntityId());
            CARD_AREA_MAP.put(this.getEntityId(), t1);
        });
    }

    public Card(CommonEntity commonEntity) {
        super(commonEntity.getEntityId(), commonEntity.getEntityName(), commonEntity.getCardId());
        area.addListener((observableValue, area1, t1) -> {
            CARD_AREA_MAP.remove(this.getEntityId());
            CARD_AREA_MAP.put(this.getEntityId(), t1);
        });
    }

    public void updateByExtraEntity(ExtraEntity extraEntity){
        CardMapper.INSTANCE.update(extraEntity.getExtraCard().getCard(), this);
        EntityMapper.INSTANCE.update(extraEntity, this);
    }


    @Override
    public Card clone() {
        try {
            Card card = (Card) super.clone();
            CardMapper.INSTANCE.update(this, card);
            return card;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return CustomToStringGenerator.generateToString(this, true);
    }

    public String toSimpleString(){
        return "【entityId:" + getEntityId() + "，entityName:" + getEntityName() + "，cardId:" + getCardId() + "】";
    }

}
