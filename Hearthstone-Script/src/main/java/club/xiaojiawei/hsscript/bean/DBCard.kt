package club.xiaojiawei.hsscript.bean

/**
 * @author 肖嘉威
 * @date 2024/11/13 15:14
 */
data class DBCard(
    var cardId: String = "",
    var name: String = "",
    var attack: Int? = null,
    var health: Int? = null,
    var cost: Int? = null,
    var text: String? = null,
    var type: String? = null,
    var cardSet: String? = null
)