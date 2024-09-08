package club.xiaojiawei.bean;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * @author 肖嘉威
 * @date 2022/11/28 19:18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class Entity {

    public static final String UNKNOWN_ENTITY_NAME = "UNKNOWN ENTITY";

    private String entityId;

    private String entityName;

    private final StringProperty cardId = new SimpleStringProperty();

    public String getCardId() {
        return cardId.get();
    }

    public StringProperty cardIdProperty() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId.set(cardId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity entity = (Entity) o;
        return Objects.equals(entityId, entity.entityId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityId);
    }
}
