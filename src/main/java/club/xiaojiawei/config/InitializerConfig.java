package club.xiaojiawei.config;

import club.xiaojiawei.initializer.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;

import javax.annotation.Resource;

/**
 * Initializer的责任链配置
 * @author 肖嘉威
 * @date 2023/7/4 15:08
 */
@Configuration
@Order(520)
public class InitializerConfig implements ApplicationRunner {

    @Resource
    private LogInitializer logInitializer;
    @Resource
    private PathInitializer pathInitializer;
    @Resource
    private WebInitializer webInitializer;
    @Resource
    private DelTempInitializer delTempInitializer;
    @Resource
    @Lazy
    private AbstractInitializer initializer;
    /**
     * Initializer责任链头对象
     * @return AbstractInitializer
     */
    @Bean
    public AbstractInitializer initializer(){
        logInitializer.setNextInitializer(pathInitializer).setNextInitializer(webInitializer).setNextInitializer(delTempInitializer);
        return logInitializer;
    }

    @Override
    public void run(ApplicationArguments args){
        initializer.init();
    }
}
