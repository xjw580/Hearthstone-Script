package club.xiaojiawei.interfaces;

import club.xiaojiawei.bean.log.ExtraEntity;

/**
 * 处理这种日志：tag=PREMIUM value=1
 * @author 肖嘉威
 * @date 2023/9/18 13:40
 */
@FunctionalInterface
public interface ParseExtraEntity {

    void parseExtraEntity(ExtraEntity extraEntity, String value);

}
