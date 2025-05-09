package club.xiaojiawei.bean.area

import club.xiaojiawei.bean.Card
import club.xiaojiawei.bean.Player
import club.xiaojiawei.status.WAR

/**
 * 除外区（各种效果在此生成）,发现时，最新的三张就是需要选择的牌
 * @author 肖嘉威
 * @date 2022/11/30 14:36
 */
class SetasideArea(allowLog: Boolean = false, player: Player) :
    Area(allowLog = allowLog, maxSize = Int.MAX_VALUE, player = player) {

    override fun addZeroCard(card: Card?) {
        add(card)
    }

}