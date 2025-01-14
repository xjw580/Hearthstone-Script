package club.xiaojiawei.mapper

import club.xiaojiawei.status.War
import org.mapstruct.Mapper
import org.mapstruct.Mapping
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

    @Mapping(target = "allowLog", constant = "false")
    fun clone(sourceWar: War): War

}