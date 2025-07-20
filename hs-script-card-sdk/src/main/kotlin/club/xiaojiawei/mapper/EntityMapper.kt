package club.xiaojiawei.mapper

import club.xiaojiawei.bean.Entity
import org.mapstruct.Mapper
import org.mapstruct.MappingTarget
import org.mapstruct.factory.Mappers

/**
 * @author 肖嘉威
 * @date 2024/9/8 19:17
 */
@Mapper
interface EntityMapper {

    companion object {
        val INSTANCE: EntityMapper = Mappers.getMapper(EntityMapper::class.java)
    }

    fun update(sourceEntity: Entity?, @MappingTarget targetEntity: Entity?)

}