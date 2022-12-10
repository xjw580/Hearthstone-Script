package club.xiaojiawei.hearthstone.appender;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import club.xiaojiawei.hearthstone.entity.WsResult;
import club.xiaojiawei.hearthstone.ws.WebSocketServer;


/**
 * @author 肖嘉威
 * @version 1.0
 * @date 2022/9/28 上午10:11
 * @description 向mongo中存储日志
 */
public class WsAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {


    @Override
    protected void append(ILoggingEvent event) {
        WebSocketServer.sendAllMessage(WsResult.ofScriptLog(event.getFormattedMessage()));
    }


}