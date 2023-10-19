package club.xiaojiawei.listener;

import club.xiaojiawei.bean.WsResult;
import club.xiaojiawei.controller.JavaFXDashboardController;
import club.xiaojiawei.enums.WsResultTypeEnum;
import club.xiaojiawei.status.War;
import club.xiaojiawei.ws.WebSocketServer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * 游戏局数监听器
 * @author 肖嘉威
 * @date 2023/9/11 16:49
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
        javaFXDashboardController.getGameTime().setText(getTimeStr(War.gameTime.get()));
        javaFXDashboardController.getExp().setText(String.valueOf(War.exp.get()));
    }

    private static String getTimeStr(int time) {
        String timeStr;
        if (time == 0){
            timeStr = String.format("%d", time);
        } else if (time < 60){
            timeStr = String.format("%dm", time);
        }else if (time < 1440){
            if (time % 60 == 0){
                timeStr = String.format("%dh", time / 60);
            }else {
                timeStr = String.format("%dh%dm", time / 60, time % 60);
            }
        }else {
            if (time % 1440 == 0){
                timeStr = String.format("%dd", time / 1440);
            }else {
                timeStr = String.format("%dd%dh", time / 1440, time % 1440 / 60);
            }
        }
        return timeStr;
    }

    private void sendWSMsg(Number warCount){
        WebSocketServer.sendAllMessage(WsResult.ofNew(WsResultTypeEnum.GAME_COUNT, warCount.toString()));
        WebSocketServer.sendAllMessage(WsResult.ofNew(WsResultTypeEnum.WINNING_PERCENTAGE, WarCountListener.getWinningPercentage()));
        WebSocketServer.sendAllMessage(WsResult.ofNew(WsResultTypeEnum.GAME_TIME, War.gameTime.get()));
        WebSocketServer.sendAllMessage(WsResult.ofNew(WsResultTypeEnum.EXP, War.exp.get()));
    }
}
