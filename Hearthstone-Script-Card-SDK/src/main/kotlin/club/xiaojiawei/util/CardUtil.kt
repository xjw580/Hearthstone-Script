package club.xiaojiawei.util

import club.xiaojiawei.bean.Card
import club.xiaojiawei.enums.CardTypeEnum

/**
 * @author 肖嘉威
 * @date 2025/1/15 10:47
 */
object CardUtil {

    fun getTauntCards(cards: List<Card>, canBeAttacked: Boolean = true): MutableList<Card> {
        val result = mutableListOf<Card>()
        for (card in cards) {
            if (card.isTaunt && (!canBeAttacked || card.canBeAttacked())) {
                result.add(card)
            }
        }
        return result
    }

    fun simulateAttack(myCard: Card, rivalCard: Card, deathRemove: Boolean = false) {
//        处理我方情况
        if (myCard.isImmuneWhileAttacking || myCard.isImmune) {
        } else if (myCard.isDivineShield) {
            if (rivalCard.atc > 0) {
                myCard.isDivineShield = false
            }
        } else if (rivalCard.isPoisonous && myCard.cardType === CardTypeEnum.MINION) {
            myCard.damage = myCard.health + myCard.armor
        } else {
            myCard.damage += rivalCard.atc
        }

//        处理敌方情况
        if (rivalCard.isDivineShield) {
            if (myCard.atc > 0) {
                rivalCard.isDivineShield = false
            }
        } else if (myCard.isPoisonous && rivalCard.cardType === CardTypeEnum.MINION) {
            rivalCard.damage = rivalCard.health + rivalCard.armor
        } else {
            rivalCard.damage += myCard.atc
        }

//        处理我方可攻击次数
        myCard.attackCount++
        if (myCard.isMegaWindfury) {
            if (myCard.attackCount >= 4) {
                myCard.isExhausted = true
            }
        } else if (myCard.isWindFury) {
            if (myCard.attackCount >= 2) {
                myCard.isExhausted = true
            }
        } else {
            myCard.isExhausted = true
        }

//        死亡判断
        if (deathRemove) {
            if (!myCard.isSurvival()) {
                myCard.area?.removeByEntityId(myCard.entityId)
            }
            if (!rivalCard.isSurvival()) {
                rivalCard.area?.removeByEntityId(rivalCard.entityId)
            }
        }
    }

}