package club.xiaojiawei.bean

/**
 * @author 肖嘉威
 * @date 2024/11/21 18:44
 */
class CardWeight() {
    constructor(weight: Double, powerWeight: Double, changeWeight: Double) : this() {
        this.weight = weight
        this.powerWeight = powerWeight
        this.changeWeight = changeWeight
    }

    /**
     * 权重：衡量卡牌的价值，影响本回合要出哪些牌及优先解哪个怪
     */
    var weight: Double = 0.0

    /**
     * 使用权重：衡量卡牌出牌顺序，比如本回合确定要出船载火炮和南海船工，如果船载火炮的使用权重大，将会先出船载火炮再出南海船工
     */
    var powerWeight: Double = 0.0

    var changeWeight: Double = 0.0
}
