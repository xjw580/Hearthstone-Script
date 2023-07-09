package club.xiaojiawei.entity;

import club.xiaojiawei.enums.TagEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author 肖嘉威
 * @date 2022/11/29 14:24
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class TagChangeEntity extends CommonEntity{

    private TagEnum tag;

    private String value;

}
