package club.xiaojiawei.bean

/**
 * hs_cards.db数据库中cards表的实体类
 * @author 肖嘉威
 * @date 2024/11/13 15:14
 */
data class DBCard(
    var cardId: String = "",
    var name: String = "",
    var attack: Int? = null,
    var health: Int? = null,
    var cost: Int? = null,
    var text: String = "",
    var type: String? = null,
    var cardSet: String? = null
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DBCard

        return cardId == other.cardId
    }

    override fun hashCode(): Int {
        return cardId.hashCode()
    }
}