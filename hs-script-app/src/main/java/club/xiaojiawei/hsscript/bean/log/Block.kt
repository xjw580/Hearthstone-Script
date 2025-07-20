package club.xiaojiawei.hsscript.bean.log

import club.xiaojiawei.hsscript.enums.BlockTypeEnum

/**
 * @author 肖嘉威
 * @date 2022/11/30 17:48
 */
class Block(
    var blockType: BlockTypeEnum = BlockTypeEnum.UNKNOWN,
    var entity: CommonEntity? = null,
    var parentBlock: Block? = null
) {

    override fun toString(): String {
        return "Block【blockType=$blockType, entity=$entity】"
    }


}
