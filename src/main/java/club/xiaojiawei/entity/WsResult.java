package club.xiaojiawei.entity;

import club.xiaojiawei.enums.WsResultTypeEnum;
import lombok.Data;

/**
 * @author 肖嘉威
 * @date 2022/12/4 11:52
 */
@Data
public class WsResult <T>{

    private WsResultTypeEnum wsResultType;

    private T msg;

    public WsResult(WsResultTypeEnum wsResultType, T msg) {
        this.wsResultType = wsResultType;
        this.msg = msg;
    }

    public static <T>WsResult<T> ofNew(WsResultTypeEnum wsResultType, T msg){
        return new WsResult<>(wsResultType, msg);
    }


}
