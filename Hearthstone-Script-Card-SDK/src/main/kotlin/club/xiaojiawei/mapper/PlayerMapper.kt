package club.xiaojiawei.mapper

import club.xiaojiawei.bean.Player
import org.mapstruct.Mapper
import org.mapstruct.MappingTarget
import org.mapstruct.factory.Mappers

/**
 * @author 肖嘉威
 * @date 2024/9/8 19:17
 */
@Mapper
interface PlayerMapper {

    companion object {
        val INSTANCE: PlayerMapper = Mappers.getMapper(PlayerMapper::class.java)
    }

    fun update(sourcePlayer: Player, @MappingTarget targetPlayer: Player)

}