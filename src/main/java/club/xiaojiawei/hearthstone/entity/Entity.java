package club.xiaojiawei.hearthstone.entity;

import lombok.Data;

/**
 * @author 肖嘉威
 * @date 2022/11/28 19:18
 */
@Data
public abstract class Entity {

    protected String entityId;

    protected String entityName;

    protected String cardId;

}
