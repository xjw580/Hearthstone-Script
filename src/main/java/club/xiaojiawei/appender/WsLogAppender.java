package club.xiaojiawei.appender;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import club.xiaojiawei.entity.WsResult;
import club.xiaojiawei.enums.WsResultTypeEnum;
import club.xiaojiawei.ws.WebSocketServer;

/**
 * @author 肖嘉威
 * @date 2023/9/11 22:29
 * @msg
 */
public class WsLogAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {
    @Override
    protected void append(ILoggingEvent event) {
        WebSocketServer.sendAllMessage(WsResult.ofNew(WsResultTypeEnum.LOG, event.getFormattedMessage()));
    }

}