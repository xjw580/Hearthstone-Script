package club.xiaojiawei.hearthstone.entity;

import lombok.Data;
import lombok.ToString;

/**
 * @author 肖嘉威
 * @date 2022/11/29 11:46
 */
@Data
@ToString(callSuper = true)
public class ExtraEntity extends CommonEntity {

    private ExtraCard extraCard = new ExtraCard();
}
