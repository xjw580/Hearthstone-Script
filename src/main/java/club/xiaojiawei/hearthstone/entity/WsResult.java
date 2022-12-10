package club.xiaojiawei.hearthstone.entity;

import club.xiaojiawei.hearthstone.enums.WsResultTypeEnum;
import lombok.Data;

/**
 * @author 肖嘉威
 * @date 2022/12/4 11:52
 */
@Data
public class WsResult <T>{

    private WsResultTypeEnum wsResultType;

    private T msg;

    private WsResult(WsResultTypeEnum wsResultType, T msg) {
        this.wsResultType = wsResultType;
        this.msg = msg;
    }

    public static WsResult<Object> ofDeckType(Object msg){
        return new WsResult<>(WsResultTypeEnum.DECK_TYPE, msg);
    }

    public static WsResult<Object> ofScriptLog(Object msg){
        return new WsResult<>(WsResultTypeEnum.SCRIPT_LOG, msg);
    }


}
