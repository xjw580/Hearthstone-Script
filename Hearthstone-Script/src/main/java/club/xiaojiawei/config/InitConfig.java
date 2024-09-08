package club.xiaojiawei.config;

import club.xiaojiawei.enums.ModeEnum;
import club.xiaojiawei.enums.WarPhaseEnum;
import club.xiaojiawei.interfaces.ModeStrategy;
import club.xiaojiawei.interfaces.PhaseStrategy;
import club.xiaojiawei.strategy.AbstractPhaseStrategy;
import jakarta.annotation.Resource;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;


/**
 * 为枚举常量的属性赋值
 * @author 肖嘉威
 * @date 2023/9/19 9:47
 */
@Configuration
@Order(250)
public class InitConfig implements ApplicationRunner {

    @Resource
    private ConfigurableApplicationContext springContext;

    @Override
    public void run(ApplicationArguments args) {
        try {
            for (WarPhaseEnum phase : WarPhaseEnum.values()) {
                Class<? extends PhaseStrategy> phaseStrategyClass = (Class<? extends AbstractPhaseStrategy>) Class.forName(phase.getPhaseStrategyClassName());;
                phase.setPhaseStrategy(springContext.getBean(phaseStrategyClass));
            }
            for (ModeEnum mode : ModeEnum.values()) {
                Class<? extends ModeStrategy<Object>> modeStrategyClass = (Class<? extends ModeStrategy<Object>>) Class.forName(mode.getModeStrategyClassName());
                mode.setModeStrategy(springContext.getBean(modeStrategyClass));
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
