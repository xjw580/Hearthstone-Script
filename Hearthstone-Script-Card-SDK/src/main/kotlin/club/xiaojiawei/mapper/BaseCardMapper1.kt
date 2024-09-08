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
interface BaseCardMapper1 {

    companion object {
        val INSTANCE: BaseCardMapper1 = Mappers.getMapper(BaseCardMapper1::class.java)
    }

    fun update(sourceCard: BaseCard?, @MappingTarget targetCard: BaseCard?)

}