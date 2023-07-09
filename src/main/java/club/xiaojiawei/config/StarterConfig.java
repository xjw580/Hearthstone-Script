package club.xiaojiawei.config;

import club.xiaojiawei.starter.AbstractStarter;
import club.xiaojiawei.starter.ClearStarter;
import club.xiaojiawei.starter.GameStarter;
import club.xiaojiawei.starter.PlatformStarter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author 肖嘉威
 * @date 2023/7/5 14:48
 * @msg
 */
@Configuration
public class StarterConfig {

    @Resource
    private ClearStarter clearStarter;
    @Resource
    private PlatformStarter platformStarter;
    @Resource
    private GameStarter gameStarter;

    @Bean
    public AbstractStarter starter(){
        clearStarter.setNextStarter(platformStarter).setNextStarter(gameStarter);
        return clearStarter;
    }
}
