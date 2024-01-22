package club.xiaojiawei.config;

import club.xiaojiawei.bean.WsResult;
import club.xiaojiawei.controller.JavaFXDashboardController;
import club.xiaojiawei.core.Core;
import club.xiaojiawei.enums.WsResultTypeEnum;
import club.xiaojiawei.status.Work;
import club.xiaojiawei.utils.SystemUtil;
import club.xiaojiawei.ws.WebSocketServer;
import jakarta.annotation.Resource;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 脚本暂停状态
 * @author 肖嘉威
 * @date 2023/7/5 15:04
 */
@Configuration
@Slf4j
public class PauseConfig {

    @Resource
    @Lazy
    private JavaFXDashboardController javafxDashboardController;

    @Resource
    @Lazy
    private Core core;

    @Bean
    public AtomicReference<BooleanProperty> isPause(){
        SimpleBooleanProperty booleanProperty = new SimpleBooleanProperty(true);
        booleanProperty.addListener((observable, oldValue, newValue) -> {
            javafxDashboardController.changeSwitch(newValue);
            WebSocketServer.sendAllMessage(WsResult.ofNew(WsResultTypeEnum.PAUSE, newValue));
            if (newValue){
                SystemUtil.cancelAllRunnable();
                Work.setWorking(false);
            }else {
                if (Work.isDuringWorkDate()){
                    core.start();
                }else {
                    Work.cannotWorkLog();
                }
            }
            log.info("当前处于" + (newValue? "停止" : "运行") + "状态");
        });
        return new AtomicReference<>(booleanProperty);
    }

}
