package club.xiaojiawei.initializer;

import club.xiaojiawei.data.SpringData;
import club.xiaojiawei.utils.SystemUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Properties;

import static club.xiaojiawei.enums.ConfigurationEnum.AUTO_OPEN_WEB;

/**
 * 打开Web页面
 * @author 肖嘉威
 * @date 2023/9/10 15:33
 */
@Component
@Slf4j
public class WebInitializer extends AbstractInitializer{
    @Resource
    private SpringData springData;
    @Resource
    private Properties scriptConfiguration;
    @Override
    public void exec(){
        if (Objects.equals(scriptConfiguration.getProperty(AUTO_OPEN_WEB.getKey()), "true")){
            log.info("打开Web界面");
            SystemUtil.openUrlByBrowser("http://127.0.0.1:" + springData.getServerPort());
        }else {
            log.info("自动打开Web界面开关处于关闭状态");
        }
        if (nextInitializer != null){
            nextInitializer.init();
        }
    }
}
