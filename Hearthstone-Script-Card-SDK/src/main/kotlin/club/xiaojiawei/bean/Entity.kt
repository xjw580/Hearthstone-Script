package club.xiaojiawei.bean

import java.util.*
import java.util.function.Consumer

/**
 * @author 肖嘉威
 * @date 2022/11/28 19:18
 */
abstract class Entity {

    /**
     * [cardId]改变监听器
     */
    var cardIdChangeListener: Consumer<String>? = null

    /**
     * 在每局游戏中每张卡牌绑定一个唯一的ID，就算两张同样的红龙，它们也只是[cardId]相同，[entityId]绝对不相同
     */
    @Volatile
    var entityId: String = ""

    @Volatile
    var entityName: String = ""

    /**
     * 卡牌标识ID，类似身份证号，不同扩展包中同样的卡牌它们的[cardId]也会有略微不同
     */
    @Volatile
    var cardId: String = ""
        set(value) {
            cardIdChangeListener?.let {
                if (value != field) {
                    it.accept(value)
                }
            }
            field = value
        }

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
