package club.xiaojiawei.bean

import club.xiaojiawei.enums.CardTypeEnum
import kotlin.math.max

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/9/18 15:26
 */
class SimulateCard(
    val card: Card,
    val cardInfo: CardInfo?,
    var attackCount: Int,
    var inversionAttackCount: Int,
    private val atcWeight: Double,
    private val inversionAtcWeight: Double,
    blood: Int,
    val cardWeight: Double = 1.0,
    val inversionCardWeight: Double = 1.0,
    var isDivineShield: Boolean = false,

    private val calcHeroWeight: Int = if (card.cardType === CardTypeEnum.HERO) HERO_EXTRA_WEIGHT else 0,
    private var calcBloodWeight: Double = blood * cardWeight,
    private var calcInversionBloodWeight: Double = blood * inversionCardWeight,
    private val calcWeight: Double = atcWeight * max(0, card.atc) * cardWeight + calcHeroWeight,
    private val calcInversionWeight: Double = inversionAtcWeight * max(
        0,
        card.atc
    ) * inversionCardWeight + calcHeroWeight,

    var text: String = ""
) : Cloneable {

    companion object {
        const val HERO_EXTRA_WEIGHT = 1_000_000
        const val TAUNT_EXTRA_WEIGHT = HERO_EXTRA_WEIGHT shl 1
        const val DIVINE_SHIELD_WEIGHT = 0.3

        fun copySimulateList(cards: List<SimulateCard>): MutableList<SimulateCard> {
            val copyList = mutableListOf<SimulateCard>()
            for (card in cards) {
                val clone = card.clone()
                copyList.add(clone)
            }
            return copyList
        }
    }

    var blood: Int = blood
        set(value) {
            field = value
            calcBloodWeight = value * cardWeight
            calcInversionBloodWeight = value * inversionCardWeight
        }

    fun isAlive(): Boolean {
        return blood > 0
    }

    fun canAttack(inversion: Boolean): Boolean {
        return isAlive() && if (inversion) inversionAttackCount > 0 else attackCount > 0
    }

    fun canAttackHero(inversion: Boolean): Boolean {
        return !card.isAttackableByRush && canAttack(inversion)
    }

    fun canBeAttacked(inversion: Boolean): Boolean {
        return isAlive() && if (inversion) inversionAttackCount > 0 else attackCount > 0
    }


    fun calcSelfWeight(inversion: Boolean): Double {
        return if (blood <= 0)
            0.0
        else
            if (inversion)
                calcInversionWeight + calcInversionBloodWeight + (if (isDivineShield) card.atc * DIVINE_SHIELD_WEIGHT else 0.0)
            else
                calcWeight + calcBloodWeight + (if (isDivineShield) card.atc * DIVINE_SHIELD_WEIGHT else 0.0)
//        return if (blood <= 0) 0.0 else blood * cardWeight + if (inversion) calcInversionAtcWeight else calcAtcWeight
    }

    public override fun clone(): SimulateCard {
        return SimulateCard(
            card,
            cardInfo,
            attackCount,
            inversionAttackCount,
            atcWeight,
            inversionAtcWeight,
            blood,
            cardWeight,
            inversionCardWeight,
            isDivineShield,
            calcHeroWeight,
            calcBloodWeight,
            calcInversionBloodWeight,
            calcWeight,
            calcInversionWeight
        )
    }

}