package club.xiaojiawei.bean

import club.xiaojiawei.enums.CardTypeEnum
import kotlin.math.max

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/9/18 15:26
 */
class SimulateCard(
    val card: Card,
    var attackCount: Int,
    var inversionAttackCount: Int,
    val atcWeight: Double,
    val inversionAtcWeight: Double,
    var blood: Int,
    val cardWeight: Double = 1.0,
    var isDivineShield: Boolean = false,
) : Cloneable {

    fun isAlive(): Boolean {
        return blood > 0
    }

    fun canAttack(inversion: Boolean): Boolean {
        return isAlive() && if (inversion) inversionAttackCount > 0 else attackCount > 0
    }

    fun canBeAttacked(inversion: Boolean): Boolean {
        return isAlive() && if (inversion) inversionAttackCount > 0 else attackCount > 0
    }

    fun calcSelfWeight(inversion: Boolean): Double {
        return if (blood > 0) {
            (blood + max(
                0,
                card.atc
            ) * (if (inversion) inversionAtcWeight else atcWeight)) * cardWeight + (if (card.cardType === CardTypeEnum.HERO) 1_000_000 else 0)
        } else 0.0
    }

    fun calcLazyWeight(lazyWeight: Double): Double {
        return if (blood > 0) {
            card.atc * max(attackCount, 0) * lazyWeight
        } else 0.0
    }

    public override fun clone(): SimulateCard {
        return SimulateCard(card, attackCount, inversionAttackCount, atcWeight, inversionAtcWeight, blood, cardWeight, isDivineShield)
    }

}