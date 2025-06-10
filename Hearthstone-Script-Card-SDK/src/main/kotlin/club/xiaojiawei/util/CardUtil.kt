package club.xiaojiawei.util

import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.CardInfo
import club.xiaojiawei.bean.War
import club.xiaojiawei.bean.area.PlayArea
import club.xiaojiawei.enums.CardActionEnum
import club.xiaojiawei.enums.CardTypeEnum

/**
 * @author 肖嘉威
 * @date 2025/1/15 10:47
 */
object CardUtil {

    /**
     * 处理卡牌的疲劳
     */
    fun handleCardExhaustedWhenIntoPlayArea(card: Card) {
        if (card.cardType === CardTypeEnum.LOCATION) {
            card.isExhausted = false
            card.isLocationActionCooldown = false
        } else if (card.isCharge) {
            card.isExhausted = false
        } else if (card.isRush) {
            card.isAttackableByRush = true
            card.isExhausted = false
        } else {
            card.isExhausted = true
        }
    }

    /**
     * 获取嘲讽卡牌
     */
    fun getTauntCards(cards: List<Card>, canBeAttacked: Boolean = true): MutableList<Card> {
        val result = mutableListOf<Card>()
        for (card in cards) {
            if (card.isTaunt && (!canBeAttacked || card.canBeAttacked())) {
                result.add(card)
            }
        }
        return result
    }

    /**
     * 模拟攻击
     */
    fun simulateAttack(war: War, myCard: Card, rivalCard: Card) {
        val myPlayArea = myCard.area
        if (myPlayArea !is PlayArea || rivalCard.area !is PlayArea) return

//        处理我方情况
        if (myCard.isImmuneWhileAttacking || myCard.isImmune) {
        } else if (myCard.isDivineShield) {
            if (rivalCard.atc > 0) {
                myCard.isDivineShield = false
            }
        } else if (rivalCard.isPoisonous && myCard.cardType === CardTypeEnum.MINION) {
            myCard.injured(myCard.bloodLimit())
        } else {
            myCard.injured(rivalCard.atc)
        }

//        处理我方武器情况
        if (myCard.cardType === CardTypeEnum.HERO) {
            myPlayArea.weapon?.let {
                if (!it.isImmune) {
                    it.injured(1)
                }
            }
        }

//        处理敌方情况
        if (rivalCard.isDivineShield) {
            if (myCard.atc > 0) {
                rivalCard.isDivineShield = false
            }
        } else if (myCard.isPoisonous && rivalCard.cardType === CardTypeEnum.MINION) {
            rivalCard.injured(rivalCard.bloodLimit())
        } else {
            rivalCard.injured(myCard.atc)
        }

//        处理我方可攻击次数
        myCard.attackCount++

        if (myCard.isMegaWindfury || (myCard.cardType === CardTypeEnum.HERO && myPlayArea.weapon?.isMegaWindfury == true)) {
            if (myCard.attackCount >= 4) {
                myCard.isExhausted = true
            }
        } else if (myCard.isWindFury || (myCard.cardType === CardTypeEnum.HERO && myPlayArea.weapon?.isWindFury == true)) {
            if (myCard.attackCount >= 2) {
                myCard.isExhausted = true
            }
        } else {
            myCard.isExhausted = true
        }
    }

}