package club.xiaojiawei.bean.area

import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.Player

/**
 * 墓地
 * @author 肖嘉威
 * @date 2022/11/27 15:02
 */
class GraveyardArea(player: Player) : Area(Int.MAX_VALUE, player) {
    override fun addZeroCard(card: Card?) {
        add(card)
    }
}
