package club.xiaojiawei.bean;

import club.xiaojiawei.bean.area.Area;
import club.xiaojiawei.bean.log.CommonEntity;
import club.xiaojiawei.bean.log.ExtraEntity;
import club.xiaojiawei.custom.CustomToStringGenerator;
import club.xiaojiawei.mapper.BaseCardMapper;
import club.xiaojiawei.mapper.EntityMapper;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static club.xiaojiawei.data.ScriptStaticData.CARD_AREA_MAP;

/**
 * 属性来源于{@link club.xiaojiawei.enums.TagEnum}
 * @author 肖嘉威
 * @date 2022/11/27 14:56
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public abstract class Card extends BaseCard{

    private static Card defaultCard;

    public static void setDefaultCard(Card defaultCard) {
        if (Card.defaultCard == null) {
            Card.defaultCard = defaultCard;
        }
    }

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
        addListener();
    }

    public Card(CommonEntity commonEntity) {
        super(commonEntity.getEntityId(), commonEntity.getEntityName(), commonEntity.getCardId());
        addListener();
    }

    private void addListener(){
        area.addListener((observableValue, area1, t1) -> {
            CARD_AREA_MAP.remove(this.getEntityId());
            CARD_AREA_MAP.put(this.getEntityId(), t1);
        });
    }


    public static class Action{

        private final Card card;

        private Action(Card card) {
            this.card = card;
        }

        private final List<Runnable> runnableList = new ArrayList<>();

        public Action exec(){
            runnableList.forEach(Runnable::run);
            return this;
        }

        public Action clear(){
            runnableList.clear();
            return this;
        }

    }

    abstract public boolean power();

    abstract public boolean power(Card card);

    abstract public boolean power(int index);

    abstract public boolean attackMinion(Card card);

    abstract public boolean attackHero();

    abstract public boolean pointTo(Card card);

}
