package club.xiaojiawei.ws;

import club.xiaojiawei.bean.WsResult;
import club.xiaojiawei.enums.ConfigurationEnum;
import club.xiaojiawei.enums.DeckEnum;
import club.xiaojiawei.enums.RunModeEnum;
import club.xiaojiawei.enums.WsResultTypeEnum;
import club.xiaojiawei.listener.WarCountListener;
import club.xiaojiawei.status.War;
import club.xiaojiawei.status.Work;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import javafx.beans.property.BooleanProperty;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicReference;

/**
 * WebScoket服务器
 * @author 肖嘉威
 * @date 2023/8/19 10:47
 */
@Slf4j
@Component
@ServerEndpoint(value = "/info")
@SuppressWarnings("all")
public class WebSocketServer{

    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;

    private static AtomicReference<BooleanProperty> isPause;
    private static Properties scriptConfiguration;
    @Resource
    public void setIsPause(AtomicReference<BooleanProperty> isPause) {
        WebSocketServer.isPause = isPause;
    }

    @Resource
    public void setScriptConfiguration(Properties scriptConfiguration) {
        WebSocketServer.scriptConfiguration = scriptConfiguration;
    }

    /**
     * session集合,存放对应的session
     */
    public static final CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<>();

    /**
     * 建立WebSocket连接
     * @param session
     */
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        webSocketSet.add(this);
        log.info(String.format("WebSocket建立连接完成，连接用户ID：【%s】，当前在线人数为：【%d】", this.session.getId(), webSocketSet.size()));
        sendInitMsg();
    }

    private void sendInitMsg(){
        DeckEnum currentDeck = DeckEnum.valueOf(scriptConfiguration.getProperty(ConfigurationEnum.DECK.getKey()));
        ArrayList<String> modes = new ArrayList<>();
        for (RunModeEnum modeEnum : RunModeEnum.values()) {
            if (modeEnum.isEnable()){
                modes.add(modeEnum.getComment());
            }
        }
        RemoteEndpoint.Basic remote = session.getBasicRemote();
//        发送顺序不要改变!
        try {
            remote.sendText(JSON.toJSONString(WsResult.ofNew(WsResultTypeEnum.LOG, "WebSocket连接成功")));
            remote.sendText(JSON.toJSONString(WsResult.ofNew(WsResultTypeEnum.PAUSE, isPause.get().get())));
            remote.sendText(JSON.toJSONString(WsResult.ofNew(WsResultTypeEnum.MODE_LIST, modes)));
            remote.sendText(JSON.toJSONString(WsResult.ofNew(WsResultTypeEnum.MODE, currentDeck.getRunMode().getComment())));
            remote.sendText(JSON.toJSONString(WsResult.ofNew(WsResultTypeEnum.DECK, currentDeck.getComment())));
            remote.sendText(JSON.toJSONString(WsResult.ofNew(WsResultTypeEnum.GAME_COUNT, War.warCount.get())));
            remote.sendText(JSON.toJSONString(WsResult.ofNew(WsResultTypeEnum.WINNING_PERCENTAGE, WarCountListener.getWinningPercentage())));
            remote.sendText(JSON.toJSONString(WsResult.ofNew(WsResultTypeEnum.WORK_DATE, new String[][]{Work.getWorkDayFlagArr(), Work.getWorkTimeFlagArr(), Work.getWorkTimeArr()})));
            remote.sendText(JSON.toJSONString(WsResult.ofNew(WsResultTypeEnum.GAME_TIME, War.gameTime.get())));
            remote.sendText(JSON.toJSONString(WsResult.ofNew(WsResultTypeEnum.EXP, War.exp.get())));
        } catch (IOException e) {
            log.error("ws发送异常", e);
        }
    }

    /**
     * 发生错误
     * @param throwable e
     */
    @OnError
    public void onError(Throwable throwable) {
        log.error("ws发生错误", throwable);
    }

    /**
     * 连接关闭
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);
        log.info(String.format("WebSocket连接断开，断开用户ID：【%s】，当前在线人数为：【%d】", this.session.getId(), webSocketSet.size()));
    }

    /**
     * 接收客户端消息
     *
     * @param message 接收的消息
     */
    @OnMessage
    public void onMessage(String message) {
        log.info(String.format("WebSocket收到用户ID【%s】发来的消息：【%d】", this.session.getId(), message));
    }

    /**
     * 群发消息
     */
    public static void sendAllMessage(WsResult wsResult) {
        for (WebSocketServer webSocket : webSocketSet) {
            try {
                webSocket.session.getBasicRemote().sendText(JSON.toJSONString(wsResult));
            } catch (IOException e) {
                log.error("WebSocket群发消息发生错误" , e);
            }
        }
    }

    public static void closeAll(){
        for (WebSocketServer webSocket : webSocketSet) {
            try {
                webSocket.session.close();
            } catch (IOException e) {
                log.error("WebSocket关闭连接发生错误" , e);
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


