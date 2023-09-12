package club.xiaojiawei.initializer;

import club.xiaojiawei.data.SpringData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Properties;

import static club.xiaojiawei.data.ScriptStaticData.ROBOT;
import static club.xiaojiawei.enums.ConfigurationKeyEnum.AUTO_OPEN_KEY;

/**
 * @author 肖嘉威
 * @date 2023/9/10 15:33
 * @msg
 */
@Component
@Slf4j
public class WebInitializer extends AbstractInitializer{
    @Resource
    private SpringData springData;
    @Resource
    private Properties scriptProperties;
    @Override
    public void exec(){
        if (Objects.equals(scriptProperties.getProperty(AUTO_OPEN_KEY.getKey()), "true")){
            // 获取系统默认的浏览器
            Desktop desktop = Desktop.getDesktop();
            // 判断桌面是否支持浏览器调用
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                // 调用默认浏览器打开网页
                try {
                    desktop.browse(new URI("http://127.0.0.1:" + springData.getServerPort()));
                } catch (IOException | URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if (nextInitializer != null){
            nextInitializer.init();
        }
    }
}
