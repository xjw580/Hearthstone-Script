package club.xiaojiawei.appender;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import club.xiaojiawei.bean.WsResult;
import club.xiaojiawei.controller.JavaFXDashboardController;
import club.xiaojiawei.enums.WsResultTypeEnum;
import club.xiaojiawei.ws.WebSocketServer;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.text.Text;


/**
 * 额外的日志Appender
 * @author 肖嘉威 xjw580@qq.com
 * @date 2022/9/28 上午10:11
 */
public class ExtraLogAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    @Override
    protected void append(ILoggingEvent event) {
        if (event.getLevel().levelInt >= Level.INFO_INT){
            appendJavaFX(event);
            appendWebSocket(event);
        }
    }

    @SuppressWarnings("all")
    private void appendJavaFX(ILoggingEvent event){
        if (JavaFXDashboardController.getStaticLogVBox() != null && JavaFXDashboardController.getStaticAccordion()!= null){
            Platform.runLater(() -> {
                ObservableList<Node> list = JavaFXDashboardController.getStaticLogVBox().getChildren();
                //                大于二百五条就清空,防止内存泄露和性能问题
                if (list.size() > 250){
                    list.clear();
                }
                Text text;
                int levelInt = event.getLevel().levelInt;
                /*为日志上颜色*/
                if (event.getThrowableProxy() == null && levelInt <= Level.INFO_INT){
                    text = new Text(event.getMessage());
                }else if (levelInt <= Level.WARN_INT){
                    text = new Text(event.getMessage());
                    text.getStyleClass().add("warn");
                }else {
                    text = new Text(event.getMessage() + "，查看脚本日志获取详细错误信息");
                    text.getStyleClass().add("error");
                }
                text.wrappingWidthProperty().bind(JavaFXDashboardController.getStaticAccordion().widthProperty().subtract(15));
                list.add(text);
            });
        }
    }
    private void appendWebSocket(ILoggingEvent event){
        WebSocketServer.sendAllMessage(WsResult.ofNew(WsResultTypeEnum.LOG, event.getFormattedMessage()));
    }

}