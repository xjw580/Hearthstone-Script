package club.xiaojiawei.hsscript.interfaces

import club.xiaojiawei.hsscript.bean.log.ExtraEntity

/**
 * 处理这种日志：tag=PREMIUM value=1
 * @author 肖嘉威
 * @date 2023/9/18 13:40
 */
fun interface ExtraEntityHandler {

    fun handle(extraEntity: ExtraEntity, value: String)

}
