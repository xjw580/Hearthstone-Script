package club.xiaojiawei.config

import club.xiaojiawei.starter.*
import jakarta.annotation.Resource
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.function.ToIntFunction

/**
 * Starter的责任链配置
 * @author 肖嘉威
 * @date 2023/7/5 14:48
 */
object StarterConfig {

    val starter: AbstractStarter = ClearStarter
        .setNextStarter(PlatformStarter)
        .setNextStarter(LoginPlatformStarter)
        .setNextStarter(GameStarter)
        .setNextStarter(InjectStarter)
        .setNextStarter(LogListenStarter)
        .setNextStarter(ExceptionListenStarter)

}
