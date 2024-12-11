package club.xiaojiawei.hsscript.bean

import club.xiaojiawei.hsscript.enums.BlockTypeEnum

/**
 * @author 肖嘉威
 * @date 2024/12/10 19:46
 */
class Behavior(val blockType: BlockTypeEnum) {
    val millis: Long = System.currentTimeMillis()
}