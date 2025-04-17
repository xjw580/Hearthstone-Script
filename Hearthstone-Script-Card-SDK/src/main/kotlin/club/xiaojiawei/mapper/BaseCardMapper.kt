package club.xiaojiawei.mapper

import club.xiaojiawei.bean.BaseCard
import club.xiaojiawei.bean.Card
import org.mapstruct.Mapper
import org.mapstruct.MappingTarget
import org.mapstruct.factory.Mappers

/**
 * @author 肖嘉威
 * @date 2024/9/8 19:13
 */
@Mapper
interface BaseCardMapper {

    companion object {
        val INSTANCE: BaseCardMapper = Mappers.getMapper(BaseCardMapper::class.java)
    }

    fun update(sourceCard: BaseCard?, @MappingTarget targetCard: BaseCard?)

    fun updateBoolean(sourceCard: BaseCard?, @MappingTarget targetCard: BaseCard?) {
        if (sourceCard == null || targetCard == null) return

        // 获取 BaseCard 的所有字段（包括私有字段）
        val fields = targetCard.javaClass.declaredFields

        fields.forEach { field ->
            // 只处理 Boolean 类型字段
            if (field.type == Boolean::class.java || field.type == Boolean::class.java) {
                field.isAccessible = true // 允许访问私有字段

                val targetValue = field.getBoolean(targetCard)
                if (!targetValue) { // 仅当目标为 false 时才更新
                    val sourceValue = field.getBoolean(sourceCard)
                    field.setBoolean(targetCard, sourceValue)
                }
            }
        }
    }

}