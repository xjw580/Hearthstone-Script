package club.xiaojiawei.hearthstone.ws;

import club.xiaojiawei.hearthstone.constant.SystemConst;
import club.xiaojiawei.hearthstone.entity.WsResult;
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
 * @date 2022/12/4 11:30
 */
@Slf4j
@Component
@ServerEndpoint("/info")    // 指定websocket 连接的url
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
        log.info("WS建立连接中,连接用户ID：{}", session.getId());
        // 建立连接
        this.session = session;
        webSocketSet.add(this);
        log.info("WS建立连接完成,当前在线人数为：{}", webSocketSet.size());
        session.getBasicRemote().sendText(JSON.toJSONString(WsResult.ofScriptLog("WS连接成功，按 Alt+P 关闭脚本，按 Ctrl+P 暂停脚本" + SystemConst.REST_TIME + "s")));
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
        log.info("WS连接断开,当前在线人数为：{}", webSocketSet.size());
    }

    /**
     * 接收客户端消息
     *
     * @param message 接收的消息
     */
    @OnMessage
    public void onMessage(String message) {
        log.info("WS收到客户端发来的消息：{}", message);
    }


    /**
     * 群发消息
     */
    public static void sendAllMessage(WsResult wsResult) {
        for (WebSocketServer webSocket : webSocketSet) {
            try {
                webSocket.session.getBasicRemote().sendText(JSON.toJSONString(wsResult));
            } catch (IOException e) {
                log.error("WS群发消息发生错误：" + e.getMessage(), e);
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

