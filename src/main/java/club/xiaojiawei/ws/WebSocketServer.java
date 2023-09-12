package club.xiaojiawei.ws;

import club.xiaojiawei.entity.WsResult;
import club.xiaojiawei.enums.WsResultTypeEnum;
import com.alibaba.fastjson.JSON;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author 肖嘉威
 * @date 2023/8/19 10:47
 * @since
 */
@Slf4j
@Component
@ServerEndpoint("/info")    // 指定websocket 连接的url
@SuppressWarnings("all")
public class WebSocketServer {

    // 与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
    // session集合,存放对应的session
    public static final CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<>();

    /**
     * 建立WebSocket连接
     * @param session
     */
    @SneakyThrows
    @OnOpen
    public void onOpen(Session session) {
        log.info("WebSocket建立连接中,连接用户ID：{}", session.getId());
        // 建立连接
        this.session = session;
        webSocketSet.add(this);
        log.info("WebSocket建立连接完成,当前在线人数为：{}", webSocketSet.size());
        session.getBasicRemote().sendText(JSON.toJSONString(WsResult.ofNew(WsResultTypeEnum.LOG, "WebSocket连接成功")));
    }

    /**
     * 发生错误
     *
     * @param throwable e
     */
    @OnError
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    /**
     * 连接关闭
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);
        log.info("WebSocket连接断开,当前在线人数为：{}", webSocketSet.size());
    }

    /**
     * 接收客户端消息
     *
     * @param message 接收的消息
     */
    @OnMessage
    public void onMessage(String message) {
        log.info("WebSocket收到客户端发来的消息：{}", message);
    }


    /**
     * 群发消息
     */
    public static void sendAllMessage(WsResult wsResult) {
        for (WebSocketServer webSocket : webSocketSet) {
            try {
                webSocket.session.getBasicRemote().sendText(JSON.toJSONString(wsResult));
            } catch (IOException e) {
                log.error("WebSocket群发消息发生错误：" + e.getMessage(), e);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WebSocketServer that = (WebSocketServer) o;
        return Objects.equals(session, that.session);
    }

    @Override
    public int hashCode() {
        return Objects.hash(session);
    }
}


