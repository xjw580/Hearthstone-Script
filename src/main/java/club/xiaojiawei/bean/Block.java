package club.xiaojiawei.bean;

import club.xiaojiawei.bean.entity.CommonEntity;
import club.xiaojiawei.enums.BlockTypeEnum;
import lombok.Data;

/**
 * @author 肖嘉威
 * @date 2022/11/30 17:48
 */
@Data
public class Block {

    private BlockTypeEnum blockType;

    private CommonEntity entity;

}
