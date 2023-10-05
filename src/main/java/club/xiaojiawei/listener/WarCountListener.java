package club.xiaojiawei.listener;

import club.xiaojiawei.bean.WsResult;
import club.xiaojiawei.controller.JavaFXDashboardController;
import club.xiaojiawei.enums.WsResultTypeEnum;
import club.xiaojiawei.status.War;
import club.xiaojiawei.ws.WebSocketServer;
import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author 肖嘉威
 * @date 2023/9/11 16:49
 * @msg 监听局数
 */
@Component
@Slf4j
public class WarCountListener {

    @Resource
    private JavaFXDashboardController javaFXDashboardController;
    @Getter
    private static String winningPercentage = "?";
    @PostConstruct
    void init(){
        War.warCount.addListener((observable, oldValue, newValue) -> {
            log.info("已完成第 " + newValue + " 把游戏");
            setJavaFXGUI(newValue);
            sendWSMsg(newValue);
        });
    }
    private void setJavaFXGUI(Number warCount){
        javaFXDashboardController.getGameCount().setText(warCount.toString());
        javaFXDashboardController.getWinningPercentage().setText(winningPercentage = String.format("%.0f", War.winCount.get() / warCount.doubleValue() * 100) + "%");
    }
    private void sendWSMsg(Number warCount){
        WebSocketServer.sendAllMessage(WsResult.ofNew(WsResultTypeEnum.GAME_COUNT, warCount.toString()));
        WebSocketServer.sendAllMessage(WsResult.ofNew(WsResultTypeEnum.WINNING_PERCENTAGE, WarCountListener.getWinningPercentage()));
        WebSocketServer.sendAllMessage(WsResult.ofNew(WsResultTypeEnum.GAME_TIME, War.gameTime.get()));
        WebSocketServer.sendAllMessage(WsResult.ofNew(WsResultTypeEnum.EXP, War.exp.get()));
    }
}
