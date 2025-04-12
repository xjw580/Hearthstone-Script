package club.xiaojiawei.mctsbuilder

import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.WarScoreCalculatorBuilder
import club.xiaojiawei.enums.CardTypeEnum

/**
 * @author 肖嘉威
 * @date 2025/4/12 12:38
 */
class OnlyFaceWarScoreCalculatorBuilder : WarScoreCalculatorBuilder() {
//    override fun calcPlayScore(
//        area: PlayArea,
//        isMe: Boolean,
//    ): Double {
//        if (isMe) {
//            return super.calcPlayScore(area, isMe)
//        } else {
//            var score = 0.0
//            score += area.cards.sumOf { calcPlayCardScore(it, isMe) } * 10
//            area.hero?.let { hero ->
//                score += calcPlayCardScore(hero, isMe) * 200
//            }
//            area.weapon?.let { weapon ->
//                score += calcPlayCardScore(weapon, isMe) * 0.6
//            }
//            return score
//        }
//    }

    override fun calcPlayCardScore(
        card: Card,
        isMe: Boolean,
    ): Double {
        var res = super.calcPlayCardScore(card, isMe)
        if (card.isTaunt) res *= 10
        if (card.cardType === CardTypeEnum.HERO) res *= 100
        return res
    }
}
