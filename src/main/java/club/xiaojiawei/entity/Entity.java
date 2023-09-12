package club.xiaojiawei.entity;

import lombok.Data;

import java.util.Objects;

/**
 * @author 肖嘉威
 * @date 2022/11/28 19:18
 */
@Data
public abstract class Entity {

    protected String entityId;

    protected String entityName;

    protected String cardId;

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
