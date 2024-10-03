package club.xiaojiawei.config

import club.xiaojiawei.enums.ModeEnum
import club.xiaojiawei.enums.WarPhaseEnum
import club.xiaojiawei.interfaces.ModeStrategy
import club.xiaojiawei.interfaces.PhaseStrategy
import club.xiaojiawei.strategy.AbstractPhaseStrategy
import jakarta.annotation.Resource
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order

/**
 * 为枚举常量的属性赋值
 * @author 肖嘉威
 * @date 2023/9/19 9:47
 */
@Configuration
@Order(250)
class InitConfig : ApplicationRunner {
    @Resource
    private val springContext: ConfigurableApplicationContext? = null

    override fun run(args: ApplicationArguments) {
        try {
            for (phase in WarPhaseEnum.entries) {
                val phaseStrategyClass: Class<out PhaseStrategy?> =
                    Class.forName(phase.getPhaseStrategyClassName()) as Class<out AbstractPhaseStrategy?>

                phase.phaseStrategy = springContext!!.getBean(phaseStrategyClass)
            }
            for (mode in ModeEnum.entries) {
                val modeStrategyClass = Class.forName(mode.getModeStrategyClassName()) as Class<out ModeStrategy<Any?>?>
                mode.modeStrategy = springContext!!.getBean(modeStrategyClass)
            }
        } catch (e: ClassNotFoundException) {
            throw RuntimeException(e)
        }
    }
}
