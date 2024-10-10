package club.xiaojiawei.bean

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.area.Area
import club.xiaojiawei.mapper.BaseCardMapper
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty

/**
 * @author 肖嘉威
 * @date 2022/11/27 14:56
 */
class Card(var action: CardAction) : BaseCard(), Cloneable {

    val areaProperty: ObjectProperty<Area?> = SimpleObjectProperty()

    /**
     * 卡牌所在区域：手牌区、战场区等
     */
    var area: Area?
        get() = areaProperty.get()
        set(value) {
            areaProperty.set(value)
        }

    @Override
    public override fun clone(): Card {
        try {
            val card = Card(this.action)
            BaseCardMapper.INSTANCE.update(this, card)
            return card
        } catch (e: CloneNotSupportedException) {
            throw RuntimeException(e)
        }
    }
}
