package club.xiaojiawei.config;

import club.xiaojiawei.controller.DashboardController;
import club.xiaojiawei.core.Core;
import club.xiaojiawei.utils.SystemUtil;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.annotation.Resource;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author 肖嘉威
 * @date 2023/7/5 15:04
 * @msg
 */
@Configuration
@Slf4j
public class DataConfig {

    @Resource
    @Lazy
    private DashboardController dashboardController;
    @Resource
    @Lazy
    private Core core;
    /**
     * 脚本是否处于暂停中
     * @return
     */
    @Bean
    public AtomicReference<BooleanProperty> isPause(){
        SimpleBooleanProperty booleanProperty = new SimpleBooleanProperty(true);
        booleanProperty.addListener((observable, oldValue, newValue) -> {
            log.info("当前处于" + (newValue? "停止" : "运行") + "状态");
            dashboardController.changeSwitch(newValue);
            if (!newValue){
                core.start();
            }else {
                SystemUtil.cancelAll();
            }
        });
        return new AtomicReference<>(booleanProperty);
    }

}
