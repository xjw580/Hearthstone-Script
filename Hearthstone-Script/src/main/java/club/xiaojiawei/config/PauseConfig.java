package club.xiaojiawei.config;

import club.xiaojiawei.bean.WsResult;
import club.xiaojiawei.controller.javafx.MainController;
import club.xiaojiawei.core.Core;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.dll.SystemDll;
import club.xiaojiawei.enums.WsResultTypeEnum;
import club.xiaojiawei.status.Work;
import club.xiaojiawei.utils.SystemUtil;
import club.xiaojiawei.ws.WebSocketServer;
import com.sun.jna.platform.win32.WinDef;
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
    private MainController javafxMainController;

    @Resource
    @Lazy
    private Core core;

    @Bean
    public AtomicReference<BooleanProperty> isPause(){
        SimpleBooleanProperty booleanProperty = new SimpleBooleanProperty(true);
        booleanProperty.addListener((observable, oldValue, newValue) -> {
            javafxMainController.changeSwitch(newValue);
            WebSocketServer.sendAllMessage(WsResult.ofNew(WsResultTypeEnum.PAUSE, newValue));
            if (newValue){
                SystemDll.INSTANCE.changeInput(ScriptStaticData.getGameHWND(), false);
                SystemDll.INSTANCE.changeWindow(ScriptStaticData.getGameHWND(), false);
                SystemUtil.closeAll();
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
