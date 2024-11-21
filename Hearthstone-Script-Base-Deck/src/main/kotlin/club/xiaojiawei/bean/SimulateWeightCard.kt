package club.xiaojiawei.bean

/**
 * @author 肖嘉威
 * @date 2024/11/14 11:19
 */
class SimulateWeightCard(
    val card: Card,
    var weight: Double,
    var powerWeight: Double = 0.0,
) {
    override fun toString(): String {
        return "【entityId:${card.entityId}，entityName:${card.entityName}，cardId:${card.cardId}，weight：${weight}】"
    }
}