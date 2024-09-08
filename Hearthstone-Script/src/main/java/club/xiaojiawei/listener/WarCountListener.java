package club.xiaojiawei.listener;

import club.xiaojiawei.bean.WsResult;
import club.xiaojiawei.controller.javafx.MainController;
import club.xiaojiawei.enums.WsResultTypeEnum;
import club.xiaojiawei.status.War;
import club.xiaojiawei.ws.WebSocketServer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 游戏局数监听器
 * @author 肖嘉威
 * @date 2023/9/11 16:49
 */
@Component
@Slf4j
public class WarCountListener {

    @Resource
    private MainController javafxMainController;
    @Getter
    private static String winningPercentage = "?";

    @PostConstruct
    void init(){
        War.WAR_COUNT.addListener((observable, oldValue, newValue) -> {
            log.info("已完成第 " + newValue + " 把游戏");
            setJavaFXGUI(newValue);
            sendWSMsg(newValue);
        });
    }

    private void setJavaFXGUI(Number warCount){
        javafxMainController.getGameCount().setText(warCount.toString());
        javafxMainController.getWinningPercentage().setText(winningPercentage = String.format("%.0f", War.WIN_COUNT.get() / warCount.doubleValue() * 100) + "%");
        javafxMainController.getGameTime().setText(formatTime(War.GAME_TIME.get()));
        javafxMainController.getExp().setText(String.valueOf(War.EXP.get()));
    }

    private static String formatTime(int time) {
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
        WebSocketServer.sendAllMessage(WsResult.ofNew(WsResultTypeEnum.GAME_TIME, War.GAME_TIME.get()));
        WebSocketServer.sendAllMessage(WsResult.ofNew(WsResultTypeEnum.EXP, War.EXP.get()));
    }
}
