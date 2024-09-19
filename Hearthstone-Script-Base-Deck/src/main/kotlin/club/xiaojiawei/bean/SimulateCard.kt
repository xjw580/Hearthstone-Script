package club.xiaojiawei.bean

import club.xiaojiawei.enums.CardTypeEnum
import kotlin.math.max

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/9/18 15:26
 */
class SimulateCard(
    val card: Card,
    var attackCount: Int = 0,
    var blood: Int = 0,
    val atcWeight: Double = 1.0,
    val cardWeight: Double = 1.0,
    var isDivineShield: Boolean = false,
    val initAttackCount: Int = attackCount,
) : Cloneable {

    fun isAlive(): Boolean {
        return blood > 0
    }

    fun canAttack(): Boolean {
        return isAlive() && attackCount > 0 && card.canAttack()
    }

    fun canBeAttacked(): Boolean {
        return isAlive() && attackCount > 0
    }

    fun calcSelfWeight(): Double {
        return if (blood > 0) {
            (1 + blood + max(
                0,
                card.atc
            ) * atcWeight) * cardWeight + if (card.cardType === CardTypeEnum.HERO) Int.MAX_VALUE else 0
        } else 0.0
    }

    fun calcLazyWeight(lazyWeight: Double): Double {
        return if (blood > 0) {
            card.atc * max(attackCount, 0) * lazyWeight
        } else 0.0
    }

    public override fun clone(): SimulateCard {
        return SimulateCard(card, attackCount, blood, atcWeight, cardWeight)
    }

}