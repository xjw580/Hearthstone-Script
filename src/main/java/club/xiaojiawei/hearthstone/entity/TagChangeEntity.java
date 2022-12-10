package club.xiaojiawei.hearthstone.entity;

import club.xiaojiawei.hearthstone.enums.TagEnum;
import lombok.Data;
import lombok.ToString;

/**
 * @author 肖嘉威
 * @date 2022/11/29 14:24
 */
@Data
@ToString(callSuper = true)
public class TagChangeEntity extends CommonEntity{

    private TagEnum tag;

    private String value;

}
