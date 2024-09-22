package club.xiaojiawei.bean.abs

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.Card

/**
 * @author 肖嘉威
 * @date 2024/9/22 22:24
 */
abstract class ClickPower : CardAction.DefaultCardAction() {

    override fun execPower(): Boolean {
        return lClick()
    }

}