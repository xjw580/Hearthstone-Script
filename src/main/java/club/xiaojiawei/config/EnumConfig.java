package club.xiaojiawei.config;

import club.xiaojiawei.enums.DeckEnum;
import club.xiaojiawei.enums.ModeEnum;
import club.xiaojiawei.enums.WarPhaseEnum;
import club.xiaojiawei.strategy.AbstractDeckStrategy;
import club.xiaojiawei.strategy.AbstractModeStrategy;
import club.xiaojiawei.strategy.AbstractPhaseStrategy;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import javax.annotation.Resource;

/**
 * @author 肖嘉威
 * @date 2023/9/19 9:47
 * @msg 为枚举常量的属性赋值
 */
@Configuration
@Order(250)
public class EnumConfig implements ApplicationRunner {
    @Resource
    private ConfigurableApplicationContext springContext;
    @Override
    public void run(ApplicationArguments args) {
        for (WarPhaseEnum phase : WarPhaseEnum.values()) {
            Class<? extends AbstractPhaseStrategy> phaseStrategyClass = phase.getPhaseStrategyClass();
            if (phaseStrategyClass != null){
                phase.setAbstractPhaseStrategy(springContext.getBean(phaseStrategyClass));
            }
        }
        for (ModeEnum mode : ModeEnum.values()) {
            Class<? extends AbstractModeStrategy<Object>> modeStrategyClass = mode.getModeStrategyClass();
            if (modeStrategyClass != null){
                mode.setAbstractModeStrategy(springContext.getBean(modeStrategyClass));
            }
        }
        for (DeckEnum deck : DeckEnum.values()) {
            Class<? extends AbstractDeckStrategy> deckStrategyClass = deck.getAbstractDeckStrategyClass();
            if (deckStrategyClass != null){
                deck.setAbstractDeckStrategy(springContext.getBean(deckStrategyClass));
            }
        }
    }
}
