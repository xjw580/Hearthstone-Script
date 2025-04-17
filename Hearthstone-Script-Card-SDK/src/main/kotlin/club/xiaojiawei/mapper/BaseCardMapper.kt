package club.xiaojiawei.mapper

import club.xiaojiawei.bean.BaseCard
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
        val fields = targetCard.javaClass.declaredFields
        for (field in fields) {
            if (field.type == Boolean::class.java) {
                field.isAccessible = true
                val targetValue = field.getBoolean(targetCard)
                if (!targetValue) {
                    val sourceValue = field.getBoolean(sourceCard)
                    field.setBoolean(targetCard, sourceValue)
                }
            }
        }
    }

}