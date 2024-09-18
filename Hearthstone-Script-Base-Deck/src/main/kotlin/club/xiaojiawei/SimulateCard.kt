package club.xiaojiawei

import club.xiaojiawei.bean.Card
import kotlin.math.max

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/9/18 15:26
 */
class SimulateCard(
    val card:Card,
    var attackCount:Int = 0,
    var blood:Int = 0,
    var atcWeight:Double = 1.0,
    var cardWeight:Double = 1.0,
    val isHero:Boolean = false
) {

    fun isAlive():Boolean{
        return blood > 0
    }

    fun canAttack():Boolean{
        return isAlive() && attackCount > 0 && card.canMove()
    }

    fun canBeAttacked():Boolean{
        return isAlive() && attackCount > 0
    }

    fun calcSelfWeight():Double{
        return if (blood > 0) {
            (blood + max(0, card.atc) * atcWeight) * cardWeight + if (isHero) Int.MAX_VALUE else 0
        } else 0.0
    }

    fun calcAtcWeight():Double{
        return card.atc * attackCount * atcWeight
    }

}