package club.xiaojiawei.config;

import club.xiaojiawei.listener.log.AbstractLogListener;
import club.xiaojiawei.listener.log.DeckLogListener;
import club.xiaojiawei.listener.log.PowerLogListener;
import club.xiaojiawei.listener.log.ScreenLogListener;
import club.xiaojiawei.starter.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author 肖嘉威
 * @date 2023/7/5 14:48
 * @msg
 */
@Configuration
public class LogListenerConfig {

    @Resource
    private DeckLogListener deckLogListener;
    @Resource
    private PowerLogListener powerLogListener;
    @Resource
    private ScreenLogListener screenLogListener;

    @Bean
    public AbstractLogListener logListener(){
        deckLogListener.setNextLogListener(screenLogListener).setNextLogListener(powerLogListener);
        return deckLogListener;
    }
}
