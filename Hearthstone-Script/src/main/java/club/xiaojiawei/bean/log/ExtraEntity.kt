package club.xiaojiawei.bean.log;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author 肖嘉威
 * @date 2022/11/29 11:46
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class ExtraEntity extends CommonEntity {

    public ExtraCard extraCard = new ExtraCard();

}
