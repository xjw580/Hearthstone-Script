package club.xiaojiawei.custom;

import club.xiaojiawei.entity.ExtraEntity;

/**
 * @author 肖嘉威
 * @date 2023/9/18 13:40
 * @msg 处理这种日志：tag=PREMIUM value=1
 */
@FunctionalInterface
public interface ParseExtraEntity {

    void parseExtraEntity(ExtraEntity extraEntity, String value);
}
