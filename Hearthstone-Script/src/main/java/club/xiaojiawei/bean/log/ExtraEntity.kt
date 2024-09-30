package club.xiaojiawei.bean.log

import lombok.Data
import lombok.EqualsAndHashCode
import lombok.ToString

/**
 * @author 肖嘉威
 * @date 2022/11/29 11:46
 */
class ExtraEntity : CommonEntity() {

    val extraCard: ExtraCard = ExtraCard()

}
