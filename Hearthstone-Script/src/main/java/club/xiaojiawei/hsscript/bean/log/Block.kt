package club.xiaojiawei.hsscript.bean.log

import club.xiaojiawei.hsscript.enums.BlockTypeEnum

/**
 * @author 肖嘉威
 * @date 2022/11/30 17:48
 */
class Block(blockType: BlockTypeEnum, var entity: CommonEntity?) {

    constructor() : this(BlockTypeEnum.UNKNOWN, null)

    var blockType: BlockTypeEnum = blockType

}
