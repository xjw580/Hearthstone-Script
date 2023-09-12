package club.xiaojiawei.config;

import club.xiaojiawei.initializer.AbstractInitializer;
import club.xiaojiawei.initializer.LogInitializer;
import club.xiaojiawei.initializer.WebInitializer;
import club.xiaojiawei.initializer.PathInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author 肖嘉威
 * @date 2023/7/4 15:08
 * @msg Initializer的责任链配置
 */
@Configuration
public class InitializerConfig {

    @Resource
    private LogInitializer logInitializer;
    @Resource
    private PathInitializer pathInitializer;
    @Resource
    private WebInitializer webInitializer;

    /**
     * Initializer责任链头对象
     * @return AbstractInitializer
     */
    @Bean
    public AbstractInitializer initializer(){
        logInitializer.setNextInitializer(pathInitializer).setNextInitializer(webInitializer);
        return logInitializer;
    }

}
