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

    fun update(sourceCard: BaseCard?, @MappingTarget targetCard: Card?)

}