package club.xiaojiawei.config;

import club.xiaojiawei.starter.*;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Starter的责任链配置
 * @author 肖嘉威
 * @date 2023/7/5 14:48
 */
@Configuration
public class StarterConfig {

    @Resource
    private ClearStarter clearStarter;
    @Resource
    private PlatformStarter platformStarter;
    @Resource
    private LoginPlatformStarter loginPlatformStarter;
    @Resource
    private GameStarter gameStarter;
    @Resource
    private LogListenStarter logListenStarter;

    @Bean
    public AbstractStarter starter(){
        clearStarter.setNextStarter(platformStarter)
                .setNextStarter(loginPlatformStarter)
                .setNextStarter(gameStarter)
                .setNextStarter(logListenStarter);
        return clearStarter;
    }
}
