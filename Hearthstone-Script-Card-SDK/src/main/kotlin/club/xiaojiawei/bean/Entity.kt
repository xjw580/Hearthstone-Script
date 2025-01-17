package club.xiaojiawei.bean

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import java.util.*

/**
 * @author 肖嘉威
 * @date 2022/11/28 19:18
 */
abstract class Entity {

    @Volatile
    var entityId: String = ""

    @Volatile
    var entityName: String = ""

    val cardIdProperty: StringProperty = SimpleStringProperty("")

    var cardId: String
        get() = cardIdProperty.get()
        set(value) = cardIdProperty.set(value)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val entity = other as Entity
        return entityId == entity.entityId
    }

    override fun hashCode(): Int {
        return Objects.hash(entityId)
    }

    companion object {
        const val UNKNOWN_ENTITY_NAME: String = "UNKNOWN ENTITY"
    }

    override fun toString(): String {
        return "【entityId:$entityId，entityName:$entityName，cardId:${cardId}】"
    }

}
