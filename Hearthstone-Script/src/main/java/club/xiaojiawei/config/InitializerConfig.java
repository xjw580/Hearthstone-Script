package club.xiaojiawei.config;

import club.xiaojiawei.initializer.AbstractInitializer;
import club.xiaojiawei.interfaces.Chain;
import jakarta.annotation.Resource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Comparator;
import java.util.List;
import java.util.Map;


/**
 * Initializer的责任链配置
 * @author 肖嘉威
 * @date 2023/7/4 15:08
 */
@Configuration
public class InitializerConfig {

    @Resource
    private ApplicationContext applicationContext;

    /**
     * Initializer责任链头对象
     * @return AbstractInitializer
     */
    @Bean
    public AbstractInitializer initializer(){
        Map<String, AbstractInitializer> map = applicationContext.getBeansOfType(AbstractInitializer.class);
        List<AbstractInitializer> list = map.values().stream().sorted(Comparator.comparingInt(Chain::getOrder)).toList();
        for (int i = list.size() - 1; i > 0;) {
            list.get(i).setNextInitializer(list.get(--i));
        }
        return list.getLast();
    }

}
