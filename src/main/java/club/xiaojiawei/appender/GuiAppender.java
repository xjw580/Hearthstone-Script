package club.xiaojiawei.appender;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import club.xiaojiawei.controller.DashboardController;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.text.Text;


/**
 * @author 肖嘉威
 * @version 1.0
 * @date 2022/9/28 上午10:11
 * @description 向GUI客户端发送消息
 */
public class GuiAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    @Override
    protected void append(ILoggingEvent event) {
        if (DashboardController.logSwitchBack.statusProperty().get() && DashboardController.logVBoxBack != null && DashboardController.accordionBack!= null){
            Platform.runLater(() -> {
                ObservableList<Node> list = DashboardController.logVBoxBack.getChildren();
                Text text = new Text(event.getMessage());
                text.wrappingWidthProperty().bind(DashboardController.accordionBack.widthProperty().subtract(15));
                list.add(text);
//                大于两百条就清空,防止内存泄露和性能问题
                if (list.size() > 200){
                    list.clear();
                }
            });
        }
    }


}