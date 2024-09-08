package club.xiaojiawei.bean.area

import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.Player

/**
 * 牌库区
 * @author 肖嘉威
 * @date 2022/11/27 15:02
 */
class DeckArea(player: Player) : Area(60, player) {
    override fun addZeroCard(card: Card?) {
        add(card)
    }
}
