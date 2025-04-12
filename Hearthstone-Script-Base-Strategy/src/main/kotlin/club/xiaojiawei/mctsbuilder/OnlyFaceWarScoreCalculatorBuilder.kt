package club.xiaojiawei.mctsbuilder

import club.xiaojiawei.bean.WarScoreCalculatorBuilder
import club.xiaojiawei.bean.area.PlayArea

/**
 * @author 肖嘉威
 * @date 2025/4/12 12:38
 */
class OnlyFaceWarScoreCalculatorBuilder : WarScoreCalculatorBuilder() {
    override fun calcPlayScore(
        area: PlayArea,
        isMe: Boolean,
    ): Double {
        if (isMe) {
            return super.calcPlayScore(area, isMe)
        } else {
            var score = 0.0
            score += area.cards.sumOf { calcPlayCardScore(it, isMe) } * 0.1
            area.hero?.let { hero ->
                score += calcPlayCardScore(hero, isMe)
            }
            area.weapon?.let { weapon ->
                score += calcPlayCardScore(weapon, isMe) * 0.6
            }
            return score
        }
    }
}