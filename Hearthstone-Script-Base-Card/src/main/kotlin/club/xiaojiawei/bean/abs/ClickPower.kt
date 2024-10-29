package club.xiaojiawei.bean.abs

import club.xiaojiawei.CardAction

/**
 * @author 肖嘉威
 * @date 2024/9/22 22:24
 */
abstract class ClickPower : CardAction.DefaultCardAction() {

    override fun execPower(): Boolean {
        return lClick()
    }

}