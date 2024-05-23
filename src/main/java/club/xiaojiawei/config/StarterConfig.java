package club.xiaojiawei.config;

import club.xiaojiawei.initializer.AbstractInitializer;
import club.xiaojiawei.interfaces.Chain;
import club.xiaojiawei.starter.*;
import jakarta.annotation.Resource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Starter的责任链配置
 * @author 肖嘉威
 * @date 2023/7/5 14:48
 */
@Configuration
public class StarterConfig {

    @Resource
    private ApplicationContext applicationContext;

    @Bean
    public AbstractStarter starter(){
        Map<String, AbstractStarter> map = applicationContext.getBeansOfType(AbstractStarter.class);
        List<AbstractStarter> list = map.values().stream().sorted(Comparator.comparingInt(Chain::getOrder)).toList();
        for (int i = list.size() - 1; i > 0;) {
            list.get(i).setNextStarter(list.get(--i));
        }
        return list.getLast();
    }

}
