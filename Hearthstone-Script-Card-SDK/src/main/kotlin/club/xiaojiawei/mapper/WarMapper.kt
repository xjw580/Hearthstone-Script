package club.xiaojiawei.mapper

import club.xiaojiawei.bean.War
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.mapstruct.factory.Mappers

/**
 * @author 肖嘉威
 * @date 2024/9/8 19:17
 */
@Mapper
interface WarMapper {

    companion object {
        val INSTANCE: WarMapper = Mappers.getMapper(WarMapper::class.java)
    }

    @Mapping(target = "cardMap", ignore = true)
    fun update(sourceWar: War, @MappingTarget targetWar: War)

}