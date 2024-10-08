package club.xiaojiawei.hsscript.bean;

import club.xiaojiawei.enums.WsResultTypeEnum;
import lombok.Data;

/**
 * @author 肖嘉威
 * @date 2022/12/4 11:52
 */
@Data
public class WsResult <T>{

    private WsResultTypeEnum type;

    private T data;

    public WsResult(WsResultTypeEnum type, T data) {
        this.type = type;
        this.data = data;
    }

    public static <T>WsResult<T> ofNew(WsResultTypeEnum wsResultType, T msg){
        return new WsResult<>(wsResultType, msg);
    }

}
