package club.xiaojiawei.hsscript.bean.log

/**
 * @author 肖嘉威
 * @date 2022/11/29 11:46
 */
class ExtraEntity(
    val extraCard: ExtraCard = ExtraCard(),
    var parentBlock: Block? = null,
) : CommonEntity()
