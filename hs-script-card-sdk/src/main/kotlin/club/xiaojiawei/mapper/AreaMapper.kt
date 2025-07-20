package club.xiaojiawei.mapper

import club.xiaojiawei.bean.area.Area
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.mapstruct.factory.Mappers

/**
 * @author 肖嘉威
 * @date 2024/9/8 19:17
 */
@Mapper
interface AreaMapper {

    companion object {
        val INSTANCE: AreaMapper = Mappers.getMapper(AreaMapper::class.java)
    }

    @Mapping(target = "cards", ignore = true)
    fun update(sourceArea: Area, @MappingTarget targetPlayer: Area)

}