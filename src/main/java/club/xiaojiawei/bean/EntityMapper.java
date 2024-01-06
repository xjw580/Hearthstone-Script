package club.xiaojiawei.bean;

import club.xiaojiawei.bean.entity.Card;
import club.xiaojiawei.bean.entity.Entity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/1/6 18:38
 */
@Mapper
public interface EntityMapper {

    EntityMapper INSTANCE = Mappers.getMapper(EntityMapper.class);

    void update(Entity sourceEntity, @MappingTarget Entity targetEntity);

}
