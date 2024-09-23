package club.xiaojiawei.appender;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import club.xiaojiawei.bean.WsResult;
import club.xiaojiawei.controller.javafx.MainController;
import club.xiaojiawei.controls.CopyLabel;
import club.xiaojiawei.controls.ico.CopyIco;
import club.xiaojiawei.controls.ico.OKIco;
import club.xiaojiawei.enums.WsResultTypeEnum;
import club.xiaojiawei.utils.SystemUtil;
import club.xiaojiawei.ws.WebSocketServer;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

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
        if (MainController.getStaticLogVBox() != null && MainController.getStaticAccordion()!= null){
            Platform.runLater(() -> {
                ObservableList<Node> list = MainController.getStaticLogVBox().getChildren();
                //                大于二百五条就清空,防止内存泄露和性能问题
                if (list.size() > 250){
                    list.clear();
                }
                CopyLabel label = new CopyLabel();
                label.setNotificationManager(MainController.getStaticNotificationManger());
                label.setStyle("-fx-wrap-text: true");
                label.prefWidthProperty().bind(MainController.getStaticAccordion().widthProperty().subtract(15));

                int levelInt = event.getLevel().levelInt;
                String message = event.getMessage();
//                处理需要复制的文本
                if (message != null && message.startsWith("$")) {
                    message = message.substring(1);
                    label.setText(message);
                    label.getStyleClass().add("copyLog");
                    AnchorPane anchorPane = wrapLabel(label);
                    list.add(anchorPane);
                    return;
                }
                /*为日志上颜色*/
                if (event.getThrowableProxy() == null && levelInt <= Level.INFO_INT){
                    label.setText(message);
                }else if (levelInt <= Level.WARN_INT){
                    label.setText(message);
                    label.getStyleClass().add("warnLog");
                }else {
                    label.setText(message + "，查看脚本日志获取详细错误信息");
                    label.getStyleClass().add("errorLog");
                }
                list.add(label);
            });
        }
    }

    private AnchorPane wrapLabel(Label label){
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getStyleClass().add("hoverRootNode");
        Node node = buildCopyNode(() -> SystemUtil.copyToClipboard(label.getText()));
        node.getStyleClass().add("hoverChildrenNode");
        anchorPane.getChildren().add(label);
        anchorPane.getChildren().add(node);
        AnchorPane.setRightAnchor(node, 5D);
        AnchorPane.setTopAnchor(node, 5D);
        return anchorPane;
    }

    private Node buildCopyNode(Runnable clickHandler){
        Label graphicLabel = new Label();
        CopyIco copyIco = new CopyIco();
        String icoColor = "#e4e4e4";
        copyIco.setColor(icoColor);
        graphicLabel.setGraphic(copyIco);
        graphicLabel.setStyle("""
                            -fx-cursor: hand;
                            -fx-alignment: CENTER;
                            -fx-pref-width: 22;
                            -fx-pref-height: 22;
                            -fx-background-radius: 3;
                            -fx-background-color: rgba(128,128,128,0.9);
                            -fx-font-size: 10;
                            """);
        graphicLabel.setOnMouseClicked(actionEvent -> {
            if (clickHandler != null){
                clickHandler.run();
            }
            OKIco okIco = new OKIco();
            okIco.setColor(icoColor);
            graphicLabel.setGraphic(okIco);
            PauseTransition pauseTransition = new PauseTransition(Duration.millis(1000));
            pauseTransition.setOnFinished(actionEvent1 -> {
                graphicLabel.setGraphic(copyIco);
            });
            pauseTransition.play();
        });
        return graphicLabel;
    }

    private void appendWebSocket(ILoggingEvent event){
        WebSocketServer.sendAllMessage(WsResult.ofNew(WsResultTypeEnum.LOG, event.getFormattedMessage()));
    }

}