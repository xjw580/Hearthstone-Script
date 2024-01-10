package club.xiaojiawei.mapper;

import club.xiaojiawei.bean.entity.Card;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/1/6 18:38
 */
@Mapper
public interface CardMapper {

    CardMapper INSTANCE = Mappers.getMapper(CardMapper.class);

    void update(Card sourceCard, @MappingTarget Card targetCard);

}
