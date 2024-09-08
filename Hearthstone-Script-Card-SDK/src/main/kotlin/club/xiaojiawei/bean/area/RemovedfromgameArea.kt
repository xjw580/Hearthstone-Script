package club.xiaojiawei.bean.area

import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.Player

/**
 * 移除区
 *
 * @author 肖嘉威
 * @date 2022/12/3 21:37
 */
class RemovedfromgameArea(player: Player) : Area(Int.MAX_VALUE, player) {
    override fun addZeroCard(card: Card?) {
        add(card)
    }
}
