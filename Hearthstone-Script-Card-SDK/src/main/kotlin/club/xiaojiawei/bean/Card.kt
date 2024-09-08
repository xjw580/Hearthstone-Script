package club.xiaojiawei.bean

import club.xiaojiawei.CardAction
import club.xiaojiawei.bean.area.Area
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty

/**
 * @author 肖嘉威
 * @date 2022/11/27 14:56
 */
class Card(var action: CardAction) : BaseCard() {

    val areaProperty: ObjectProperty<Area?> = SimpleObjectProperty()

    var area: Area?
        get() = areaProperty.get()
        set(value) {
            areaProperty.set(value)
        }
}
