package club.xiaojiawei.bean.log

import club.xiaojiawei.enums.BlockTypeEnum
import lombok.Data

/**
 * @author 肖嘉威
 * @date 2022/11/30 17:48
 */
class Block {

    var blockType: BlockTypeEnum? = null

    var entity: CommonEntity? = null

}
